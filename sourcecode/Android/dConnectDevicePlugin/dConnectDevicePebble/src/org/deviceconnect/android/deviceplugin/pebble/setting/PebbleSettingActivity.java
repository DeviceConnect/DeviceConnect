/*
 PebbleSettingActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.setting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.deviceconnect.android.deviceplugin.pebble.R;
import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

/**
 * Pebbleの設定画面.
 * @author NTT DOCOMO, INC.
 */
public class PebbleSettingActivity extends DConnectSettingPageFragmentActivity {
    /** googleStorId. */
    private static final String PACKAGE_PEBBLE = "com.getpebble.android";

    /** Pebbleアプリのインストールに使用する、Pebble社管理アプリのコンポーネント. */
    private static final String PEBBLE_LAUNCH_COMPONENT = "com.getpebble.android";
    /** Pebbleアプリのインストールに使用する、Pebble社管理アプリのActivity. */
    private static final String PEBBLE_LAUNCH_ACTIVITY = "com.getpebble.android.ui.UpdateActivity";
    
    /**
     * フラグメント一覧.
     */
    private List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getPageCount() {
        return 5;
    }

    @Override
    public Fragment createPage(final int position) {
        if (fragments.size() == 0) {
            fragments.add(new BluetoothActivationFragment());
            fragments.add(new BluetoothSettingPromptFragment());
            fragments.add(new A_AppInstrallationFragment());
            fragments.add(new P_AppInstrallationFragment());
            fragments.add(new SettingFinishFragment());
        }
        return fragments.get(position);
    }

    /**
     * カレントページをセットする.
     * @param position
     */
    public void setCurrentPage(final int position) {
        getViewPager().setCurrentItem(position, true);
    }

    /**
     * BaseFragment クラス.
     *
     */
    public class BaseFragment extends Fragment {
    }

    /**
     * 手順1 PebbleをBluetooth検出可能にする.
     */
    public class BluetoothActivationFragment extends BaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_1, container, false);
            return root;
        }
    }

    /**
     * 手順2 端末標準の設定画面でPebbleとのペアリングを実行する.
     */
    public class BluetoothSettingPromptFragment extends BaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_2, container, false);
            Button button = (Button) root.findViewById(R.id.dconnect_settings_step_2_button_launch_bluetooth_setting);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }
            });
            return root;
        }
    }

    /**
     * 手順3 必須アプリのインストール.
     */
    public class A_AppInstrallationFragment extends BaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_3, container, false);
            Button btn = (Button) root.findViewById(R.id.dconnect_settings_step_3_button_install_pebble);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("market://details?id=" + PACKAGE_PEBBLE);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            return root;
        }
    }

    /**
     * 手順4 必須アプリのインストール.
     */
    public class P_AppInstrallationFragment extends BaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_4, container, false);
            Button btn = (Button) root.findViewById(R.id.dconnect_settings_step_4_button_install_plugin);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    installPebbleApprication(getPbwFileName());
                }
            });
            return root;
        }
    }
    
    /**
     * uri で指定した Pebble側アプリケーションをインストールする.
     * @param uri
     */
    private void installPebbleApprication(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName(PEBBLE_LAUNCH_COMPONENT, PEBBLE_LAUNCH_ACTIVITY);
        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        if (apps.size() > 0) {
            startActivity(intent);
            return;
        }
        Toast.makeText(this, R.string.page04_error01, Toast.LENGTH_LONG).show();
    }

    /**
     * リソースから pbw ファイルを作成し、その uri を返す.
     * @return uri を返す.
     */
    @SuppressWarnings("deprecation")
    private Uri getPbwFileName() {
        File file = this.getFileStreamPath("test.pbw");
        try {
            fileCopy(getResources().openRawResource(R.raw.dc_pebble), openFileOutput(file.getName(), MODE_WORLD_READABLE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    /**
     * 手順最終. 
     *
     */
    public class SettingFinishFragment extends BaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_finish, container, false);
            return root;
        }
    }

    /**
     * ファイルをコピーする.
     * @param is 入力
     * @param os 出力
     * @throws IOException
     */
    private void fileCopy(InputStream is, OutputStream os)throws IOException{  
        byte[] b = new byte[1024];
        while(is.read(b) > 0) {
            os.write(b);
        }
        is.close();
        os.close();
    }
}
