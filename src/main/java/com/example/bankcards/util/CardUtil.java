package com.example.bankcards.util;

public class CardUtil {

    public static String encryptNumber(String number) {
        // Простейшее "шифрование" — для тестового. Можно заменить на AES
        return new StringBuilder(number).reverse().toString();
    }

    public static String maskNumber(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    public static String maskEncrypted(String encrypted) {
        String original = new StringBuilder(encrypted).reverse().toString();
        return maskNumber(original);
    }
}
