/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * SimpleHttpClient.
 */
public final class SimpleHttpClient {

    /** デフォルトのタイムアウト. */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000; // [msec]
    /** デフォルトのタイムアウト. */
    private static final int DEFAULT_READ_TIMEOUT = 10000; // [msec]

    /**
     * コンストラクタ. ユーティリティクラスなので、インスタンスは作らせない。
     */
    private SimpleHttpClient() {
    }

    /**
     * Send HTTP GET request to the indicated url. Then returns response as
     * string.
     * 
     * @param url request target
     * @return response as string
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public static String httpGet(final String url) throws IOException {
        return httpGet(url, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Send HTTP GET request to the indicated url. Then returns response as
     * string.
     * 
     * @param url request target
     * @param timeout Request timeout
     * @return response as string
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public static String httpGet(final String url, final int timeout) throws IOException {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;

        // Open connection and input stream
        try {
            httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            httpConn.setReadTimeout(timeout);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null) {
                throw new IOException("Response Error:" + responseCode);
            }
        } catch (final SocketTimeoutException e) {
//            Log.w(TAG, "httpGet: Timeout: " + url);
            throw new IOException();
        } catch (final MalformedURLException e) {
//            Log.w(TAG, "httpGet: MalformedUrlException: " + url);
            throw new IOException();
        } catch (final IOException e) {
//            Log.w(TAG, "httpGet: " + e.getMessage());
            if (httpConn != null) {
                httpConn.disconnect();
            }
            throw e;
        }

        // Read stream as String
        BufferedReader reader = null;
        try {
            StringBuilder responseBuf = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            int c;
            while ((c = reader.read()) != -1) {
                responseBuf.append((char) c);
            }
            return responseBuf.toString();
        } catch (IOException e) {
//            Log.w(TAG, "httpGet: read error: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                //Exceptionを受けるだけなので処理は行わない
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
              //Exceptionを受けるだけなので処理は行わない
            }
        }
    }

    /**
     * Send HTTP POST request to the indicated url. Then returns response as
     * string.
     * 
     * @param url request target
     * @param postData POST body data as string (ex. JSON)
     * @return response as string
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public static String httpPost(final String url, final String postData) throws IOException {
        return httpPost(url, postData, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Send HTTP POST request to the indicated url. Then returns response as
     * string.
     * 
     * @param url request target
     * @param postData POST body data as string (ex. JSON)
     * @param timeout Request timeout
     * @return response as string
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public static String httpPost(final String url, final String postData, final int timeout) throws IOException {
        HttpURLConnection httpConn = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        InputStream inputStream = null;

        // Open connection and input stream
        try {
            httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            httpConn.setReadTimeout(timeout);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);

            outputStream = httpConn.getOutputStream();
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(postData);
            writer.flush();
            writer.close();
            writer = null;
            outputStream.close();
            outputStream = null;

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null) {
                throw new IOException("Response Error:" + responseCode);
            }
        } catch (final SocketTimeoutException e) {
//            Log.w(TAG, "httpPost: Timeout: " + url);
            throw new IOException();
        } catch (final MalformedURLException e) {
//            Log.w(TAG, "httpPost: MalformedUrlException: " + url);
            throw new IOException();
        } catch (final IOException e) {
//            Log.w(TAG, "httpPost: IOException: " + e.getMessage());
            if (httpConn != null) {
                httpConn.disconnect();
            }
            throw e;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
              //Exceptionを受けるだけなので処理は行わない
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
              //Exceptionを受けるだけなので処理は行わない
            }
        }

        // Read stream as String
        BufferedReader reader = null;
        try {
            StringBuilder responseBuf = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            int c;
            while ((c = reader.read()) != -1) {
                responseBuf.append((char) c);
            }
            return responseBuf.toString();
        } catch (IOException e) {
//            Log.w(TAG, "httpPost: read error: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
              //Exceptionを受けるだけなので処理は行わない
            }
        }
    }
}
