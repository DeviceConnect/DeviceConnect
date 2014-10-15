/*
 NetworkServiceDiscoveryProfileFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.deviceconnect.android.uiapp.device.SmartDevice;
import org.deviceconnect.android.uiapp.fragment.SmartDeviceFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.utils.URIBuilder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Network Service Discovery Profile フラグメント.
 */
public class NetworkServiceDiscoveryProfileFragment extends SmartDeviceFragment {

    /**
     * リストビュー.
     */
    private ListView mListView;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreate", savedInstanceState);
        super.onCreate(savedInstanceState);

        mLogger.exiting(getClass().getName(), "onCreate");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreateView",
                new Object[] {inflater, container, savedInstanceState});
        final Context context = getActivity();

        FrameLayout lframe = new FrameLayout(context);

        ListView lv = new ListView(getActivity());
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        lframe.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lframe.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        mListView = (ListView) lframe.findViewById(android.R.id.list);
        mListView.setAdapter(new ArrayAdapter<SmartDevice>(
                getActivity(), android.R.layout.simple_list_item_1));

        (new ServiceDiscoveryTask()).execute();

        mLogger.exiting(getClass().getName(), "onCreateView", mListView);
        return lframe;
    }

    /**
     * サービス検索タスク.
     */
    private class ServiceDiscoveryTask extends AsyncTask<Void, Integer, List<SmartDevice>> {

        @SuppressWarnings("unchecked")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ArrayAdapter<SmartDevice>) mListView.getAdapter()).clear();
        }

        @Override
        protected List<SmartDevice> doInBackground(final Void... params) {
            mLogger.entering(getClass().getName(), "doInBackground", params);

            List<SmartDevice> devices = new ArrayList<SmartDevice>();

            DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);

            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
            uriBuilder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
            uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
            uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

            try {
                HttpResponse response = getDConnectClient().execute(
                        getDefaultHost(), new HttpGet(uriBuilder.build()));
                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (URISyntaxException e) {
                e.printStackTrace();

                mLogger.exiting(getClass().getName(), "doInBackground", devices);
                return devices;
            } catch (IOException e) {
                e.printStackTrace();

                mLogger.exiting(getClass().getName(), "doInBackground", devices);
                return devices;
            }

            if (message == null) {
                mLogger.exiting(getClass().getName(), "doInBackground", devices);
                return devices;
            }

            int result = message.getInt(DConnectMessage.EXTRA_RESULT);
            if (result == DConnectMessage.RESULT_ERROR) {
                mLogger.exiting(getClass().getName(), "doInBackground", devices);
                return devices;
            }

            List<Object> services = message.getList(
                    NetworkServiceDiscoveryProfileConstants.PARAM_SERVICES);
            if (services != null) {
                for (Object object: services) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> service = (Map<String, Object>) object;
                    SmartDevice device = new SmartDevice(
                        service.get(NetworkServiceDiscoveryProfileConstants.PARAM_ID).toString(),
                        service.get(NetworkServiceDiscoveryProfileConstants.PARAM_NAME).toString());
                    devices.add(device);
                }
            }

            mLogger.exiting(getClass().getName(), "doInBackground", devices);
            return devices;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(final List<SmartDevice> result) {
            mLogger.entering(getClass().getName(), "onPostExecute", result);

            if (getActivity().isFinishing()) {
                return;
            }

            ((ArrayAdapter<SmartDevice>) mListView.getAdapter()).addAll(result);
            mLogger.exiting(getClass().getName(), "onPostExecute");
        }
    }

}
