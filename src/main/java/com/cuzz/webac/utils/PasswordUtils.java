package com.cuzz.webac.utils;

import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PasswordUtils {


    // AES密钥（256位），应安全存储在后端
    public static final String secretKey = "7f9d3f47b1a8e0dfb8c9a987f1b3d6a4c8e2f74b1a927e34d6f9e8c2a1b4f5c3";
    public static final String ivString = "00000000000000000000000000000000";  // 与前端保持一致

    // 将十六进制字符串转换为字节数组
    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // 解密Base64编码的加密数据
    public static String decrypt(String encryptedData) throws Exception {
        byte[] keyBytes = hexStringToByteArray(secretKey);
        byte[] ivBytes = hexStringToByteArray(ivString);

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // 匹配前端的模式和填充
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }


    // 对密码进行加密 返回加密后的密码加salt
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // 验证密码
    public static boolean checkPassword(String plainPassword, String hashedPasswordAndSalt) {
        return BCrypt.checkpw(plainPassword, hashedPasswordAndSalt);
    }

}
