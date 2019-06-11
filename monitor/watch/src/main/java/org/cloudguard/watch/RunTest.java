package org.cloudguard.watch;

import org.bouncycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.Scanner;

public class RunTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidCipherTextException {
        Scanner scanner = new Scanner(System.in);

//        System.out.println("Enter full file path for test dir:");
//        String test = scanner.nextLine();
        Date date = new Date();
        System.out.println(date.getTime() % 1000);
//        System.out.println("Enter full file path for in dir:");
//        String in = scanner.nextLine();
//        System.out.println("Enter full file path for cloud dir:");
//        String cloud = scanner.nextLine();
//
//        System.out.println("Upload or Download [U/D]");
//        String ud = scanner.nextLine();

//        listFiles(new File(test));

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
