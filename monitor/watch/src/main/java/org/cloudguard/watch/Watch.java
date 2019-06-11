package org.cloudguard.watch;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;
import org.cloudguard.crypto.AESEncryptUtil;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.FileEncryptUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

/**
 * Based on example from
 * https://howtodoinjava.com/java8/java-8-watchservice-api-tutorial/
 */
public class Watch {
    private final WatchService watcher;
    private final ConcurrentMap<WatchKey, Path> keys;
    private final String inDir;
    private final String outDir;
    private final boolean encryption;
    private final List<String> usernames;
    private final List<PublicKey> publicKeys;
    private final PrivateKey privateKey;

//    @Override
//    public void run() {
//        try {
//            System.out.println("run here");
//            walkAndRegisterDirectories(Paths.get(this.inDir));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public Watch (String inDir, String outDir, boolean encryption, List<String> usernames,
          List<PublicKey> publicKeys, PrivateKey privateKey) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new ConcurrentHashMap<>();
        this.inDir = inDir;
        this.outDir = outDir;
        this.encryption = encryption;
        this.usernames = usernames;
        this.publicKeys = publicKeys;
        this.privateKey = privateKey;

        walkAndRegisterDirectories(Paths.get(this.inDir));
    }

    private void copyFile(File from, File to) throws IOException {
        FileUtils.copyFile(from, to);
    }

    /**
     * Register the given directory with the WatchService; This function will be called by FileVisitor
     */
    private void registerDirectory(Path dir) throws IOException
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void walkAndRegisterDirectories(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                try {
                    @SuppressWarnings("rawtypes")
                    WatchEvent.Kind kind = event.kind();

                    // Context for directory entry event is the file name of entry
                    @SuppressWarnings("unchecked")
                    Path name = ((WatchEvent<Path>)event).context();
                    Path child = dir.resolve(name);

                    // print out event
                    String relative = new File(this.inDir).toURI().relativize(child.toFile().toURI()).getPath();


                    // if directory is created, and watching recursively, then register it and its sub-directories
                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child)) {
                                walkAndRegisterDirectories(child);
                            }
                        } catch (IOException x) {
                            // do something useful
                        }
                    }

                    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                        // copy file
                        String outFilePath = this.outDir + "/" + relative;
                        System.out.println(outFilePath);
                        System.out.println();

                        if (Files.isDirectory(child)) {
                            // TODO
//                            FileUtils.copyDirectory(child.toFile(), new File(outFilePath));
                        } else {
                            if (encryption) {
                                byte[] aesKey = AESEncryptUtil.generateKey();
                                FileEncryptUtil.encrypt(
                                        new RandomAccessFile(child.toFile(), "r"),
                                        new RandomAccessFile(outFilePath, "rw"),
                                        aesKey, usernames, publicKeys);
                            } else {
                                FileEncryptUtil.decrypt(new RandomAccessFile(child.toFile(), "r"),
                                        new RandomAccessFile(outFilePath, "rw"),
                                        "username", privateKey);
                            }
//                            this.copyFile(child.toFile(), new File(outFilePath));
                        }
                    }

                    if (kind == ENTRY_DELETE) {
                        String outFilePath = this.outDir + "/" + relative;
                        System.out.println(outFilePath);
                        System.out.println();

                        if (Files.isDirectory(child))
                            FileUtils.deleteDirectory(new File(outFilePath));
                        else
                            FileUtils.forceDelete(new File(outFilePath));

                    }

//                    System.out.format("%s: %s\n", event.kind().name(), child);
                    System.out.format("%s: %s\n", event.kind().name(), relative);
                    System.out.println("time = " + new Date().getTime());
                } catch (Exception e) {

                }


            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }



}