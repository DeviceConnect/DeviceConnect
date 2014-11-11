/*
WearUtil.java
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.Collection;
import java.util.HashSet;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Wear Utils.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearUtils {

    /**
     * Google Play Service.
     */
    private static GoogleApiClient mGoogleApiClient;

    /**
     * Google Play Serviceを設定.
     * 
     * @param mGoogleApiClient
     */
    public void setGooglePlay(final GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
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
     */
    public void sendMessageToStartActivity(final String mId, final String action, final String message) {
        Collection<String> nodes = getNodes();
        for (String node : nodes) {

            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node, action,
                    message.getBytes()).await();
            if (!result.getStatus().isSuccess()) {

            } else {

            }
        }
    }
}
