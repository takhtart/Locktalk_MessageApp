package com.example.locktalk_messageapp.qolfunctions;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {

    // Encrypts message content using Blowfish algo and chatID as shared key
    public static String encrypt(String message, String chatID) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(chatID.getBytes(StandardCharsets.UTF_8), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec("abcdefgh".getBytes(StandardCharsets.UTF_8)));
        byte[] values = cipher.doFinal(message.getBytes());
        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    // Decrypts message content using Blowfish algo and chatID as shared key
    public static String decrypt(String message, String chatID) throws Exception {
        byte[] values = Base64.decode(message, Base64.DEFAULT);
//        return Arrays.toString(values);
        SecretKeySpec secretKeySpec = new SecretKeySpec(chatID.getBytes(StandardCharsets.UTF_8), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5PAdding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec("abcdefgh".getBytes(StandardCharsets.UTF_8)));
        return new String(cipher.doFinal(values));
    }

}
