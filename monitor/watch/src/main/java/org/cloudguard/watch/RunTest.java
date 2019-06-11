package org.cloudguard.watch;

import org.apache.commons.io.FileUtils;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class RunTest {
    public static void main(String[] args) throws
            IOException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter full file path for public key file:");
        String pub = scanner.nextLine();
        System.out.println("Enter full file path for private key file:");
        String pri = scanner.nextLine();

        CryptoUtil.init();
        RandomAccessFile randomAccessFile = new RandomAccessFile(pub, "rw");
        PublicKey publicKey = RSAEncryptUtil.getPublicKeyFromString(randomAccessFile.readUTF());
        randomAccessFile.close();
        randomAccessFile = new RandomAccessFile(pri, "rw");
        PrivateKey privateKey = RSAEncryptUtil.getPrivateKeyFromString(randomAccessFile.readUTF());
        randomAccessFile.close();

        randomAccessFile = new RandomAccessFile(pub, "rw");
        randomAccessFile.writeUTF(RSAEncryptUtil.getKeyAsString(publicKey));
        randomAccessFile.close();
        randomAccessFile = new RandomAccessFile(pri, "rw");
        randomAccessFile.writeUTF(RSAEncryptUtil.getKeyAsString(privateKey));
        randomAccessFile.close();

//        System.out.println("Enter full file path for test dir:");
//        String test = scanner.nextLine();
        System.out.println("Enter full file path for plaintext dir:");
        String plaintext = scanner.nextLine();
        System.out.println("Enter full file path for cloud dir:");
        String cloud = scanner.nextLine();

        System.out.println("Upload or Download [U/D]");
        String ud = scanner.nextLine();

        List<String> usernames = new ArrayList<>();
        usernames.add("username");
        List<PublicKey> publicKeys = new ArrayList<>();
        publicKeys.add(publicKey);

        System.out.println("Press enter to continue");
        scanner.nextLine();
        System.out.println("Test begins in 10 seconds");
        boolean upload = ud.startsWith("U") || ud.startsWith("u");
        TimeUnit.SECONDS.sleep(10);
        if (upload) {
            // copy files one by one from [plaintext] to [cloud]
           Watch watch = new Watch(plaintext, cloud, true, usernames, publicKeys, privateKey);
           watch.processEvents();
        } else {
            // copy files one by one from [cloud] to [plaintext]
            Watch watch = new Watch(cloud, plaintext, false, usernames, publicKeys, privateKey);
            watch.processEvents();
        }
    }

    private static void listFiles(File folder) {
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }
}
