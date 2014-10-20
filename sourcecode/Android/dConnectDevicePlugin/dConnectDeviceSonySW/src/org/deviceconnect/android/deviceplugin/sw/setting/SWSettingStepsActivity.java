/*
 SettingStepsActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.deviceconnect.android.deviceplugin.sw.R;
import org.deviceconnect.android.deviceplugin.sw.SWConstants;

import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

/**
 * SWデバイスプラグインの設定手順説明画面.
 */
public class SWSettingStepsActivity extends DConnectSettingPageFragmentActivity {

    /**
     * 接続するターゲットデバイスモデル.
     */
    private int mTargetModel = SWConstants.SW_MODEL_SW2;
    
    /**
     * ホストアプリケーションタイトルテキスト.
     */
    private static TextView hostApplicationTitleText;

    /** フラグメント一覧. */
    private List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null && savedInstanceState.getInt(SWConstants.EXTRA_SW_MODEL, -1) == -1) {
            mTargetModel = savedInstanceState.getInt(SWConstants.EXTRA_SW_MODEL);
        } else {
            Intent intent = getIntent();
            mTargetModel = intent.getIntExtra(SWConstants.EXTRA_SW_MODEL, SWConstants.SW_MODEL_UNKNOWN);
        }
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putInt(SWConstants.EXTRA_SW_MODEL, mTargetModel);
    }

    @Override
    public int getPageCount() {
        return SWConstants.TUTORIAL_PAGE_NNMBER;
    }

    @Override
    public Fragment createPage(final int position) {
        if (fragments.size() == 0) {
            BaseFragment f1 = new AppInstrallationFragment();
            f1.setTargetModel(mTargetModel);
            f1.setActivity(this);
            BaseFragment f2 = new BluetoothActivationFragment();
            f2.setTargetModel(mTargetModel);
            f2.setActivity(this);
            BaseFragment f3 = new BluetoothSettingPromptFragment();
            f3.setTargetModel(mTargetModel);
            f3.setActivity(this);
            BaseFragment f4 = new BluetoothSettingFinishFragment();
            f4.setTargetModel(mTargetModel);
            f4.setActivity(this);
            fragments.add(f1);
            fragments.add(f2);
            fragments.add(f3);
            fragments.add(f4);
        }
        return fragments.get(position);
    }
    /**
     * チュートリアルページの取得.
     * @param position position
     */
    public void setCurrentPage(final int position) {
        getViewPager().setCurrentItem(position, true);
    }

    public static class BaseFragment extends Fragment {
        
        /** ロガー. */
        protected Logger mLogger = Logger.getLogger(SWConstants.LOGGER_NAME);
        /**
         * 接続デバイスターゲットモデル.
         */
        int mTargetModel;
        /**
         * チュートリアルページアクティビティ.
         */
        SWSettingStepsActivity mActivity;
        /**
         * 接続するデバイスを設定する.
         * @param targetModel targetModel
         */
        public void setTargetModel(final int targetModel) {
            mTargetModel = targetModel;
        }
        /**
         * アクティビティを設定する.
         * @param activity activity
         */
        public void setActivity(final SWSettingStepsActivity activity) {
            mActivity = activity;
        }
    }

    /**
     * 手順1 必須アプリのインストール.
     */
    public static class AppInstrallationFragment extends BaseFragment {

        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            final int layoutId;
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                layoutId = R.layout.dconnect_settings_step_1_sw1;
                break;
            case SWConstants.SW_MODEL_SW2:
                layoutId = R.layout.dconnect_settings_step_1_sw2;
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                return null;
            }
            View root = inflater.inflate(layoutId, container, false);
            
            hostApplicationTitleText = (TextView) 
                    root.findViewById(R.id.dconnect_settings_step_1_HostApplication_title);
            
            ImageButton installSmartconnectButton = (ImageButton) 
                    root.findViewById(R.id.dconnect_settings_step_1_button_install_smartconnect);
            installSmartconnectButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Uri uri = Uri.parse("market://details?id=" + SWConstants.PACKAGE_SMART_CONNECT);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            ImageButton installWatchButton = (ImageButton) 
                    root.findViewById(R.id.dconnect_settings_step_1_button_install_plugin);
            installWatchButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String packageName = null;
                    switch (mTargetModel) {
                    case SWConstants.SW_MODEL_SW1:
                        packageName = SWConstants.PACKAGE_SMART_WATCH;
                        break;
                    case SWConstants.SW_MODEL_SW2:
                        packageName = SWConstants.PACKAGE_SMART_WATCH_2;
                        break;
                    default:
                        // エラー出力
                        mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                        break;
                    }
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                hostApplicationTitleText.setText(SWConstants.APP_NAME_SMART_WATCH + "：");
                break;
            case SWConstants.SW_MODEL_SW2:
                hostApplicationTitleText.setText(SWConstants.APP_NAME_SMART_WATCH_2 + "：");
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                break;
            }
            
            return root;
        }
        @Override
        public void onResume() {
            super.onResume();
            
            mLogger.entering("SWSettingStepsActivity", "onResume");
            checkPackage();
            mLogger.exiting(this.getClass().getName(), "onResume");
        }
        
        /**
         * 必須アプリケーションパッケージのインストール状態で文言を変更する.
         */
        public void checkPackage() {
            
            String smartConnectId = SWConstants.PACKAGE_SMART_CONNECT;
            String smartWatchId = "";
            String smartConnectText = "";
            String smartWatchText = "";
            
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                smartWatchId = SWConstants.PACKAGE_SMART_WATCH;
                break;
            case SWConstants.SW_MODEL_SW2:
                smartWatchId = SWConstants.PACKAGE_SMART_WATCH_2;
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                break;
            }

            try {
                PackageManager pm = getActivity().getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(
                        smartConnectId, PackageManager.GET_META_DATA);
                mLogger.fine(appInfo + "");
                smartConnectText = "インストール済みです";
            } catch (NameNotFoundException e) {
                // インストールされていない場合は例外が発生
                mLogger.fine(smartConnectId + " is none");
                smartConnectText = "インストールされていません";
            }
            
            try {
                PackageManager pm2 = getActivity().getPackageManager();
                ApplicationInfo appInfo2 = pm2.getApplicationInfo(
                        smartWatchId, PackageManager.GET_META_DATA);
                mLogger.fine(appInfo2 + "");
                smartWatchText = "インストール済みです";
                
            } catch (NameNotFoundException e) {
                // インストールされていない場合は例外が発生
                mLogger.fine(smartWatchId + " is none");
                smartWatchText = "インストールされていません";
            }
            
            ((TextView) getView().findViewById(
                    R.id.dconnect_settings_step_1_SmartConnect_description)).setText(smartConnectText);
            ((TextView) getView().findViewById(
                    R.id.dconnect_settings_step_1_HostApplication_description)).setText(smartWatchText);
        }
    }

    /**
     * 手順2 SonyWatchをBluetooth検出可能にする.
     */
    public static class BluetoothActivationFragment extends BaseFragment {
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            final int layoutId;
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                layoutId = R.layout.dconnect_settings_step_2_sw1;
                break;
            case SWConstants.SW_MODEL_SW2:
                layoutId = R.layout.dconnect_settings_step_2_sw2;
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                return null;
            }
            View root = inflater.inflate(layoutId, container, false);
            return root;
        }
    }

    /**
     * 手順3 端末標準の設定画面でSonyWatchとのペアリングを実行する.
     */
    public static class BluetoothSettingPromptFragment extends BaseFragment {
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            final int layoutId;
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                layoutId = R.layout.dconnect_settings_step_3_sw1;
                break;
            case SWConstants.SW_MODEL_SW2:
                layoutId = R.layout.dconnect_settings_step_3_sw2;
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                return null;
            }
            View root = inflater.inflate(layoutId, container, false);
            Button button = (Button) root.findViewById(R.id.dconnect_settings_step_3_button_launch_bluetooth_setting);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }
            });
            return root;
        }
    }

    /**
     * 手順4 設定完了画面.
     */
    public static class BluetoothSettingFinishFragment extends BaseFragment {
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            final int layoutId;
            switch (mTargetModel) {
            case SWConstants.SW_MODEL_SW1:
                layoutId = R.layout.dconnect_settings_step_4_sw1;
                break;
            case SWConstants.SW_MODEL_SW2:
                layoutId = R.layout.dconnect_settings_step_4_sw2;
                break;
            default:
                // エラー出力
                mLogger.warning("Failed to detect the model of SW: " + this.getClass().getName());
                return null;
            }
            View root = inflater.inflate(layoutId, container, false);
            return root;
        }
    }
}
