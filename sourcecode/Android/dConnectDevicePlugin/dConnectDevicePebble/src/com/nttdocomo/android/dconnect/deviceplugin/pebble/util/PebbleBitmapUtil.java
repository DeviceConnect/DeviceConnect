package com.nttdocomo.android.dconnect.deviceplugin.pebble.util;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 画像を変換するためのユーティリティクラス.
 */
public final class PebbleBitmapUtil {
    /**
     * Paint.
     */
    private static final Paint MY_PAINT = new Paint();

    /**
     * コンストラクタ. ユーティリティクラスなので、private.
     */
    private PebbleBitmapUtil() {
    }

    /**
     * Pebbleのスクリーンサイズの枠に収まるようにアスペクト比を固定のままbitmapを拡縮する.
     * 
     * @param b 拡縮するbitmap
     * @return 拡縮したBitmap
     */
    public static Bitmap scale(final Bitmap b) {
        return scale(b, PebbleManager.PEBBLE_SCREEN_WIDTH, PebbleManager.PEBBLE_SCREEN_HEIGHT);
    }

    /**
     * 指定されたサイズの枠に収まるようにアスペクト比を固定のままbitmapを拡縮する.
     * <p>
     * 返り値のBitmapは別インスタンスなので、メモリを解放する場合には、別々にrecycleすること。
     * </p>
     * 
     * @param b 拡縮するbitmap
     * @param width 横幅
     * @param height 縦幅
     * @return 拡縮したBitmap
     */
    public static Bitmap scale(final Bitmap b, final int width, final int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float scale = 1.0f;
        int x;
        int y;
        int w = b.getWidth();
        int h = b.getHeight();
        int newW;
        int newH;
        if (w < width && h < height) {
            newW = w;
            newH = h;
            x = (int) ((width - newW) / 2);
            y = (int) ((height - newH) / 2);
        } else {
            if (w > h) {
                scale = width / (float) w;
                newW = width;
                newH = (int) (h * scale);
                x = 0;
                y = (int) ((height - newH) / 2);
            } else {
                scale = height / (float) h;
                newW = (int) (w * scale);
                newH = height;
                x = (int) ((width - newW) / 2);
                y = 0;
            }
        }
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, newW, newH);
        canvas.drawBitmap(b, src, dst, MY_PAINT);
        return bitmap;
    }

    /**
     * 指定されたデータを2値化して、PebbleのGBitmap構造に変換する.
     * 
     * @param bitmap 変換するBitmap
     * @return GBitmapのデータ
     */
    public static byte[] convertImageThresholding(final Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        final boolean byThreshold = false;
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        Random random = new Random();
        final int randomNumberMax = 255 ;

        PbiImageStream stream = new PbiImageStream(width, height);
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                final int threshold = 128;
                final int rgbColors = 3;
                int bitmapColor = pixels[(xx + yy * width)];
                int rr = Color.red(bitmapColor);
                int gg = Color.green(bitmapColor);
                int bb = Color.blue(bitmapColor);
                int x, y;
                y = (rr + gg + bb) / rgbColors;
                if (byThreshold) {
                    if (y < threshold) {
                        x = 0;
                    } else {
                        x = 1;
                    }
                } else {
                    x = 0 ;//誤差拡散法:iOS 側との互換性を有する
                    if( y > 150 ) {
                        x = 1 ;
                    }
                    else if( y > 110 ) {
                        if( y > random.nextInt(randomNumberMax) ) {
                            x = 1 ;
                        }
                    }
                }
                stream.setPixel(xx, yy, x);
            }
        }
        return stream.getStream();
    }
}
