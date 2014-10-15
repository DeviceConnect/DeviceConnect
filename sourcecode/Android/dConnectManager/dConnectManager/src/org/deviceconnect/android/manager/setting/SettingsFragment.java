/*
 SettingsFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.deviceconnect.android.manager.DConnectService;
import org.deviceconnect.android.manager.R;
import org.deviceconnect.android.manager.setting.OpenSourceLicenseFragment.OpenSourceSoftware;
import org.deviceconnect.android.observer.DConnectObservationService;
import org.deviceconnect.android.observer.receiver.ObserverReceiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

/**
 * 設定画面Fragment.
 * @author NTT DOCOMO, INC.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    /**
     * オープンソースソフトウェア.
     */
    private ArrayList<OpenSourceSoftware> mOpenSourceList;

    /** 乱数の最大値. */
    private static final int MAX_NUM = 10000;
    /** キーワードの桁数を定義. */
    private static final int DIGIT = 4;
    /** 10進数の定義. */
    private static final int DECIMAL = 10;
    /** SSL設定チェックボックス. */
    private CheckBoxPreference checkBoxSslPreferences;
    /** ポート設定テキストエディッタ. */
    private EditTextPreference editPortPreferences;
    /** LocalOAuth設定チェックボックス. */
    private CheckBoxPreference checkBoxOauthPreferences;
    /** 外部IP設定チェックボックス. */
    private CheckBoxPreference checkBoxExternalPreferences;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // SharedPreferenceをマルチプロセスでも動作する設定にする
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);

        // オープソースのリストを準備
        mOpenSourceList = new ArrayList<OpenSourceSoftware>();
        mOpenSourceList.add(OpenSourceLicenseFragment.createOpenSourceSoftware(
                "android-support-v4.jar", R.raw.andorid_support_v4));
        mOpenSourceList.add(OpenSourceLicenseFragment.createOpenSourceSoftware(
                "apache-mime4j-0.6.jar", R.raw.apache_mime4j));
        mOpenSourceList.add(OpenSourceLicenseFragment.createOpenSourceSoftware(
                "android-support-v4-preferencefragment", R.raw.android_support_v4_preferencefragment));
        mOpenSourceList.add(OpenSourceLicenseFragment.createOpenSourceSoftware(
                "Java WebSocket", R.raw.java_websocket));

        PreferenceScreen versionPreferences = (PreferenceScreen)
                getPreferenceScreen().findPreference(
                        getString(R.string.key_settings_about_appinfo));
        try {
            versionPreferences.setSummary((getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName));
        } catch (NameNotFoundException e) {
            throw new RuntimeException("could not get my package.");
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String keyword = sp.getString(getString(R.string.key_settings_dconn_keyword), null);
        if (keyword == null || keyword.length() <= 0) {
            keyword = createKeyword();
        }

        EditTextPreference editKeywordPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_keyword));
        editKeywordPreferences.setOnPreferenceChangeListener(this);
        editKeywordPreferences.setSummary(keyword);
        editKeywordPreferences.setDefaultValue(keyword);
        editKeywordPreferences.setText(keyword);
        editKeywordPreferences.shouldCommit();

        // SSLのON/OFF
        checkBoxSslPreferences = (CheckBoxPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_ssl));
        checkBoxSslPreferences.setOnPreferenceChangeListener(this);

        // ホスト名設定
        EditTextPreference editHostPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_host));
        editHostPreferences.setOnPreferenceChangeListener(this);
        editHostPreferences.setSummary(editHostPreferences.getText());

        // ポート番号設定
        editPortPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_port));
        editPortPreferences.setOnPreferenceChangeListener(this);
        editPortPreferences.setSummary(editPortPreferences.getText());

        // Local OAuthのON/OFF
        checkBoxOauthPreferences = (CheckBoxPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_local_oauth));
        checkBoxOauthPreferences.setOnPreferenceChangeListener(this);

        // グローバル設定のON/OFF
        checkBoxExternalPreferences = (CheckBoxPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_allow_external_ip));
        checkBoxExternalPreferences.setOnPreferenceChangeListener(this);

        editHostPreferences.setEnabled(false);
        checkBoxSslPreferences.setEnabled(!isDConnectServiceRunning());
        editPortPreferences.setEnabled(!isDConnectServiceRunning());
        checkBoxOauthPreferences.setEnabled(!isDConnectServiceRunning());
        checkBoxExternalPreferences.setEnabled(!isDConnectServiceRunning());
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // dConnectManagerの起動チェック
        SwitchPreference serverPreferences = (SwitchPreference)
                getPreferenceScreen()
                .findPreference(getString(R.string.key_settings_dconn_server_on_off));
        serverPreferences.setOnPreferenceChangeListener(this);
        serverPreferences.setChecked(isDConnectServiceRunning());
        
        // 監視サービスの起動チェック
        SwitchPreference observerPreferences = (SwitchPreference)
                getPreferenceScreen()
                .findPreference(getString(R.string.key_settings_dconn_observer_on_off));
        observerPreferences.setOnPreferenceChangeListener(this);
        observerPreferences.setChecked(isObservationServices());

        // 各dConnectManagerの設定
        checkBoxSslPreferences.setEnabled(!isDConnectServiceRunning());
        editPortPreferences.setEnabled(!isDConnectServiceRunning());
        checkBoxOauthPreferences.setEnabled(!isDConnectServiceRunning());
        checkBoxExternalPreferences.setEnabled(!isDConnectServiceRunning());
    }

    @Override
    public boolean onPreferenceChange(
            final Preference preference, final Object newValue) {
        final String key = preference.getKey();
        if (preference instanceof EditTextPreference) {
            if (getString(R.string.key_settings_dconn_port).equals(key)) {
                String value = newValue.toString();
                try {
                    // 入力値が整数かチェックする
                    Integer.parseInt(value);
                    ((EditTextPreference) preference).setSummary(value);
                } catch (NumberFormatException e) {
                    return true;
                }
            } else {
                ((EditTextPreference) preference).setSummary(newValue.toString());
            }
        } else if (preference instanceof SwitchPreference) {
            if (getString(R.string.key_settings_dconn_server_on_off).equals(key)) {
                SwitchPreference pref = ((SwitchPreference) preference);
                boolean checked = pref.isChecked();
                checkBoxSslPreferences.setEnabled(checked);
                checkBoxOauthPreferences.setEnabled(checked);
                checkBoxExternalPreferences.setEnabled(checked);
                editPortPreferences.setEnabled(checked);
                // dConnectManagerのON/OFF
                Intent intent = new Intent(getActivity(), DConnectService.class);
                if (!checked) {
                    getActivity().startService(intent);
                } else {
                    getActivity().stopService(intent);
                }
            } else if (getString(R.string.key_settings_dconn_observer_on_off).equals(key)) {
                SwitchPreference pref = ((SwitchPreference) preference);
                boolean checked = pref.isChecked();
                // 監視サービスのON/OFF
                Intent intent = new Intent();
                intent.setClass(getActivity(), ObserverReceiver.class);
                if (!checked) {
                    intent.setAction(DConnectObservationService.ACTION_START);
                } else {
                    intent.setAction(DConnectObservationService.ACTION_STOP);
                }
                getActivity().sendBroadcast(intent);
            }
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(
            final PreferenceScreen preferenceScreen, final Preference preference) {
        boolean result = super.onPreferenceTreeClick(preferenceScreen, preference);

        // 各説明をダイアログで表示
        if (getString(R.string.key_settings_open_source_licenses).equals(preference.getKey())) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(OpenSourceLicenseFragment.EXTRA_OSS, mOpenSourceList);
            OpenSourceLicenseFragment fragment = new OpenSourceLicenseFragment();
            fragment.setArguments(args);
            fragment.show(getFragmentManager(), null);
        } else if (getString(R.string.key_settings_about_privacypolicy).equals(preference.getKey())) {
            Bundle policyArgs = new Bundle();
            policyArgs.putInt(Intent.EXTRA_TITLE, R.string.activity_settings_privacy_policy);
            policyArgs.putInt(Intent.EXTRA_TEXT, R.raw.privacypolicy);
            TextDialogFragment fragment = new TextDialogFragment();
            fragment.setArguments(policyArgs);
            fragment.show(getFragmentManager(), null);
        } else if (getString(R.string.key_settings_about_tos).equals(preference.getKey())) {
            Bundle tosArgs = new Bundle();
            tosArgs.putInt(Intent.EXTRA_TITLE, R.string.activity_settings_terms_of_service);
            tosArgs.putInt(Intent.EXTRA_TEXT, R.raw.termsofservice);
            TextDialogFragment fragment = new TextDialogFragment();
            fragment.setArguments(tosArgs);
            fragment.show(getFragmentManager(), null);
        }

        return result;
    }

    /**
     * DConnectServiceが動作しているか確認する.
     * @return 起動中の場合はtrue、それ以外はfalse
     */
    private boolean isDConnectServiceRunning() {
        return isServiceRunning(getActivity(), DConnectService.class);
    }

    /**
     * サービスに起動確認を行う.
     * @param c コンテキスト
     * @param cls クラス
     * @return 起動中の場合はtrue、それ以外はfalse
     */
    private boolean isServiceRunning(final Context c, final Class<?> cls) {
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo i : runningService) {
            if (cls.getName().equals(i.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * キーワードを作成する.
     * @return キーワード
     */
    private String createKeyword() {
        StringBuilder builder = new StringBuilder();
        builder.append("DCONNECT-");
        int rand = Math.abs(new Random().nextInt() % MAX_NUM);
        for (int i = 0; i < DIGIT; i++) {
            int r = rand % DECIMAL;
            builder.append(r);
            rand /= DECIMAL;
        }
        return builder.toString();
    }

    /**
     * dConnectManagerの監視サービスの起動状態を取得する.
     * @return 起動している場合はtrue、それ以外はfalse
     */
    private boolean isObservationServices() {
        return isServiceRunning(getActivity(), DConnectObservationService.class);
    }
}
