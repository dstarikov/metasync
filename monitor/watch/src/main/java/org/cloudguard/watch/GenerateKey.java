package org.cloudguard.watch;

import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class GenerateKey {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full file path for public key file:");
        String pub = scanner.nextLine();
        System.out.println("Enter full file path for private key file:");
        String pri = scanner.nextLine();

        CryptoUtil.init();
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        RandomAccessFile randomAccessFile = new RandomAccessFile(pub, "rw");
        randomAccessFile.writeUTF(RSAEncryptUtil.getKeyAsString(publicKey));
        randomAccessFile.close();
        randomAccessFile = new RandomAccessFile(pri, "rw");
        randomAccessFile.writeUTF(RSAEncryptUtil.getKeyAsString(privateKey));
        randomAccessFile.close();
    }
}
