/*
 SWSettingMainActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.setting;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.deviceconnect.android.deviceplugin.sw.R;
import org.deviceconnect.android.deviceplugin.sw.SWConstants;

import org.deviceconnect.android.ui.activity.DConnectSettingPageActivity;

/**
 * SonyWatchDevicePluginの設定画面.
 * <p>
 * 実際に使用されるSonyWatchに応じた設定手順説明画面への導線を提供する.
 * </p>
 */
public class SWSettingMainActivity extends DConnectSettingPageActivity {
    /**
     * View.
     */
    private View mView;

    @Override
    public int getPageCount() {
        return 1;
    }

    @Override
    public View createPage(final int position) {
        if (mView == null) {
            mView = getLayoutInflater().inflate(R.layout.dconnect_settings_main, null);
            Button buttonSw2 = (Button) mView.findViewById(R.id.dconnect_settings_button_sw2);
            buttonSw2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    
                    startActivity(SWConstants.SW_MODEL_SW2);
                    finish();
                }
            });
            Button buttonSw1 = (Button) mView.findViewById(R.id.dconnect_settings_button_sw1);
            buttonSw1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(SWConstants.SW_MODEL_SW1);
                    finish();
                }
            });
        }
        return mView;
    }
/**
 * 
 * @param targetModel targetModel.
 */
    public void startActivity(final int targetModel) {
        Intent intent = new Intent(getApplicationContext(), SWSettingStepsActivity.class);
        intent.putExtra(SWConstants.EXTRA_SW_MODEL, targetModel);
        startActivity(intent);
    }

}
