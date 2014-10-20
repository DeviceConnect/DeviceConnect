/*
SonyCameraBaseFragment
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sonycamera.activity;

import org.deviceconnect.android.deviceplugin.sonycamera.R;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

/**
 * Sony Camera ベースフラグメント.
 * @author NTT DOCOMO, INC.
 */
public class SonyCameraBaseFragment extends Fragment {

    /** 検索中のダイアログ. */
    private ProgressDialog mDialog;

    /**
     * デバイスIDを設定する.
     * 
     * @param id デバイスプラグインID
     */
    public void setDeviceId(final String id) {
        ((SonyCameraSettingActivity) getActivity()).setDeviceId(id);
    }

    /**
     * デバイスIDを取得する.
     * 
     * @return デバイスID
     */
    public String getDeviceId() {
        return ((SonyCameraSettingActivity) getActivity()).getDeviceId();
    }

    /**
     * プログレスバーが表示されているか.
     * 
     * @return 表示されている場合はtrue,それ以外はfalse
     */
    public boolean isShowProgressDialog() {
        return mDialog != null;
    }

    /**
     * プログレスバーを表示する.
     */
    public void showProgressDialog() {
        if (mDialog != null) {
            return;
        }
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(R.string.sonycamera_proccessing);
        mDialog.setMessage(getString(R.string.sonycamera_now_loading));
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(true);
        mDialog.show();
    }

    /**
     * プログレスバーを非表示にする.
     */
    public void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
