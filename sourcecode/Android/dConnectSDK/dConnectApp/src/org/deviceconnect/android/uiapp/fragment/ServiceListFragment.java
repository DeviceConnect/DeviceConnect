/*
 ServiceListFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.deviceconnect.android.uiapp.DConnectActivity;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.device.SmartDevice;
import org.deviceconnect.android.uiapp.device.SmartService;
import org.deviceconnect.android.uiapp.fragment.profile.BatteryProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.ConnectProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.DeviceOrientationProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.ExtraProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.FileProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.MediaPlayerProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.MediaStreamRecordingProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.NetworkServiceDiscoveryProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.NotificationProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.PhoneProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.ProximityProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.SettingsProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.SystemProfileFragment;
import org.deviceconnect.android.uiapp.fragment.profile.VibrationProfileFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.client.DConnectClient;
import org.deviceconnect.message.http.impl.client.HttpDConnectClient;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.BatteryProfileConstants;
import org.deviceconnect.profile.ConnectProfileConstants;
import org.deviceconnect.profile.DeviceOrientationProfileConstants;
import org.deviceconnect.profile.FileProfileConstants;
import org.deviceconnect.profile.MediaPlayerProfileConstants;
import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.profile.NotificationProfileConstants;
import org.deviceconnect.profile.PhoneProfileConstants;
import org.deviceconnect.profile.ProximityProfileConstants;
import org.deviceconnect.profile.SettingsProfileConstants;
import org.deviceconnect.profile.SystemProfileConstants;
import org.deviceconnect.profile.VibrationProfileConstants;
import org.deviceconnect.utils.URIBuilder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * サービスリストフラグメント.
 */
public class ServiceListFragment extends ListFragment {

    /**
     * スマートデバイス情報.
     */
    private SmartDevice mDevice;

    /**
     * d-Connectクライアント.
     */
    private DConnectClient mDConnectClient;

    /**
     * デフォルトターゲット.
     */
    private HttpHost mDefaultTarget = null;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");

    /**
     * フラグメントを生成する.
     * @return フラグメント
     */
    public static ServiceListFragment newInstance() {
        return new ServiceListFragment();
    }

    /**
     * コンストラクタ.
     */
    public ServiceListFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreate", savedInstanceState);
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mDConnectClient = new HttpDConnectClient();
        loadTargetHostSettings();

        mDevice = getArguments().getParcelable("device");

        (new ServiceSystemLoader()).execute(mDevice.getId());

        mLogger.exiting(getClass().getName(), "onCreate");
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onActivityCreated", savedInstanceState);

        super.onActivityCreated(savedInstanceState);
        getListView().setSelector(android.R.color.transparent);
        getListView().setDivider(null);

        mLogger.exiting(getClass().getName(), "onActivityCreated");
    }

    @Override
    public void onResume() {
        mLogger.entering(getClass().getName(), "onResume");

        super.onResume();
        loadTargetHostSettings();

        mLogger.exiting(getClass().getName(), "onResume");
    }

    @Override
    public void onListItemClick(final ListView parent,
            final View view, final int position, final long id) {
        mLogger.entering(getClass().getName(), "onListItemClick",
                new Object[] {parent, view, position, id});

        super.onListItemClick(parent, view, position, id);

        SmartService service = (SmartService) getListAdapter().getItem(position);
        String serviceName = service.getName();

        Fragment serviceFragment = null;
        if (serviceName.equals(BatteryProfileConstants.PROFILE_NAME)) {
            serviceFragment = new BatteryProfileFragment();
        } else if (serviceName.equals(ConnectProfileConstants.PROFILE_NAME)) {
            serviceFragment = new ConnectProfileFragment();
        } else if (serviceName.equals(DeviceOrientationProfileConstants.PROFILE_NAME)) {
            serviceFragment = new DeviceOrientationProfileFragment();
        } else if (serviceName.equals(FileProfileConstants.PROFILE_NAME)) {
            serviceFragment = new FileProfileFragment();
        } else if (serviceName.equals(MediaPlayerProfileConstants.PROFILE_NAME)) {
            serviceFragment = new MediaPlayerProfileFragment();
        } else if (serviceName.equals(MediaStreamRecordingProfileConstants.PROFILE_NAME)) {
            serviceFragment = new MediaStreamRecordingProfileFragment();
        } else if (serviceName.equals(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME)) {
            serviceFragment = new NetworkServiceDiscoveryProfileFragment();
        } else if (serviceName.equals(NotificationProfileConstants.PROFILE_NAME)) {
            serviceFragment = new NotificationProfileFragment();
        } else if (serviceName.equals(PhoneProfileConstants.PROFILE_NAME)) {
            serviceFragment = new PhoneProfileFragment();
        } else if (serviceName.equals(ProximityProfileConstants.PROFILE_NAME)) {
            serviceFragment = new ProximityProfileFragment();
        } else if (serviceName.equals(SettingsProfileConstants.PROFILE_NAME)) {
            serviceFragment = new SettingsProfileFragment();
        } else if (serviceName.equals(SystemProfileConstants.PROFILE_NAME)) {
            serviceFragment = new SystemProfileFragment();
        } else if (serviceName.equals(VibrationProfileConstants.PROFILE_NAME)) {
            serviceFragment = new VibrationProfileFragment();
        } else {
            serviceFragment = new ExtraProfileFragment(serviceName);
        }

        if (serviceFragment != null) {
            Bundle args = new Bundle();
            args.putParcelable("device", mDevice);
            serviceFragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.activity_main_content, serviceFragment)
                    .addToBackStack(null)
                    .commit();
        }

        mLogger.exiting(getClass().getName(), "onListItemClick");
    }

    /**
     * ターゲットホスト設定を読み込む.
     */
    private void loadTargetHostSettings() {
        mLogger.entering(getClass().getName(), "loadTargetHostSettings");

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

        mLogger.exiting(getClass().getName(), "loadTargetHostSettings");
    }

    /**
     * デフォルトホストを取得する.
     * @return ホスト
     */
    public HttpHost getDefaultHost() {
        return mDefaultTarget;
    }

    /**
     * アクセストークンを取得する.
     * アクセストークンがない場合にはnullを返却する。
     * @return アクセストークン
     */
    public String getAccessToken() {
        return ((DConnectActivity) getActivity()).getAccessToken();
    }

    /**
     * サービス選択リスナー.
     */
    public interface ServiceListSelectedListener {
        /**
         * サービス選択ハンドラ.
         * @param parent 親View
         * @param view 選択されたView
         * @param position 選択されたViewの位置
         * @param id 選択されたViewのID
         */
        void onServiceListSelected(final ListView parent,
                final View view, final int position, final long id);
    }

    /**
     * サービスシステムローダ.
     */
    private class ServiceSystemLoader extends AsyncTask<String, Integer, DConnectMessage> {

        @Override
        protected void onPreExecute() {
            mLogger.entering(getClass().getName(), "onPreExecute");
            mDevice.getServiceList().clear();
            mLogger.exiting(getClass().getName(), "onPreExecute");
        }

        @Override
        protected DConnectMessage doInBackground(final String... args) {
            mLogger.entering(getClass().getName(), "doInBackground", args);

            DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);

            if (args == null || args.length == 0) {
                mLogger.exiting(getClass().getName(), "doInBackground", message);
                return message;
            }

            String deviceId = args[0];

            try {
                URIBuilder uriBuilder = new URIBuilder();
                uriBuilder.setProfile(SystemProfileConstants.PROFILE_NAME);
                uriBuilder.setAttribute(SystemProfileConstants.ATTRIBUTE_DEVICE);
                uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
                uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                mLogger.fine("request: " + uriBuilder.build().toString());
                HttpResponse response = mDConnectClient.execute(
                        getDefaultHost(), new HttpGet(uriBuilder.build()));
                mLogger.fine("response: " + message.toString());

                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (URISyntaxException e) {
                mLogger.warning(e.toString());
            } catch (IOException e) {
                mLogger.warning(e.toString());
            }

            mLogger.exiting(getClass().getName(), "doInBackground", message);
            return message;
        }

        @Override
        protected void onPostExecute(final DConnectMessage result) {
            mLogger.entering(getClass().getName(), "onPostExecute", result);

            if (getActivity().isFinishing()) {
                return;
            }

            if (result == null
                    || result.getInt(DConnectMessage.EXTRA_RESULT)
                            == DConnectMessage.RESULT_ERROR) {
                mLogger.exiting(getClass().getName(), "onPostExecute");
                return;
            }

            List<Object> support = result.getList(SystemProfileConstants.PARAM_SUPPORTS);
            if (support != null) {
                for (Object object: support) {
                    String name = (String) object;
                    if (name != null) {
                        mDevice.addService(new SmartService(name));
                    }
                }
            }

            if (getActivity() != null && !getActivity().isFinishing()) {
                setListAdapter(new ServiceArrayAdapter(
                        getActivity(),
                        R.layout.card_item,
                        mDevice.getServiceList()));
            }

            mLogger.exiting(getClass().getName(), "onPostExecute");
        }

    }

    /**
     * サービス配列アダプタ.
     */
    private class ServiceArrayAdapter extends ArrayAdapter<SmartService> {

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
        public ServiceArrayAdapter(
                final Context context, final int resource, final List<SmartService> objects) {
            super(context, resource, objects);
            mLogger.entering(getClass().getName(), "ServiceArrayAdapter",
                    new Object[] {context, resource, objects});

            mResourceId = resource;

            mLogger.exiting(getClass().getName(), "ServiceArrayAdapter");
        }

        /**
         * コンストラクタ.
         * @param context コンテキスト
         * @param resource レイアウトリソースID
         * @param objects サービスリスト
         */
        public ServiceArrayAdapter(
                final Context context, final int resource, final SmartService[] objects) {
            super(context, resource, objects);
            mLogger.entering(getClass().getName(), "ServiceArrayAdapter",
                    new Object[] {context, resource, objects});

            mResourceId = resource;

            mLogger.exiting(getClass().getName(), "ServiceArrayAdapter");
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            mLogger.entering(getClass().getName(), "getView",
                    new Object[] {position, convertView, parent});

            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = getLayoutInflater(getArguments()).inflate(mResourceId, null);
            }

            SmartService item = getItem(position);

            ImageView iconView = (ImageView) view.findViewById(android.R.id.icon);
            iconView.setImageDrawable(getResources().getDrawable(item.getIconId()));

            TextView titleView = (TextView) view.findViewById(android.R.id.text1);
            titleView.setText(item.getName());

            mLogger.exiting(getClass().getName(), "getView", view);
            return view;
        }

    }

}
