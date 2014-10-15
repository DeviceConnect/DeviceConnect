/*
 IRKitPowerOnFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.fragment;

import org.deviceconnect.android.deviceplugin.irkit.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 電源投入画面.
 * @author NTT DOCOMO, INC.
 */
public class IRKitPowerOnFragment extends IRKitBaseFragment {
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.irkit_settings_step_1, null);
        return root;
    }
}
