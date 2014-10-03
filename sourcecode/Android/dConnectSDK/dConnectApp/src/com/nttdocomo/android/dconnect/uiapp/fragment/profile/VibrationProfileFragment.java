package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.fragment.SmartDeviceFragment;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.VibrationProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * Vibrationプロファイル用フラグメント.
 */
public class VibrationProfileFragment extends SmartDeviceFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_vibration_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        final TextView pattern = (TextView) view.findViewById(R.id.fragment_vibration_pattern);

        Button btn = (Button) view.findViewById(R.id.fragment_vibration_send);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                sendVibration(pattern.getText());
            }
        });
        return view;
    }
    
    /**
     * Vibrationの命令を送信する.
     * @param pattern バイブレーションパターン
     */
    private void sendVibration(final CharSequence pattern) {
        (new AsyncTask<Void, Void, DConnectMessage>() {
            public DConnectMessage doInBackground(final Void ...args) {
                String p = null;
                if (pattern != null && pattern.length() > 0) {
                    p = pattern.toString();
                }

                try {
                    URIBuilder builder = new URIBuilder();
                    builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
                    builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
                    builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                    if (p != null) {
                        builder.addParameter(VibrationProfileConstants.PARAM_PATTERN, p);
                    }
                    builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPut(builder.build()));
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final DConnectMessage result) {
                if (getActivity().isFinishing()) {
                    return;
                }

                TextView tv = (TextView) getView().findViewById(R.id.fragment_vibration_result);
                if (result == null) {
                    tv.setText("failed");
                } else {
                    tv.setText(result.toString());
                }
            }
        }).execute();
    }
}
