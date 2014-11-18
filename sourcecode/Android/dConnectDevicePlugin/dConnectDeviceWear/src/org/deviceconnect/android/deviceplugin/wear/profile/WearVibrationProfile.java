/*
 WearVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.Collection;
import java.util.HashSet;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.VibrationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Vibration Profile.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearVibrationProfile extends VibrationProfile implements ConnectionCallbacks, OnConnectionFailedListener {

    /** Google Play Service. */
    private GoogleApiClient mGoogleApiClient;

    /** 内部ID. */
    private String mId = "";

    /** Status. */
    private int mVibrateStatus;

    /** バイブレーションSTART. */
    private static final int STATUS_VIBRATE_START = 1;

    /** バイブレーションSTOP. */
    private static final int STATUS_VIBRATE_STOP = 2;

    /** VibrationのPattern. */
    private long[] mPattern;

    @Override
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId,
            final long[] pattern) {
        if (deviceId == null) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (!WearUtils.checkDeviceId(deviceId)) {
            MessageUtils.setNotFoundDeviceError(response);
        } else {
            mId = getNodeId(deviceId);
            mVibrateStatus = STATUS_VIBRATE_START;
            mPattern = pattern;

            // Connect Google Play Service
            mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addApi(Wearable.API)
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
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (!WearUtils.checkDeviceId(deviceId)) {
            MessageUtils.setNotFoundDeviceError(response);
        } else {
            mId = getNodeId(deviceId);
            mVibrateStatus = STATUS_VIBRATE_STOP;

            // Connect Google Play Service
            mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addApi(Wearable.API)
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
    private void sendMessageToStartActivity(final String id, final String action, final String message) {
        Collection<String> nodes = getNodes();
        for (String node : nodes) {
            // 指定デバイスのノードに送信
            if (node.indexOf(id) != -1) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node, action,
                        message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    mGoogleApiClient.disconnect();
                }
            }
        }
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        if (mVibrateStatus == STATUS_VIBRATE_START) {
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
        } else if (mVibrateStatus == STATUS_VIBRATE_STOP) {
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
