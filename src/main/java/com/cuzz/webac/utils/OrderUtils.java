package com.cuzz.webac.utils;

import java.io.IOException;
import java.security.*;
import java.util.UUID;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

public class OrderUtils {
    static final int KEY_LENGTH_BYTE = 32;
    static final int TAG_LENGTH_BIT = 128;
    public static String generateOrderNumber() {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();

        // 获取 UUID 的前部分，并去掉连字符
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 拼接时间戳和 UUID 片段，生成订单号
        return "ORDER_" + timestamp + "_" + uuidPart;
    }

    public static void AesUtil(byte[] key) {
        if (key.length != KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
        }
        System.out.println("key符合规则");
    }

    public static String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext,byte[] aesKey)
            throws GeneralSecurityException, IOException {

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
