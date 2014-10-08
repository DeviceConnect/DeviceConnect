/*
HueFragment01
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package com.nttdocomo.android.dconnect.deviceplugin.hue.activity.fragment;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

/**
 * Hue設定画面(1)フラグメント.
 */
public class HueFragment01 extends Fragment implements OnClickListener, OnItemClickListener {

    /** TAG. */
    private static final String TAG = "HUE_FRAGMENT";

    /** ListViewのAdapter. */
    private CustomAdapter mAdapter;

    /** Activity. */
    private Activity mActivity;

    /** HueSDKオブジェクト. */
    private PHHueSDK mPhHueSDK;

    /** ProgressZone. */
    private View mProgressView;

    /** AccessPointのリスト. */
    private ListView mListView;

    /** 再検索ボタン. */
    private Button mSearchButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.hue_fragment_01, container, false);

        mActivity = this.getActivity();

        if (mRootView != null) {
            mSearchButton = (Button) mRootView.findViewById(R.id.btnRefresh);
            mSearchButton.setOnClickListener(this);

            mProgressView = mRootView.findViewById(R.id.progress_zone);
            mProgressView.setVisibility(View.VISIBLE);

            mListView = (ListView) mRootView.findViewById(R.id.bridge_list2);
            mListView.setOnItemClickListener(this);
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hueのインスタンスの取得.
        mPhHueSDK = PHHueSDK.create();

        // アプリ名の登録.
        mPhHueSDK.setDeviceName(HueControl.APNAME);

        // HueブリッジからのCallbackを受け取るためのリスナーを登録.
        mPhHueSDK.getNotificationManager().registerSDKListener(mListener);

        // カスタムAdapterの作成.
        mAdapter = new CustomAdapter(this.getActivity().getBaseContext(), mPhHueSDK.getAccessPointsFound());
        mListView.setAdapter(mAdapter);

        // アクセスポイントのキャッシュを取得.
        mPhHueSDK.getAccessPointsFound();

        // ローカルBridgeのUPNP Searchを開始する.
        doBridgeSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // リスナーを解除
        if (mListener != null) {
            mPhHueSDK.getNotificationManager().unregisterSDKListener(mListener);
        }
    }

    /**
     * ローカルBridgeのUPNP Searchを開始する.
     */
    public void doBridgeSearch() {

        PHBridgeSearchManager sm = (PHBridgeSearchManager) mPhHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);

        // ローカルBridgeのUPNP Searchを開始
        sm.search(true, true);
    }

    // hueブリッジのNotificationを受け取るためのリスナー.
    public PHSDKListener mListener = new PHSDKListener() {

        @Override
        public void onBridgeConnected(final PHBridge b) {
        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
        }

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {
            if (accessPoint != null && accessPoint.size() > 0) {

                mPhHueSDK.getAccessPointsFound().clear();
                mPhHueSDK.getAccessPointsFound().addAll(accessPoint);

                // ListViewに描画.
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateData(mPhHueSDK.getAccessPointsFound());
                        mProgressView.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int arg0, String arg1) {
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> arg0) {
        }
    };

    @Override
    public void onClick(final View v) {

        // 検索処理を再度実行.
        mProgressView.setVisibility(View.VISIBLE);
        doBridgeSearch();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {

            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit);

            // 選択されたアクセスポイントからMacアドレス, IPアドレスを取得.
            PHAccessPoint mAccessPoint = (PHAccessPoint) mAdapter.getItem(position);

            // 次のFragmentに遷移.
            transaction.replace(R.id.fragment_frame, HueFragment02.newInstance(mAccessPoint));
            transaction.commit();

        } catch (Exception e) {

        }
    }

    /**
     * カスタムAdapter.
     */
    private class CustomAdapter extends BaseAdapter {

        private final Context mContext;
        private List<PHAccessPoint> mAccessPoint;

        public CustomAdapter(Context context, List<PHAccessPoint> accessPoint) {
            this.mContext = context;
            this.mAccessPoint = accessPoint;
        }

        public void updateData(List<PHAccessPoint> accessPoint) {
            this.mAccessPoint = accessPoint;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.hue_list, parent, false);

            TextView mTextView = (TextView) rowView.findViewById(R.id.row_textview1);

            try {
                String listTitle = mAccessPoint.get(position).getMacAddress() + "("
                        + mAccessPoint.get(position).getIpAddress() + ")";
                mTextView.setText(listTitle);
            } catch (Exception e) {
            }

            return rowView;
        }

        @Override
        public int getCount() {
            return mAccessPoint.size();
        }

        @Override
        public Object getItem(int position) {
            return mAccessPoint.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
