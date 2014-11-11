/*
 WearNetworkServiceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * NetworkServiceDiscoveryプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile implements ConnectionCallbacks,
        OnConnectionFailedListener {

    /** Google Play Service. */
    private GoogleApiClient mGoogleApiClient;

    /**
     * デバイスID.
     */
    public static final String DEVICE_ID = "Wear";

    /**
     * デバイス名: {@value}
     */
    public static final String DEVICE_NAME = "Android Wear";

    /**
     * テスト用デバイスタイプ.
     */
    public static final String DEVICE_TYPE = "BLE";

    /**
     * テスト用オンライン状態.
     */
    public static final boolean DEVICE_ONLINE = true;

    /**
     * テスト用コンフィグ.
     */
    public static final String DEVICE_CONFIG = "myConfig";

    /**
     * StaticなResponse Intent.
     */
    public static Intent mResponse;

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addApi(Wearable.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mResponse = response;

        return false;
    }

    @Override
    protected boolean onPutOnServiceChange(final Intent request, Intent response, String deviceId, String sessionKey) {

        if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_SERVICE_CHANGE);

            Bundle service = new Bundle();
            setId(service, DEVICE_ID);
            setName(service, DEVICE_NAME);
            setType(service, DEVICE_TYPE);
            setOnline(service, DEVICE_ONLINE);
            setConfig(service, DEVICE_CONFIG);

            setNetworkService(message, service);

            return false;
        }
    }

    @Override
    protected boolean onDeleteOnServiceChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                List<Bundle> services = new ArrayList<Bundle>();

                Collection<String> mNodes = getNodes();

                // Wearのバイス数分だけ、検索結果に反映
                for (String node : mNodes) {
                    // nodeの最初をUniqueKeyで利用
                    String[] mNodeArray = node.split("-");

                    Bundle service = new Bundle();
                    setId(service, DEVICE_ID + "(" + mNodeArray[0] + ")");
                    setName(service, DEVICE_NAME + "(" + mNodeArray[0] + ")");
                    setType(service, DEVICE_TYPE);
                    setOnline(service, DEVICE_ONLINE);
                    setConfig(service, DEVICE_CONFIG);
                    services.add(service);
                }

                setResult(mResponse, DConnectMessage.RESULT_OK);
                setServices(mResponse, services);
                getContext().sendBroadcast(mResponse);

                return null;
            }
        }.execute();
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

    @Override
    public void onConnectionSuspended(final int cause) {
        setResult(mResponse, DConnectMessage.RESULT_ERROR);
        getContext().sendBroadcast(mResponse);
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        setResult(mResponse, DConnectMessage.RESULT_ERROR);
        getContext().sendBroadcast(mResponse);
    }
}
