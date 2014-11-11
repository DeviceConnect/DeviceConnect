/*
 ChromeCastSettingStepsActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.setting;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.deviceplugin.chromecast.ChromeCastService;
import org.deviceconnect.android.deviceplugin.chromecast.R;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastApplication;
import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

/**
 * チュートリアル画面（ステップ）
 * <p>
 * 画面を作成する
 * </p>
 * 
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastSettingStepsActivity extends DConnectSettingPageFragmentActivity {

    private List<Fragment> fragments = new ArrayList<Fragment>();

    ChromeCastService chromeCastService = null;
    ChromeCastApplication application = null;

    @Override
    public int getPageCount() {
        return 3;
    }

    @Override
    public Fragment createPage(final int position) {
        if (fragments.size() == 0) {
            fragments.add(new FragmentPage1());
            fragments.add(new FragmentPage2());
            fragments.add(new FragmentPage3());
        }
        return fragments.get(position);
    }

    /**
     * 指定されたページに遷移する
     * 
     * @param   position
     * @return  なし
     */
    public void setCurrentPage(final int position) {
        getViewPager().setCurrentItem(position, true);
    }

    /**
     * フラグメント (Page1)
     * <p>
     * Chromecastの接続
     * </p>
     */
    private class FragmentPage1 extends Fragment {
        @Override
        public View onCreateView(final LayoutInflater inflater,
                final ViewGroup container, final Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.chromecast_settings_step_1, container, false);
            return root;
        }
    }

    /**
     * フラグメント (Page2)
     * <p>
     * 電源の接続
     * </p>
     */
    private class FragmentPage2 extends Fragment {
        @Override
        public View onCreateView(final LayoutInflater inflater,
                final ViewGroup container, final Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.chromecast_settings_step_2, container, false);
            return root;
        }
    }

    /**
     * フラグメント (Page3)
     * <p>
     * Chromecastの設定
     * </p>
     */
    private class FragmentPage3 extends Fragment {
        
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
                e.printStackTrace();
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
            changeButtonBackground((Button) findViewById(R.id.buttonChromecastSettingApp));
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

            return rootView;
        }
    }
}
