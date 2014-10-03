package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.fragment.SmartDevicePreferenceFragment;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * コネクションサービスフラグメント.
 */
public class ConnectProfileFragment extends SmartDevicePreferenceFragment {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("dconnect.uiapp");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create layout(preference screen)
        addPreferencesFromResource(R.xml.connection_service);

        // set toggle click listener
        ((SwitchPreference) findPreference(getString(R.string.key_connection_service_wifi)))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(
                            final Preference preference, final Object newValue) {
                        setEnabledPreference(preference.getKey(), false);

                        (new ConnectEnableLoader()).execute(
                                preference.getKey(), newValue);
                        return true;
                    }
                });

        ((SwitchPreference) findPreference(getString(R.string.key_connection_service_bluetooth)))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(
                            final Preference preference, final Object newValue) {
                        setEnabledPreference(preference.getKey(), false);
                        setEnabledPreference(getString(
                                R.string.key_connection_service_bluetooth_discoverable), false);

                        (new ConnectEnableLoader()).execute(
                                preference.getKey(), newValue);
                        return true;
                    }
                });

        ((SwitchPreference) findPreference(getString(R.string.key_connection_service_ble)))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(
                            final Preference preference, final Object newValue) {
                        setEnabledPreference(preference.getKey(), false);

                        (new ConnectEnableLoader()).execute(
                                preference.getKey(), newValue);
                        return true;
                    }
                });

        // load current connection statuses
        (new ConnectStatusLoader(getString(R.string.key_connection_service_wifi))).execute();
        (new ConnectStatusLoader(getString(R.string.key_connection_service_bluetooth))).execute();
        (new ConnectStatusLoader(getString(R.string.key_connection_service_ble))).execute();
    }

    @Override
    public View onCreateView(final LayoutInflater paramLayoutInflater,
            final ViewGroup paramViewGroup, final Bundle paramBundle) {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        return view;
    }

    @Override
    public boolean onPreferenceTreeClick(
            final PreferenceScreen preferenceScreen, final Preference preference) {

        boolean result;

        if (preference.getKey().equals(getString(
                R.string.key_connection_service_bluetooth_discoverable))) {
            (new AsyncTask<Void, Integer, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {

                    URIBuilder uriBuilder = new URIBuilder();
                    uriBuilder.setProfile(ConnectProfileConstants.PROFILE_NAME);
                    uriBuilder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
                    uriBuilder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
                    uriBuilder.addParameter(
                            DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                    uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                    try {
                        getDConnectClient().execute(new HttpPut(uriBuilder.build()));
                    } catch (IOException e) {
                        mLogger.warning(e.toString());
                    } catch (URISyntaxException e) {
                        mLogger.warning(e.toString());
                    }

                    return null;
                }
            }).execute();
        }

        result = super.onPreferenceTreeClick(preferenceScreen, preference);

        return result;
    }

    /**
     * SwitchPreferenceの有効状態を設定する.
     * @param key プリファレンスキー
     * @param enabled 有効状態
     */
    private void setEnabledPreference(final String key, final boolean enabled) {
        findPreference(key).setEnabled(enabled);
    }

    /**
     * SwitchPreferenceの有効状態を反転する.
     * @param key プリファレンスキー
     */
    private void toggleCheckedPreference(final String key) {
        SwitchPreference pref = (SwitchPreference) findPreference(key);
        pref.setChecked(!pref.isChecked());
    }

    /**
     * SwitchPreferenceのチェック状態を設定する.
     * @param key プリファレンスキー
     * @param checked チェック状態
     */
    private void setCheckedSwitchPreference(final String key, final boolean checked) {
        ((SwitchPreference) findPreference(key)).setChecked(checked);
    }

    /**
     * コネクションステータス変更ローダー.
     */
    private class ConnectEnableLoader extends AsyncTask<Object, Integer, DConnectMessage> {

        /**
         * Preferenceキー.
         */
        private String mKey;

        /**
         * 変更先ステータス.
         */
        private boolean mEnabled;

        @Override
        protected DConnectMessage doInBackground(final Object... args) {

            DConnectMessage message;

            mKey = (String) args[0];
            mEnabled = (Boolean) args[1];

            URIBuilder uriBuilder = new URIBuilder();

            try {
                if (mKey.equals(getString(R.string.key_connection_service_wifi))) {
                    uriBuilder.setPath("/connect/wifi");
                } else if (mKey.equals(getString(R.string.key_connection_service_bluetooth))) {
                    uriBuilder.setPath("/connect/bluetooth");
                } else if (mKey.equals(getString(R.string.key_connection_service_ble))) {
                    uriBuilder.setPath("/connect/ble");
                }
                uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                HttpUriRequest request = null;
                if (mEnabled) {
                    request = new HttpPut(uriBuilder.build());
                } else {
                    request = new HttpDelete(uriBuilder.build());
                }

                HttpResponse response = getDConnectClient().execute(
                        getDefaultHost(), request);
                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (IOException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            } catch (URISyntaxException e) {
                message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            }

            return message;

        }

        @Override
        protected void onPostExecute(final DConnectMessage result) {
            super.onPostExecute(result);

            if (getActivity().isFinishing()) {
                return;
            }

            if (!isAdded()) {
                return;
            }

            if (result.getInt(DConnectMessage.EXTRA_RESULT) != DConnectMessage.RESULT_OK) {
                // revert switch status
                toggleCheckedPreference(mKey);
            } else {
                // bluetooth enabled/disabled is success
                if (mKey.equals(getString(R.string.key_connection_service_bluetooth))) {
                    // change bluetooth discovery preference status
                    setEnabledPreference(getString(
                            R.string.key_connection_service_bluetooth_discoverable), mEnabled);
                }
            }

            setEnabledPreference(mKey, true);

        }

    }

    /**
     * コネクションステータスローダー.
     */
    private class ConnectStatusLoader extends AsyncTask<String, Integer, Boolean> {

        /**
         * プリファレンスキー.
         */
        private String mKey;

        /**
         * コンストラクタ.
         * @param key プリファレンスキー
         */
        public ConnectStatusLoader(final String key) {
            mKey = key;
        }

        @Override
        protected void onPreExecute() {
            setEnabledPreference(mKey, false);

            setEnabledPreference(getString(
                    R.string.key_connection_service_bluetooth), false);
            setEnabledPreference(getString(
                    R.string.key_connection_service_bluetooth_discoverable), false);

            setEnabledPreference(getString(
                    R.string.key_connection_service_ble), false);
        }

        @Override
        protected Boolean doInBackground(final String... params) {

            boolean result = true;

            URIBuilder uriBuilder = new URIBuilder();

            if (mKey.equals(getString(R.string.key_connection_service_wifi))) {
                uriBuilder.setPath("/connect/wifi");
            } else if (mKey.equals(getString(R.string.key_connection_service_bluetooth))) {
                uriBuilder.setPath("/connect/bluetooth");
            } else if (mKey.equals(getString(R.string.key_connection_service_ble))) {
                uriBuilder.setPath("/connect/ble");
            } else {
                result = false;
            }

            if (result) {
                uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());

                try {
                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpGet(uriBuilder.build()));
                    DConnectMessage message = (new HttpMessageFactory())
                            .newDConnectMessage(response);
                    if (message.getInt(DConnectMessage.EXTRA_RESULT)
                            == DConnectMessage.RESULT_OK) {
                        result = message.getBoolean("enabled");
                    } else {
                        result = false;
                    }
                } catch (IOException e) {
                    result = false;
                } catch (URISyntaxException e) {
                    result = false;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            if (getActivity().isFinishing()) {
                return;
            }

            if (!isAdded()) {
                return;
            }

            if (!mKey.equals(getString(R.string.key_connection_service_ble))) {
                setEnabledPreference(mKey, true);
                setCheckedSwitchPreference(mKey, result);

                if (mKey.equals(getString(R.string.key_connection_service_bluetooth))) {
                    setEnabledPreference(getString(
                            R.string.key_connection_service_bluetooth_discoverable), result);
                }
            }

        }

    }

}
