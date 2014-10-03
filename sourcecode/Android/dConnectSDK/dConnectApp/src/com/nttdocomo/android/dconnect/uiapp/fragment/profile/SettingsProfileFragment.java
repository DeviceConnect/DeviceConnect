package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.fragment.SmartDeviceFragment;

/**
 * Settingsプロファイル用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class SettingsProfileFragment extends SmartDeviceFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_settings_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        return view;
    }
}
