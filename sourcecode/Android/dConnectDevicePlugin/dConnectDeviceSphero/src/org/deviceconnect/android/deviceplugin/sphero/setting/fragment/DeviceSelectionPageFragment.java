/*
 DeviceSelectionPageFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.setting.fragment;

import java.util.List;

import org.deviceconnect.android.deviceplugin.sphero.setting.SettingActivity;
import org.deviceconnect.android.deviceplugin.sphero.setting.SettingActivity.DeviceControlListener;
import org.deviceconnect.android.deviceplugin.sphero.setting.widget.DeviceListAdapter;
import org.deviceconnect.android.deviceplugin.sphero.setting.widget.DeviceListAdapter.OnConnectButtonClickListener;

import orbotix.sphero.Sphero;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.deviceconnect.android.deviceplugin.sphero.R;

/**
 * デバイス一覧画面.
 * @author NTT DOCOMO, INC.
 */
public class DeviceSelectionPageFragment extends Fragment implements DeviceControlListener,
        OnConnectButtonClickListener {

    /**
     * プログレス領域の可視フラグ.
     */
    private static final String KEY_PROGRESS_VISIBILITY = "visible";

    /**
     * アダプター.
     */
    private DeviceListAdapter mAdapter;

    /**
     * インジケーター.
     */
    private ProgressDialog mIndView;

    /**
     * 検索ダイアログの表示状態.
     */
    private int mSearchingVisibility = -1;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        ((SettingActivity) activity).setDeviceControlListener(this);
        mAdapter = new DeviceListAdapter(getActivity());
        mAdapter.setOnConnectButtonClickListener(this);
        ((SettingActivity) activity).sendGetConnectedDevicesBroadcast();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.setting_device_list, null);
        View emptyView = root.findViewById(R.id.device_list_empty);
        ListView listView = (ListView) root.findViewById(R.id.device_list_view);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(emptyView);

        if (savedInstanceState != null) {
            mSearchingVisibility = savedInstanceState.getInt(KEY_PROGRESS_VISIBILITY);
        }

        if (mSearchingVisibility == -1 || mAdapter.getCount() != 0) {
            mSearchingVisibility = View.GONE;
        }
        
        View progressZone = root.findViewById(R.id.progress_zone);
        progressZone.setVisibility(mSearchingVisibility);

        return root;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        
        View root = getView();
        
        if (root != null) {
            View progressZone = root.findViewById(R.id.progress_zone);
            outState.putInt(KEY_PROGRESS_VISIBILITY, progressZone.getVisibility());
        }
    }

    @Override
    public void onConnectedDevices(final List<Parcelable> devices) {

        if (devices.size() == 0) {
            startDiscovery();
        } else {
            for (Parcelable device : devices) {
                if (device instanceof Sphero) {
                    mAdapter.add((Sphero) device);
                }
            }
        }
    }

    @Override
    public void onDeviceFound(final Sphero device) {
        
        View root = getView();
        if (root != null) {
            View progressZone = root.findViewById(R.id.progress_zone);
            progressZone.setVisibility(View.GONE);
        }
        
        mAdapter.add(device);
    }

    @Override
    public void onDeviceLost(final Sphero device) {
        mAdapter.remove(device);
    }

    @Override
    public void onDeviceConnected(final Sphero device) {

        if (mIndView != null) {
            mIndView.dismiss();
        }

        if (device == null) {
            AlertDialog.Builder builder = new Builder(getActivity());
            builder.setTitle(R.string.title_error);
            builder.setMessage(R.string.message_conn_error);
            builder.setPositiveButton(R.string.btn_close, new OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        }

        mAdapter.changeConnectionState(device);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        View root = getView();
        if (root != null) {
            View progressZone = root.findViewById(R.id.progress_zone);
            mSearchingVisibility = progressZone.getVisibility();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDiscovery();
    }

    @Override
    public void onClicked(final int position, final Sphero device) {

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (device.isConnected()) {
            ((SettingActivity) activity).sendDisonnectBroadcast(device.getUniqueId());
            mAdapter.remove(device);

            if (mAdapter.getCount() == 0) {
                // 現在検知している場合は一旦検知をやめ、新たに検知を開始する.
                stopDiscovery();
                startDiscovery();
            }
        } else {
            ((SettingActivity) activity).sendConnectBroadcast(device.getUniqueId());
            mIndView = new ProgressDialog(activity);
            mIndView.setMessage(activity.getString(R.string.connecting));
            mIndView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mIndView.setCancelable(false);
            mIndView.show();
        }
    }

    /**
     * 検知開始.
     */
    private void startDiscovery() {
        Activity activity = getActivity();
        if (activity != null) {
            ((SettingActivity) activity).sendStartDiscoveryBroadcast();
            View root = getView();
            if (root != null) {
                View progressZone = root.findViewById(R.id.progress_zone);
                progressZone.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 検知終了.
     */
    private void stopDiscovery() {
        Activity activity = getActivity();
        if (activity != null) {
            ((SettingActivity) activity).sendStopDiscoveryBroadcast();
        }
    }
}
