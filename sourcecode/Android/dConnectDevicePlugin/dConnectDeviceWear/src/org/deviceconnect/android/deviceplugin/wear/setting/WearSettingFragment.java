/*
 WearSettingFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.setting;

import org.deviceconnect.android.deviceplugin.wear.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 設定用Fragment.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearSettingFragment extends Fragment implements OnClickListener {

    /** Debug Tag. */
    private static final String TAG = "WEAR";

    /** context. */
    Activity mActivity;

    /** PluginID. */
    String mPluginId;

    /** ImageView. */
    private ImageView mImageView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        // Activityを取得
        mActivity = this.getActivity();

        // Positionを取得
        Bundle mBundle = getArguments();
        int mPagePosition = mBundle.getInt("position", 0);

        int mPageLayoutId = this.getResources().getIdentifier("wear_setting_" + mPagePosition, "layout",
                getActivity().getPackageName());

        View mView = inflater.inflate(mPageLayoutId, container, false);

        if (mPagePosition == 0) {
            mImageView = (ImageView) mView.findViewById(R.id.dconnect_settings_googleplay);
            mImageView.setOnClickListener(this);

        }

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mImageView)) {
            Uri uri = Uri.parse("market://details?id=com.google.android.wearable.app");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
