package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.fragment.SmartDevicePreferenceFragment;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.BatteryProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * コネクションサービスフラグメント.
 */
public class BatteryProfileFragment extends SmartDevicePreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create layout(preference screen)
        addPreferencesFromResource(R.xml.battery_profile);
        // load current battery statuses
        (new BatteryStatusLoader()).execute();
    }

    @Override
    public View onCreateView(final LayoutInflater paramLayoutInflater,
            final ViewGroup paramViewGroup, final Bundle paramBundle) {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        return view;
    }

    /**
     * バッテリーステータスローダー.
     */
    private class BatteryStatusLoader extends AsyncTask<String, Integer, DConnectMessage> {

        @Override
        protected DConnectMessage doInBackground(final String... args) {
            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setProfile(BatteryProfileConstants.PROFILE_NAME);
            uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
            uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

            DConnectMessage message;
            try {
                HttpResponse response = getDConnectClient().execute(
                        getDefaultHost(), new HttpGet(uriBuilder.build()));
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

            if (result.getInt(DConnectMessage.EXTRA_RESULT)
                    != DConnectMessage.RESULT_OK) {
                return;
            }

            // set profile list
            PreferenceCategory batteryStatusList = (PreferenceCategory) findPreference(
                    getString(R.string.key_battery_profile_list));

            try {
                Number level = (Number) result.get(BatteryProfileConstants.PARAM_LEVEL);
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.level));
                pref.setSummary(Double.toString(level.doubleValue()) + "%");
                batteryStatusList.addPreference(pref);
            } catch (Exception e) {
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.level));
                pref.setSummary("-");
                batteryStatusList.addPreference(pref);
            }

            try {
                boolean charging = result.getBoolean(BatteryProfileConstants.PARAM_CHARGING);
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.charging));
                pref.setSummary(Boolean.toString(charging));
                batteryStatusList.addPreference(pref);
            } catch (Exception e) {
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.charging));
                pref.setSummary("-");
                batteryStatusList.addPreference(pref);
            }

            try {
                int chargingTime = result.getInt(BatteryProfileConstants.PARAM_CHARGING_TIME);
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.chargingtime));
                pref.setSummary(Integer.toString(chargingTime));
                batteryStatusList.addPreference(pref);
            } catch (Exception e) {
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.chargingtime));
                pref.setSummary("-");
                batteryStatusList.addPreference(pref);
            }

            try {
                int dischargingTime = result.getInt(BatteryProfileConstants.PARAM_DISCHARGING_TIME);
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.dischargingtime));
                pref.setSummary(Integer.toString(dischargingTime));
                batteryStatusList.addPreference(pref);
            } catch (Exception e) {
                Preference pref = new Preference(getActivity());
                pref.setTitle(getString(R.string.dischargingtime));
                pref.setSummary("-");
                batteryStatusList.addPreference(pref);
            }
        }
    }

}
