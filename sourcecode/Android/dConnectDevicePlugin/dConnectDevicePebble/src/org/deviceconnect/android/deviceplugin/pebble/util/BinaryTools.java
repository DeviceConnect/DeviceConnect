/*
 BinaryTools.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.util;

/**
 * バイナリデータを扱うクラス
 * @author NTT DOCOMO, INC.
 */
public final class BinaryTools {
    /** 8bitマスク. */
    private static final int BIT_MASK8 = 0xff;
    /** 8bitシフト. */
    public static final int BIT_SHIFT8 = 8;

    /**
     * コンストラクタ.
     * ユーティリティクラスなので、private.
     */
    private BinaryTools() {
    }

    /**
     * byte 配列の指定の位置から、2byte のデータを書き込む.
     * 
     * @param buf byte配列.
     * @param value 書き込むデータ
     * @param offset 書き込み位置
     */
    public static void setInt32ToByte2(final byte[] buf, final int value, final int offset) {
        buf[offset] = (byte) value;
        buf[offset + 1] = (byte) (value >> Byte.SIZE);
    }

    /**
     * byte 配列から4byte読み込み、int のデータを得る.
     * 
     * @param buf byte配列
     * @return intのデータ
     */
    public static int getInt32FromByte4(final byte[] buf) {
        int value = 0;
        value = getInt32FromByte2WithOffset(buf, 0);
        value += getInt32FromByte2WithOffset(buf, 2);
        return value;
    }

    /**
     * byte 配列から2byte読み込み、int のデータを得る.
     * 
     * @param buf byte配列
     * @return intのデータ
     */
    public static int getInt32FromByte2(final byte[] buf) {
        int value = 0;
        value = getInt32FromByte2WithOffset(buf, 0);
        return value;
    }

    /**
     * byte 配列から2byte読み込み、int のデータを得る.
     * <p>
     * little endian
     * </p>
     * @param buf byte配列
     * @param offset 何番目から読み込むか
     * @return intのデータ
     */
    private static int getInt32FromByte2WithOffset(final byte[] buf, final int offset) {
        int value = 0;
        value = buf[offset];
        value &= BIT_MASK8;
        int hiValue = buf[offset + 1];
        hiValue &= BIT_MASK8;
        hiValue <<= BIT_SHIFT8;
        return value + hiValue;
    }
}
