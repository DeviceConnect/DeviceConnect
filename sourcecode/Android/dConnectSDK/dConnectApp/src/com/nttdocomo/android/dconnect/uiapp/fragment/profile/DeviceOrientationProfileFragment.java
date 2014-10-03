package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

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
import com.nttdocomo.dconnect.message.event.EventHandler;
import com.nttdocomo.dconnect.message.http.event.HttpEventManager;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * DeviceOrientationプロファイル用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class DeviceOrientationProfileFragment extends SmartDeviceFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_device_orientation_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        Button registerBtn = (Button) view.findViewById(R.id.fragment_orientation_register);
        registerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                registerEvent();
            }
        });
        
        Button unregisterBtn = (Button) view.findViewById(R.id.fragment_orientation_unregister);
        unregisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                unregisterEvent();
            }
        });
        return view;
    }

    /**
     * イベントを登録する.
     */
    private void registerEvent() {
        (new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            public DConnectMessage doInBackground(final Void ...args) {
                final TextView tv = (TextView) getView().findViewById(R.id.fragment_orientation_event);
                try {
                    URIBuilder builder = new URIBuilder();
                    if (isSSL()) {
                        builder.setScheme("https");
                    } else {
                        builder.setScheme("http");
                    }
                    builder.setHost(getHost());
                    builder.setPort(getPort());
                    builder.setProfile(DeviceOrientationProfileConstants.PROFILE_NAME);
                    builder.setAttribute(DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
                    builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                    builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
                    builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
                    HttpResponse response = HttpEventManager.INSTANCE.registerEvent(builder, new EventHandler() {
                        @Override
                        public void onEvent(final JSONObject event) {
                            if (event != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText(event.toString());
                                    }
                                });
                            }
                        }
                    });
                    return (new HttpMessageFactory()).newDConnectMessage(response);
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

                if (result != null) {
                    TextView tv = (TextView) getView().findViewById(R.id.fragment_orientation_result);
                    tv.setText(result.toString());
                }
            }
        }).execute();
    }
    /**
     * イベントを登録する.
     */
    private void unregisterEvent() {
        (new AsyncTask<Void, Void, DConnectMessage>() {
            public DConnectMessage doInBackground(final Void ...args) {
                try {
                    URIBuilder builder = new URIBuilder();
                    if (isSSL()) {
                        builder.setScheme("https");
                    } else {
                        builder.setScheme("http");
                    }
                    builder.setHost(getHost());
                    builder.setPort(getPort());
                    builder.setProfile(DeviceOrientationProfileConstants.PROFILE_NAME);
                    builder.setAttribute(DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
                    builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                    builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
                    builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
                    HttpEventManager.INSTANCE.unregisterEvent(builder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute();
    }

}
