/*
 ChromeCastSettingFragmentPage3.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.setting;

import org.deviceconnect.android.deviceplugin.chromecast.BuildConfig;
import org.deviceconnect.android.deviceplugin.chromecast.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

/**
 * チュートリアル画面
 * <p>
 * 画面を作成する
 * </p>
 * Chromecastの設定
 * 
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastSettingFragmentPage3 extends Fragment {
    private static final String packageName = "com.google.android.apps.chromecast.app";
    private int badgeWidth = 0;
    private int badgeHeight = 0;
    
    /**
     * Chromecast App (Google) のインストール状態を調べる
     * 
     * @param   context         コンテキスト
     * @return  インストール状態    （true: インストールされている, false: インストールされていない）
     */
    private boolean isApplicationInstalled(Context context) {
        boolean installed = false;
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            installed = true;
        } catch (NameNotFoundException e) {
            if(BuildConfig.DEBUG){
                e.printStackTrace();
            }
        }
        return installed;
    }

    /**
     * Chromecast App (Google) のインストール状態に応じて、Buttonの背景を変更する
     * 
     * @param   button  ボタン
     * @return  なし
     */
    private void changeButtonBackground(Button button) {
        if (isApplicationInstalled(button.getContext())) {
            button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            button.setBackgroundResource(R.drawable.button_blue);
            button.setText(getResources().getString(R.string.chromecast_settings_step_3_button));
        } else {
            button.setLayoutParams(new LayoutParams(badgeWidth, badgeHeight));
            button.setBackgroundResource(R.drawable.button_google_play);
            button.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        changeButtonBackground((Button) getActivity().findViewById(R.id.buttonChromecastSettingApp));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chromecast_settings_step_3, container, false);

        Button button = (Button) rootView.findViewById(R.id.buttonChromecastSettingApp);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent;
                if (isApplicationInstalled(v.getContext())) {
                    intent = v.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                }
                startActivity(intent);
            }
        });

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.button_google_play);
        badgeWidth = image.getWidth();
        badgeHeight = image.getHeight();
        image.recycle();
        
        button = (Button) rootView.findViewById(R.id.buttonChromecastSettingWifiRestart);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(false);
                wifi.setWifiEnabled(true);
            }
        });

        return rootView;
    }
}