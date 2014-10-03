package com.nttdocomo.android.dconnect.client.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.preference.PreferenceFragment;

import com.nttdocomo.android.dconnect.client.fragment.OpenSourceLicenseFragment.OpenSourceSoftware;
import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.activity.SettingsActivity;

/**
 * 設定画面フラグメント.
 */
public abstract class AbstractSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    /**
     * OSSダイアログフラグメント.
     */
    private OpenSourceLicenseFragment mOssFragment;

    /**
     * プライバシーポリシーダイアログフラグメント.
     */
    private TextDialogFragment mPrivacyPolicyFragment;

    /**
     * 利用規約ダイアログフラグメント.
     */
    private TextDialogFragment mTermsOfServiceFragment;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Bundle args = new Bundle();
        args.putParcelableArrayList(OpenSourceLicenseFragment.EXTRA_OSS,
                getOpenSourceSoftware());
        mOssFragment = new OpenSourceLicenseFragment();
        mOssFragment.setArguments(args);

        Bundle policyArgs = new Bundle();
        policyArgs.putInt(Intent.EXTRA_TITLE, R.string.privacy_policy);
        policyArgs.putInt(Intent.EXTRA_TEXT, R.raw.privacypolicy);
        mPrivacyPolicyFragment = new TextDialogFragment();
        mPrivacyPolicyFragment.setArguments(policyArgs);

        Bundle tosArgs = new Bundle();
        tosArgs.putInt(Intent.EXTRA_TITLE, R.string.terms_of_service);
        tosArgs.putInt(Intent.EXTRA_TEXT, R.raw.termsofservice);
        mTermsOfServiceFragment = new TextDialogFragment();
        mTermsOfServiceFragment.setArguments(tosArgs);

        PreferenceScreen versionPreferences = (PreferenceScreen)
                getPreferenceScreen().findPreference(
                        getString(R.string.key_settings_about_appinfo));
        try {
            versionPreferences.setSummary((getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        CheckBoxPreference checkBoxSslPreferences = (CheckBoxPreference)
                getPreferenceScreen().findPreference(
                        getString(R.string.key_settings_dconn_ssl));
        checkBoxSslPreferences.setOnPreferenceChangeListener(this);

        EditTextPreference editHostPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_host));
        editHostPreferences.setOnPreferenceChangeListener(this);
        editHostPreferences.setSummary(editHostPreferences.getText());

        EditTextPreference editPortPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_port));
        editPortPreferences.setOnPreferenceChangeListener(this);
        editPortPreferences.setSummary(editPortPreferences.getText());

        // Local OAuth設定
        SettingsActivity activity = (SettingsActivity) getActivity();

        EditTextPreference editClientIdPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_client_id));
        editClientIdPreferences.setSummary(activity.getClientId());

        EditTextPreference editClientSecretPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_client_secret));
        editClientSecretPreferences.setSummary(activity.getClientSecret());

        EditTextPreference editAccessTokenPreferences = (EditTextPreference)
                getPreferenceScreen().findPreference(getString(R.string.key_settings_dconn_access_token));
        editAccessTokenPreferences.setSummary(activity.getAccessToken());
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {

        if (preference instanceof EditTextPreference) {
            ((EditTextPreference) preference).setSummary(newValue.toString());
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(
            final PreferenceScreen preferenceScreen, final Preference preference) {
        boolean result = super.onPreferenceTreeClick(preferenceScreen, preference);

        if (getString(R.string.key_settings_about_oss).equals(preference.getKey())) {
            mOssFragment.show(getFragmentManager(), null);
        } else if (getString(R.string.key_settings_about_privacypolicy).equals(
                preference.getKey())) {
            mPrivacyPolicyFragment.show(getFragmentManager(), null);
        } else if (getString(R.string.key_settings_about_tos).equals(preference.getKey())) {
            mTermsOfServiceFragment.show(getFragmentManager(), null);
        }

        return result;
    }

    /**
     * オープンソースソフトウェアリストを取得する.
     * @return ソフトウェアリスト
     * @return
     */
    protected abstract ArrayList<OpenSourceSoftware> getOpenSourceSoftware();

}
