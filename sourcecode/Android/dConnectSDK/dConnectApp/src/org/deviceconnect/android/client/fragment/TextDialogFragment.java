/*
 TextDialogFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.client.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.http.protocol.HTTP;
import org.deviceconnect.android.uiapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * プライバシーポリシーフラグメント.
 */
public class TextDialogFragment extends DialogFragment {

    /**
     * バッファサイズ.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreateView",
                new Object[] {inflater, container, savedInstanceState});

        View view = inflater.inflate(R.layout.fragment_privacypolicy, null);
        TextView text = (TextView) view.findViewById(android.R.id.text1);

        InputStream is = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            is = getActivity().getResources().openRawResource(
                    getArguments().getInt(Intent.EXTRA_TEXT));
            byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                int len = is.read(buf);
                if (len < 0) {
                    break;
                }
                os.write(buf, 0, len);
            }
            text.setText(new String(os.toByteArray(), HTTP.UTF_8));
        } catch (IOException e) {
            mLogger.warning(e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    mLogger.fine(e.toString());
                }
            }
        }

        getDialog().setTitle(getArguments().getInt(Intent.EXTRA_TITLE));

        mLogger.exiting(getClass().getName(), "onCreateView", view);
        return view;
    }

}
