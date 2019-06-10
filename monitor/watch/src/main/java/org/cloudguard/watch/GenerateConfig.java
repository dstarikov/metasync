package org.cloudguard.watch;

import com.google.gson.Gson;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class GenerateConfig {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String workingDir = System.getProperty("user.dir");


        CryptoUtil.init();
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        Config config = new Config(RSAEncryptUtil.getKeyAsString(keyPair.getPublic()),
                RSAEncryptUtil.getKeyAsString(keyPair.getPrivate()),
                workingDir + "/" + "in",
                workingDir + "/" + "encrypted",
                workingDir + "/" + "decrypted");

        Gson gson = new Gson();

        RandomAccessFile randomAccessFile = new RandomAccessFile("config.json", "rw");
        randomAccessFile.write(gson.toJson(config).getBytes());
        randomAccessFile.close();

        Config config1 = gson.fromJson(new FileReader("config.json"), Config.class);
        System.out.println(config.equals(config1));
    }
}
