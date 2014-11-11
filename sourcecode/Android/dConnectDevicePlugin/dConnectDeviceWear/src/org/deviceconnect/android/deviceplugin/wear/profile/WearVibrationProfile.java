/*
 WearVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import org.deviceconnect.android.profile.VibrationProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * Vibration Profile.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearVibrationProfile extends VibrationProfile implements ConnectionCallbacks, OnConnectionFailedListener {

    /** Google Play Service. */
    private static GoogleApiClient mGoogleApiClient;

    /** Tag. */
    private static final String TAG = "WEAR";

    /** Staticな内部ID. */
    private static String mId = "";

    /** Status. */
    private static int vibrateStatus;

    /** バイブレーションSTART. */
    private static final int STATUS_VIBRATE_START = 1;

    /** バイブレーションSTOP. */
    private static final int STATUS_VIBRATE_STOP = 2;

    /** StaticなPattern. */
    private static long[] mPattern;

    @Override
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId,
            final long[] pattern) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            mId = getNodeId(deviceId);
            vibrateStatus = STATUS_VIBRATE_START;
            mPattern = pattern;

            // Connect Google Play Service
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext()).addApi(Wearable.API)
                    .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onDeleteVibrate(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            mId = getNodeId(deviceId);
            vibrateStatus = STATUS_VIBRATE_STOP;

            // Connect Google Play Service
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext()).addApi(Wearable.API)
                    .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }

            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    /**
     * DeviceIDがらnodeを取得.
     * 
     * @param deviceId デバイスID
     * @return nodeId
     */
    private String getNodeId(final String deviceId) {

        String[] mDeviceIdArray = deviceId.split("\\(", 0);
        String id = mDeviceIdArray[1].replace(")", "");

        return id;
    }

    /**
     * Wear nodeを取得.
     * 
     * @return WearNode
     */
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            Log.i("WEAR", "node.getId():" + node.getId());
            results.add(node.getId());
        }
        return results;
    }

    /**
     * Wearにメッセージを送信.
     * 
     * @param id 内部ID
     * @param action アクション名
     * @param message 送信する文字列
     */
    public void sendMessageToStartActivity(final String id, final String action, final String message) {
        Collection<String> nodes = getNodes();
        for (String node : nodes) {

            // 指定デバイスのノードに送信
            if (node.indexOf(id) != -1) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node, action,
                        message.getBytes()).await();
                if (!result.getStatus().isSuccess()) {
                } else {
                    mGoogleApiClient.disconnect();
                }
            }
        }
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = WearNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        if (vibrateStatus == STATUS_VIBRATE_START) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {
                    // パターンを文字列に変換
                    String mPatternStr = "";
                    for (int i = 0; i < mPattern.length; i++) {
                        if (i == 0) {
                            mPatternStr += mPattern[i];
                        } else {
                            mPatternStr += "," + mPattern[i];
                        }
                    }
                    sendMessageToStartActivity(mId, WearConst.DEVICE_TO_WEAR_VIBRATION_RUN, mPatternStr);

                    return null;
                }
            }.execute();
        } else if (vibrateStatus == STATUS_VIBRATE_STOP) {
            // メッセージの送信
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {

                    sendMessageToStartActivity(mId, WearConst.DEVICE_TO_WEAR_VIBRATION_DEL, "");

                    return null;
                }
            }.execute();
        }
    }

    @Override
    public void onConnectionSuspended(final int cause) {
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
    }

}
