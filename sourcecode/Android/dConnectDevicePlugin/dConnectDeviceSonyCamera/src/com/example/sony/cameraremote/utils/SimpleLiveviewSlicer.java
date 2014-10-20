/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * SimpleLiveviewSlicer.
 */
public class SimpleLiveviewSlicer {

    /**
     * Payload data class. See also Camera Remote API specification document to
     * know the data structure.
     */
    public static final class Payload {
        /** jpeg data container. */
        private byte[] jpegData;

        /** padding data container. */
        private byte[] paddingData;

        /**
         * jpegData getter.
         * @return jpegData
         */
        public byte[] getJpegData() {
            return jpegData;
        }
        /**
         * jpegData setter.
         * @param jpegDataParam jpegData
         */
        public void setJpegData(final byte[] jpegDataParam) {
            jpegData = jpegDataParam;
        }
        /**
         * paddingData getter.
         * @return paddingData
         */
        public byte[] getPaddingData() {
            return paddingData;
        }
        /**
         * paddingData setter.
         * @param paddingDataParam paddingData
         */
        public void setPaddingData(final byte[] paddingDataParam) {
            paddingData = paddingDataParam;
        }

        /**
         * Constructor.
         * @param jpgData jpegData
         * @param padData padding
         */
        private Payload(final byte[] jpgData, final byte[] padData) {
            setJpegData(jpgData);
            setPaddingData(padData);
        }
    }

    /** タイムアウト設定. */
    private static final int CONNECTION_TIMEOUT = 2000; // [msec]

    /** toInteger用のstartIndexの値定義. */
    private static final int JPG_TO_INTEGER_STARTINDEX = 4;
    /** toInteger用のcounterの値定義. */
    private static final int JPG_TO_INTEGER_COUNTER = 3;
    /** toInteger用のstartIndexの値定義. */
    private static final int PAD_TO_INTEGER_STARTINDEX = 7;
    /** toInteger用のcounterの値定義. */
    private static final int PAD_TO_INTEGER_COUNTER = 1;

    /** 数値利用のために定義. */
    private static final int NUMB_THREE = 3;
    /** 数値利用のために定義. */
    private static final int NUMB_FOUR = 4;
    /** 数値利用のために定義. */
    private static final int NUMB_115 = 115;
    /** 数値利用のために定義. */
    private static final int NUMB_FF = 0xFF;

    /** payloadheadder値. */
    private static final int PAYLOAD_ZERO = 0x24;
    /** payloadheadder値. */
    private static final int PAYLOAD_ONE = 0x35;
    /** payloadheadder値. */
    private static final int PAYLOAD_TWO = 0x68;
    /** payloadheadder値. */
    private static final int PAYLOAD_THREE = 0x79;


    /**
     * バッファの最大幅.
     */
    private static final int BUFFER_MAX_VAL = 1024;

    /** 値定義. */
    private static final int RET_VALUE = 8;

    /** Intへの変換用. */
    private static final int CONVERT_TO_INT = 0xff;

    /** HTTPコネクションインスタンス. */
    private HttpURLConnection mHttpConn;
    /** 入力ストリーム. */
    private InputStream mInputStream;

    /**
     * Opens Liveview HTTP GET connection and prepares for reading Packet data.
     * 
     * @param liveviewUrl Liveview data url that is obtained by DD.xml or result
     *            of startLiveview API.
     * @throws IOException generic errors or exception.
     */
    public void open(final String liveviewUrl) throws IOException {
        if (mInputStream != null || mHttpConn != null) {
            throw new IllegalStateException("Slicer is already open.");
        }

        final URL url = new URL(liveviewUrl);
        mHttpConn = (HttpURLConnection) url.openConnection();
        mHttpConn.setRequestMethod("GET");
        mHttpConn.setConnectTimeout(CONNECTION_TIMEOUT);
        mHttpConn.connect();

        if (mHttpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            mInputStream = mHttpConn.getInputStream();
        }
        if (mInputStream == null) {
            throw new IOException("open error: " + liveviewUrl);
        }
    }

    /**
     * Closes the connection.
     * 
     * @throws IOException generic errors or exception.
     */
    public void close() throws IOException {
        if (mInputStream != null) {
            mInputStream.close();
            mInputStream = null;
        }
        if (mHttpConn != null) {
            mHttpConn.disconnect();
            mHttpConn = null;
        }
    }

    /**
     * Reads liveview stream and slice one Packet. If server is not ready for
     * liveview data, this API calling will be blocked until server returns next
     * data.
     * 
     * @return Payload data of sliced Packet
     * @throws IOException generic errors or exception.
     */
    public Payload nextPayload() throws IOException {
        if (mInputStream != null) {
            // Common Header
            int readLength = 1 + 1 + 2 + NUMB_FOUR;
            byte[] commonHeader = readBytes(mInputStream, readLength);
            if (commonHeader == null || commonHeader.length != readLength) {
                throw new IOException("Cannot read stream for common header.");
            }
            if (commonHeader[0] != (byte) NUMB_FF) {
                throw new IOException("Unexpected data format. (Start byte)");
            }
            if (commonHeader[1] != (byte) 0x01) {
                throw new IOException("Unexpected data format. (Payload byte)");
            }

            // Payload Header
            readLength = NUMB_FOUR + NUMB_THREE + 1 + NUMB_FOUR + 1 + NUMB_115;
            byte[] payloadHeader = readBytes(mInputStream, readLength);
            if (payloadHeader == null || payloadHeader.length != readLength) {
                throw new IOException("Cannot read stream for payload header.");
            }
            if (payloadHeader[0] != (byte) PAYLOAD_ZERO
                    || payloadHeader[1] != (byte) PAYLOAD_ONE
                    || payloadHeader[2] != (byte) PAYLOAD_TWO
                    || payloadHeader[NUMB_THREE] != (byte) PAYLOAD_THREE) {
                throw new IOException("Unexpected data format. (Start code)");
            }
            int jpegSize = bytesToInt(payloadHeader, JPG_TO_INTEGER_STARTINDEX, JPG_TO_INTEGER_COUNTER);
            int paddingSize = bytesToInt(payloadHeader, PAD_TO_INTEGER_STARTINDEX, PAD_TO_INTEGER_COUNTER);

            // Payload Data
            byte[] jpegData = readBytes(mInputStream, jpegSize);
            byte[] paddingData = readBytes(mInputStream, paddingSize);

            return new Payload(jpegData, paddingData);
        }
        return null;
    }

    /**
     *  Converts byte array to int.
     * @param byteData byteData
     * @param startIndex Index
     * @param count count
     * @return ret
     */
    private static int bytesToInt(final byte[] byteData, final int startIndex,
            final int count) {
        int ret = 0;
        for (int i = startIndex; i < startIndex + count; i++) {
            ret = (ret << RET_VALUE) | (byteData[i] & CONVERT_TO_INT);
        }
        return ret;
    }

    /**
     *  Reads byte array from the indicated input stream.
     * @param in inputstream
     * @param length length
     * @return ret
     * @throws IOException IO
     */
    private static byte[] readBytes(final InputStream in, final int length)
            throws IOException {
        ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_MAX_VAL];
        while (true) {
            int trialReadlen = Math.min(buffer.length,
                    length - tmpByteArray.size());
            int readlen = in.read(buffer, 0, trialReadlen);
            if (readlen < 0) {
                break;
            }
            tmpByteArray.write(buffer, 0, readlen);
            if (length <= tmpByteArray.size()) {
                break;
            }
        }
        byte[] ret = tmpByteArray.toByteArray();
        tmpByteArray.close();
        return ret;
    }
}
