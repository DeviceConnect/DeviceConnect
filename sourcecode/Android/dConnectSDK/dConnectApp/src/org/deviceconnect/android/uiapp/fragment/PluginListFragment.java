/*
 PluginListFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.device.DevicePlugin;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.client.DConnectClient;
import org.deviceconnect.message.http.impl.client.HttpDConnectClient;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.SystemProfileConstants;
import org.deviceconnect.utils.URIBuilder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * プラグインリストを表示するフラグメント.
 */
public class PluginListFragment extends ListFragment {

    /**
     * d-Connectクライアント.
     */
    private DConnectClient mDConnectClient;

    /**
     * デフォルトターゲット.
     */
    private HttpHost mDefaultTarget = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mDConnectClient = new HttpDConnectClient();
        loadTargetHostSettings();

        (new SystemLoader()).execute();
    }

    @Override
    public void onListItemClick(final ListView parent,
            final View view, final int position, final long id) {
        DevicePlugin plugin = (DevicePlugin) parent.getAdapter().getItem(position);
        SystemWakeupLoader loader = new SystemWakeupLoader();
        loader.execute(plugin.getId());
    }

    /**
     * ターゲットホスト設定を読み込む.
     */
    private void loadTargetHostSettings() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        String scheme;
        if (prefs.getBoolean(getString(R.string.key_settings_dconn_ssl), false)) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        String hostname = prefs.getString(
                getString(R.string.key_settings_dconn_host),
                getString(R.string.default_host));

        int port = Integer.parseInt(prefs.getString(
                getString(R.string.key_settings_dconn_port),
                getString(R.string.default_port)));

        mDefaultTarget = new HttpHost(hostname, port , scheme);
    }

    /**
     * デフォルトホストを取得する.
     * @return ホスト
     */
    private HttpHost getDefaultHost() {
        return mDefaultTarget;
    }

    /**
     * システムローダー.
     */
    private class SystemWakeupLoader extends AsyncTask<String, Void, DConnectMessage> {

        @Override
        protected DConnectMessage doInBackground(final String... args) {
            DConnectMessage message = null;
            try {
                if (args == null || args.length < 1) {
                    return null;
                }
                String id = args[0];
                URIBuilder uriBuilder = new URIBuilder();
                uriBuilder.setProfile(SystemProfileConstants.PROFILE_NAME);
                uriBuilder.setInterface(SystemProfileConstants.INTERFACE_DEVICE);
                uriBuilder.setAttribute(SystemProfileConstants.ATTRIBUTE_WAKEUP);
                uriBuilder.addParameter(SystemProfileConstants.PARAM_PLUGIN_ID, id);

                HttpResponse response = mDConnectClient.execute(
                        getDefaultHost(), new HttpPut(uriBuilder.build()));

                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (IOException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            } catch (URISyntaxException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            }
            return message;
        }

        @Override
        protected void onPostExecute(final DConnectMessage result) {
            super.onPostExecute(result);
            
            if (result.getInt(DConnectMessage.EXTRA_RESULT)
                    == DConnectMessage.RESULT_OK) {
                return;
            }

            Toast.makeText(getActivity(), R.string.plugins_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * システムローダー.
     */
    private class SystemLoader extends AsyncTask<Object, Integer, DConnectMessage> {

        @Override
        protected DConnectMessage doInBackground(final Object... args) {

            DConnectMessage message;
            try {
                URIBuilder uriBuilder = new URIBuilder();
                uriBuilder.setProfile(SystemProfileConstants.PROFILE_NAME);

                HttpResponse response = mDConnectClient.execute(
                        getDefaultHost(), new HttpGet(uriBuilder.build()));

                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (IOException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            } catch (URISyntaxException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            }

            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setProfile(SystemProfileConstants.PROFILE_NAME);
            try {
                HttpResponse response = mDConnectClient.execute(
                        getDefaultHost(), new HttpGet(uriBuilder.build()));
                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (IOException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            } catch (URISyntaxException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            }
            return message;
        }

        @Override
        protected void onPostExecute(final DConnectMessage result) {
            super.onPostExecute(result);

            if (getActivity().isFinishing()) {
                return;
            }

            if (result.getInt(DConnectMessage.EXTRA_RESULT)
                    != DConnectMessage.RESULT_OK) {
                return;
            }

            List<DevicePlugin> pluginList = new ArrayList<DevicePlugin>();

            List<Object> plugins = result.getList(SystemProfileConstants.PARAM_PLUGINS);
            if (plugins != null) {
                for (Object object : plugins) {
                    HashMap<Object, Object> plugin = (HashMap<Object, Object>) object;
                    if (plugin != null) {
                        String name = (String) plugin.get("name");
                        String id = (String) plugin.get("id");
                        pluginList.add(new DevicePlugin(name, id));
                    }
                }
            }

            if (getActivity() != null && !getActivity().isFinishing()) {
                setListAdapter(new PluginArrayAdapter(getActivity(), 
                        R.layout.card_item, pluginList));
            }
        }
    }

    /**
     * プラグイン配列アダプタ.
     */
    private class PluginArrayAdapter extends ArrayAdapter<DevicePlugin> {

        /**
         * レイアウトリソースID.
         */
        private int mResourceId;

        /**
         * コンストラクタ.
         * @param context コンテキスト
         * @param resource レイアウトリソースID
         * @param objects サービスリスト
         */
        public PluginArrayAdapter(final Context context, final int resource,
                final List<DevicePlugin> objects) {
            super(context, resource, objects);
            mResourceId = resource;
        }

        /**
         * コンストラクタ.
         * @param context コンテキスト
         * @param resource レイアウトリソースID
         * @param objects サービスリスト
         */
        public PluginArrayAdapter(
                final Context context, final int resource, final DevicePlugin[] objects) {
            super(context, resource, objects);
            mResourceId = resource;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater(getArguments()).inflate(mResourceId, null);
            }

            DevicePlugin item = getItem(position);

//            ImageView iconView = (ImageView) view.findViewById(android.R.id.icon);
//            iconView.setImageDrawable(getResources().getDrawable(item.getIconId()));

            TextView titleView = (TextView) view.findViewById(android.R.id.text1);
            titleView.setText(item.getName());

            return view;
        }
    }
}
