package com.nttdocomo.android.dconnect.deviceplugin.irkit.settings.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nttdocomo.android.dconnect.deviceplugin.irkit.R;

/**
 * 電源投入画面.
 * @author dconnect04
 *
 */
public class IRKitPowerOnFragment extends IRKitBaseFragment {
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dconnect_settings_step_1, null);
        return root;
    }
}
