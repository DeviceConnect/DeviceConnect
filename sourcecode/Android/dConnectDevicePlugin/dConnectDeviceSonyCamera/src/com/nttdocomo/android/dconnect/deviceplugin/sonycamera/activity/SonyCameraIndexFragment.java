package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.activity;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
SonyCameraIndexFragment
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraIndexFragment extends SonyCameraBaseFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        return root;
    }
}
