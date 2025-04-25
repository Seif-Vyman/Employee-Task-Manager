package com.SE2.EmployeeTaskManager.util;

import org.jasypt.util.text.AES256TextEncryptor;

public class JasyptUtil {
    private static final String SECRET = "5aleha3laAllah"; // Change this!

    public static String encrypt(String plainText) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(SECRET);
        return textEncryptor.encrypt(plainText);
    }

    public static String decrypt(String encryptedText) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(SECRET);
        return textEncryptor.decrypt(encryptedText);
    }
}