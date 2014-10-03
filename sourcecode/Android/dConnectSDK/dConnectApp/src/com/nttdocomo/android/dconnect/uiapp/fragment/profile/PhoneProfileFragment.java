package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

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
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * Phoneプロファイル用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class PhoneProfileFragment extends SmartDeviceFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_phone_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        final TextView num = (TextView) view.findViewById(R.id.fragment_phone_number);
        
        final OnClickListener l = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                String phone = num.getText().toString();
                int id = v.getId();
                if (id == R.id.fragment_phone_1) {
                    phone += "1";
                } else if (id == R.id.fragment_phone_2) {
                    phone += "2";
                } else if (id == R.id.fragment_phone_3) {
                    phone += "3";
                } else if (id == R.id.fragment_phone_4) {
                    phone += "4";
                } else if (id == R.id.fragment_phone_5) {
                    phone += "5";
                } else if (id == R.id.fragment_phone_6) {
                    phone += "6";
                } else if (id == R.id.fragment_phone_7) {
                    phone += "7";
                } else if (id == R.id.fragment_phone_8) {
                    phone += "8";
                } else if (id == R.id.fragment_phone_9) {
                    phone += "9";
                } else if (id == R.id.fragment_phone_a) {
                    phone += "*";
                } else if (id == R.id.fragment_phone_0) {
                    phone += "0";
                } else if (id == R.id.fragment_phone_s) {
                    phone += "#";
                } else if (id == R.id.fragment_phone_clear) {
                    phone = "";
                }
                num.setText(phone);
            }
        };
        
        int[] ids = {
                R.id.fragment_phone_1,
                R.id.fragment_phone_2,
                R.id.fragment_phone_3,
                R.id.fragment_phone_4,
                R.id.fragment_phone_5,
                R.id.fragment_phone_6,
                R.id.fragment_phone_7,
                R.id.fragment_phone_8,
                R.id.fragment_phone_9,
                R.id.fragment_phone_a,
                R.id.fragment_phone_0,
                R.id.fragment_phone_s,
                R.id.fragment_phone_clear
        };
        
        for (int i = 0; i < ids.length; i++) {
            Button btn = (Button) view.findViewById(ids[i]);
            btn.setOnClickListener(l);
        }

        
        Button send = (Button) view.findViewById(R.id.fragment_phone_send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                callPhone(num.getText().toString());
            }
        });
        return view;
    }
    
    private void callPhone(final String number) {
        (new AsyncTask<Void, Void, DConnectMessage>() {
            public DConnectMessage doInBackground(final Void ...args) {
 
                try {
                    URIBuilder builder = new URIBuilder();
                    builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
                    builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
                    builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
                    builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, number);
                    builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPost(builder.build()));
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

                TextView tv = (TextView) getView().findViewById(R.id.fragment_phone_result);
                if (result == null) {
                    tv.setText("failed");
                } else {
                    tv.setText(result.toString());
                }
            }
        }).execute();
    }
}
