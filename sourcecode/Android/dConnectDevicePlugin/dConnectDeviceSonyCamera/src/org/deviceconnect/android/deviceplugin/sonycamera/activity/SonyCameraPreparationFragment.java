/*
SonyCameraIndexFrgment
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.sonycamera.activity;

import org.deviceconnect.android.deviceplugin.sonycamera.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 下準備説明用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class SonyCameraPreparationFragment extends SonyCameraBaseFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preparation, container, false);
        return root;
    }
}
