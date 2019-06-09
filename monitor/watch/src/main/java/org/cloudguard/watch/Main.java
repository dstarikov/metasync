package org.cloudguard.watch;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.cloudguard.crypto.AESEncryptUtil;
import org.cloudguard.crypto.CryptoUtil;
import org.cloudguard.crypto.FileEncryptUtil;
import org.cloudguard.crypto.RSAEncryptUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidCipherTextException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full file path for input file:");
        String inFile = scanner.nextLine();
        System.out.println("Enter full file path for encrypted output file:");
        String encrypted = scanner.nextLine();
        System.out.println("Enter full file path for decrypted output file:");
        String decrypted = scanner.nextLine();

        CryptoUtil.init();
        byte[] aesKey = AESEncryptUtil.generateKey();
        KeyPair keyPair = RSAEncryptUtil.generateKey();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        List<String> usernames = new ArrayList<>();
        usernames.add("username");
        List<PublicKey> publicKeys = new ArrayList<>();
        publicKeys.add(publicKey);

        FileEncryptUtil.encrypt(
                new RandomAccessFile(inFile, "r"),
                new RandomAccessFile(encrypted, "rw"),
                aesKey, usernames, publicKeys);

        FileEncryptUtil.decrypt(new RandomAccessFile(encrypted, "r"),
                new RandomAccessFile(decrypted, "rw"),
                "username", privateKey);
    }
}
