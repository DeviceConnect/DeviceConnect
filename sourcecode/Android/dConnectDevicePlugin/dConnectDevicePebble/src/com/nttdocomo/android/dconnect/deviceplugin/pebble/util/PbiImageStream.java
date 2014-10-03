package com.nttdocomo.android.dconnect.deviceplugin.pebble.util;

/**
 * Pebble 用 pbi 画像のファイルイメージ作成
 * 
 * @author terawaki
 * @see ~/pebble-dev/PebbleSDK-2.3/Pebble/tools/bitmapgen.py を参照のこと
 */
public class PbiImageStream {
    /** Pebble の int のビット数. */
    private static final int CONTAIN_BITS = 32;
    /** Pebble の int の byte 数. */
    private static final int WORD_BY_BYTE = 4;
    /** pbi ファイルイメージ. */
    private byte[] mDataWithHeader;
    /** pbi 総byte 数. */
    private int mDataImageDataSize;
    /** pbi ヘッダ長. */
    private static final int HEADER_LENGTH = 12;

    // 以下の 6つ のshortデータは、pbi ヘッダを表している
    /** 横ラインのバイト数. */
    private int mRowSizeByte;
    /** pbi ファイルのバージョン. */
    private static final short PBI_FIEL_VERSION = (1 << 12);
    /** 開始位置 x. */
    private short mX = 0;
    /** 開始位置 y. */
    private short mY = 0;
    /** 横幅 bit 数. */
    private short mWidth = 0;
    /** 高さ bit 数. */
    private short mHeight = 0;

    /**
     * Pbi ファイルイメージの作成.
     * 
     * @param w 横幅.
     * @param h 高さ.
     */
    PbiImageStream(final int w, final int h) {
        mWidth = (short) w;
        mHeight = (short) h;
        mRowSizeByte = ((mWidth + CONTAIN_BITS - 1) / CONTAIN_BITS) * WORD_BY_BYTE;
        mDataImageDataSize = mRowSizeByte * mHeight;
        mDataWithHeader = new byte[mDataImageDataSize + HEADER_LENGTH];

        // ヘッダー作成
        BinaryTools.setInt32ToByte2(mDataWithHeader, mRowSizeByte, 0);
        BinaryTools.setInt32ToByte2(mDataWithHeader, PBI_FIEL_VERSION, 2);
        BinaryTools.setInt32ToByte2(mDataWithHeader, mX, 4);
        BinaryTools.setInt32ToByte2(mDataWithHeader, mY, 6);
        BinaryTools.setInt32ToByte2(mDataWithHeader, mWidth, 8);
        BinaryTools.setInt32ToByte2(mDataWithHeader, mHeight, 10);

        // データ部分を初期化
        for (int i = HEADER_LENGTH; i < mDataWithHeader.length; i++) {
            mDataWithHeader[i] = 0;
        }
    }

    /**
     * ピクセルを描画.
     * 
     * @param x 位置 x.
     * @param y 位置 y.
     * @param color 色
     */
    public void setPixel(final int x, final int y, final int color) {
        if (x < 0 || x >= mWidth) {
            return;
        }
        if (y < 0 || y >= mHeight) {
            return;
        }
        int wordPosX = x / Byte.SIZE;
        int index = (mRowSizeByte * y) + wordPosX;
        int bitPosX = x % Byte.SIZE;
        if (color != 0) {
            mDataWithHeader[index + HEADER_LENGTH] |= (1 << bitPosX);
        } else {
            mDataWithHeader[index + HEADER_LENGTH] &= (~(1 << bitPosX));
        }
    }

    /**
     * pbi ファイルの byte配列を得る.
     * 
     * @return byte配列
     */
    public byte[] getStream() {
        return mDataWithHeader;
    }
}
