package com.teacore.teascript.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 二维码工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class QrCodeUtils  {

    //传入字符串生成相应的二维码
    public static Bitmap create2DCode(String str) throws WriterException{

        //生成二维矩阵，编码时指定大小，如果生成图片以后进行缩放的话，图片会模糊从而导致识别失败
        BitMatrix matrix=new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE,300,300);

        int width=matrix.getWidth();
        int height=matrix.getHeight();

        // 二维矩阵转为一维像素数组
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }








}
