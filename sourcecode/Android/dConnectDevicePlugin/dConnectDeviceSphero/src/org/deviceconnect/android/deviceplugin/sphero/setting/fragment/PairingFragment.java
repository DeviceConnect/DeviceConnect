/*
 PairingFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.setting.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.deviceconnect.android.deviceplugin.sphero.R;

/**
 * Spheroペアリング説明画面.
 * @author NTT DOCOMO, INC.
 */
public class PairingFragment extends Fragment {
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
                final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.setting_pairing, null);
        
        final ImageView image = (ImageView) root.findViewById(R.id.animView);
        image.setBackgroundResource(R.anim.sphero_light);
        
        Button button = (Button) root.findViewById(R.id.btnSetting);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        });
        
        return root;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        View root = getView();
        if (root != null) {
            ImageView image = (ImageView) root.findViewById(R.id.animView);
            AnimationDrawable anim = (AnimationDrawable) image.getBackground();
            anim.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        View root = getView();
        if (root != null) {
            ImageView image = (ImageView) root.findViewById(R.id.animView);
            AnimationDrawable anim = (AnimationDrawable) image.getBackground();
            anim.stop();
        }
    }
}
