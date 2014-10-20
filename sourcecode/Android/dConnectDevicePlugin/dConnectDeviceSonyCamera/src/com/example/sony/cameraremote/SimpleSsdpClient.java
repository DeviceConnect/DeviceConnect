/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * SimpleSsdpClient.
 */
public class SimpleSsdpClient {

    /** タイムアウトmsec. */
    private static final int SSDP_RECEIVE_TIMEOUT = 10000; // msec
    /** バッファサイズ. */
    private static final int PACKET_BUFFER_SIZE = 1024;
    /** SSDPポート. */
    private static final int SSDP_PORT = 1900;
    /** SSDP MX. */
    private static final int SSDP_MX = 1;
    /** SSDPアドレス. */
    private static final String SSDP_ADDR = "239.255.255.250";
    /** SSDP URN. */
    private static final String SSDP_ST = "urn:schemas-sony-com:service:ScalarWebAPI:1";
    /** スレッドスリープ時間. */
    private static final int THREAD_SLEEP_TIME_MSEC = 100;

    /** Handler interface for SSDP search result. */
    public interface SearchResultHandler {

        /**
         * Called when API server device is found. Note that it's performed by
         * non-UI thread.
         * 
         * @param device API server device that is found by searching
         */
        void onDeviceFound(ServerDevice device);

        /**
         * Called when searching completes successfully. Note that it's
         * performed by non-UI thread.
         */
        void onFinished();

        /**
         * Called when searching completes with some errors. Note that it's
         * performed by non-UI thread.
         */
        void onErrorFinished();
    }

    /** Searching Flag. */
    private boolean mSearching = false;

    /**
     * Search API server device.
     * 
     * @param handler result handler
     * @return true: start successfully, false: already searching now
     */
    public synchronized boolean search(final SearchResultHandler handler) {
        if (mSearching) {
            return false;
        }
        if (handler == null) {
            throw new NullPointerException("handler is null.");
        }

        final String ssdpRequest = "M-SEARCH * HTTP/1.1\r\n" + String.format("HOST: %s:%d\r\n", SSDP_ADDR, SSDP_PORT)
                + String.format("MAN: \"ssdp:discover\"\r\n") + String.format("MX: %d\r\n", SSDP_MX)
                + String.format("ST: %s\r\n", SSDP_ST) + "\r\n";
        final byte[] sendData = ssdpRequest.getBytes();

        Thread mThread = new Thread() {

            @Override
            public void run() {
                // Send Datagram packets
                DatagramSocket socket = null;
                DatagramPacket receivePacket = null;
                DatagramPacket packet = null;
                try {
                    socket = new DatagramSocket();
                    InetSocketAddress iAddress = new InetSocketAddress(SSDP_ADDR, SSDP_PORT);
                    packet = new DatagramPacket(sendData, sendData.length, iAddress);
                    // send 3 times
                    socket.send(packet);
                    Thread.sleep(THREAD_SLEEP_TIME_MSEC);
                    socket.send(packet);
                    Thread.sleep(THREAD_SLEEP_TIME_MSEC);
                    socket.send(packet);
                } catch (InterruptedException e) {
                  //Exceptionを受けるだけなので処理は行わない
                } catch (SocketException e) {
                    handler.onErrorFinished();
                } catch (IOException e) {
                    handler.onErrorFinished();
                }

                if (socket == null) {
                    return;
                }

                // Receive reply packets
                mSearching = true;
                long startTime = System.currentTimeMillis();
                List<String> foundDevices = new ArrayList<String>();
                byte[] array = new byte[PACKET_BUFFER_SIZE];
                while (mSearching) {
                    receivePacket = new DatagramPacket(array, array.length);
                    try {
                        socket.setSoTimeout(SSDP_RECEIVE_TIMEOUT);
                        socket.receive(receivePacket);
                        String ssdpReplyMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        String ddUsn = findParameterValue(ssdpReplyMessage, "USN");

                        /*
                         * There is possibility to receive multiple packets from
                         * a individual server.
                         */
                        if (!foundDevices.contains(ddUsn)) {
                            String ddLocation = findParameterValue(ssdpReplyMessage, "LOCATION");
                            foundDevices.add(ddUsn);

                            // Fetch Device Description XML and parse it.
                            ServerDevice device = ServerDevice.fetch(ddLocation);
                            // Note that it's a irresponsible rule
                            // for the sample application.
                            if (device != null && device.hasApiService("camera")) {
                                handler.onDeviceFound(device);
                            }
                        }
                    } catch (InterruptedIOException e) {
                        break;
                    } catch (IOException e) {
                        handler.onErrorFinished();
                        return;
                    }
                    if (SSDP_RECEIVE_TIMEOUT < System.currentTimeMillis() - startTime) {
                        break;
                    }
                }
                mSearching = false;
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                handler.onFinished();
            };
        };
        mThread.start();

        return true;
    }

    /**
     * Checks whether searching is in progress or not.
     * 
     * @return true: now searching, false: otherwise
     */
    public boolean isSearching() {
        return mSearching;
    }

    /**
     * Cancels searching. Note that it cannot stop the operation immediately.
     */
    public void cancelSearching() {
        mSearching = false;
    }

    /**
     * Find a value string from message line as below. (ex.)
     * "ST: XXXXX-YYYYY-ZZZZZ" -> "XXXXX-YYYYY-ZZZZZ"
     * 
     * @param ssdpMessage SSDP message
     * @param paramName paramName
     * @return val.trim
     */
    private static String findParameterValue(final String ssdpMessage, final String paramName) {
        String name = paramName;
        if (!name.endsWith(":")) {
            name = name + ":";
        }
        int start = ssdpMessage.indexOf(name);
        int end = ssdpMessage.indexOf("\r\n", start);
        if (start != -1 && end != -1) {
            start += name.length();
            String val = ssdpMessage.substring(start, end);
            if (val != null) {
                return val.trim();
            }
        }
        return null;
    }
}
