package com.cuzz.webac.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    static String[] rareCharacters = {
            "儜", "儝", "儞", "償", "儠", "儡", "儢", "儣", "儤", "儥",
            "儦", "儧", "儨", "儩", "優", "儫", "儬", "儭", "儮", "儯",
            "儰", "儲", "儳", "儴", "儵", "儶", "儷", "儸", "儺"
    };


    // 去掉边框的二维码生成方法
    public static int[][] generateQRCodeMatrix(String qrCodeData, int size) throws WriterException {
        // 配置二维码生成参数
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // 初始化 QRCodeWriter
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 生成二维码，得到 BitMatrix
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, size, size, hintMap);

        // 找到二维码实际内容的边界
        int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
        int realWidth = enclosingRectangle[2];
        int realHeight = enclosingRectangle[3];

        // 裁剪掉空白边框，只保留实际二维码区域
        int[][] qrCodeMatrix = new int[realHeight][realWidth];
        for (int y = 0; y < realHeight; y++) {
            for (int x = 0; x < realWidth; x++) {
                // 通过偏移量取实际二维码的像素值
                qrCodeMatrix[y][x] = bitMatrix.get(x + enclosingRectangle[0], y + enclosingRectangle[1]) ? 1 : 0;
            }
        }

        return qrCodeMatrix;  // 返回二维码的二进制矩阵
    }

    private static String conversion(int[][] qrCode, String[] rareCharacters) {
        String strng="";
        for (int i = 0; i < 29; i++) {
            for (int j = 0; j < 29; j++) {
                if (qrCode[i][j] == 0) {
                    strng = strng +"§f"+ rareCharacters[i]+"ꐫ";
                } else {
                    strng = strng + "§c" + rareCharacters[i]+"ꐫ";
                }
            }
            //这边可加入缩进的  自己测试时使用换行
            //16 +8+4+1
            strng=strng+"ꐯꐮꐭꐫ";
        }
        return strng;
    }

    public static String getQRStr(String qrCodeData) throws WriterException {
        int[][] bitmap = generateQRCodeMatrix(qrCodeData, 29);
        String QRStr = conversion(bitmap,rareCharacters);
        return QRStr;
    }
}
