/*
 ExtraProfileFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.fragment.SmartDeviceFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.utils.URIBuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 独自拡張したプロファイルなどの処理を行うフラグメント.
 */
public class ExtraProfileFragment extends SmartDeviceFragment {
    /**
     * プロファイル名.
     */
    private String mProfile;
    
    /**
     * コンストラクタ.
     * @param profile プロファイル名
     */
    public ExtraProfileFragment(final String profile) {
        mProfile = profile;
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_extra_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        final String[] methods = {
            "GET", "POST", "PUT", "DELETE"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        for (int i = 0; i < methods.length; i++) {
            adapter.add(methods[i]);
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        Button sendButton = (Button) view.findViewById(R.id.fragment_extra_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onClickSend(view);
            }
        });

        return view;
    }

    /**
     * 送信ボタンクリックハンドラー.
     * @param view 送信ボタン
     */
    protected void onClickSend(final View view) {
        // インターフェース
        final CharSequence inter = ((TextView) getView().findViewById(
                R.id.fragment_extra_interface)).getText();
        // アトリビュート
        final CharSequence attr = ((TextView) getView().findViewById(
                R.id.fragment_extra_attribute)).getText();
        // アクセストークン
        final String accessToken = getAccessToken();
        // クエリー
        final CharSequence query = ((TextView) getView().findViewById(
                R.id.fragment_extra_query)).getText();

        final URIBuilder builder = new URIBuilder();
        builder.setProfile(mProfile);
        if (inter != null && inter.length() > 0) {
            builder.setInterface(inter.toString());
        }
        if (attr != null && attr.length() > 0) {
            builder.setAttribute(attr.toString());
        }
        builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID,
                getSmartDevice().getId());
        if (accessToken != null) {
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN,
                    accessToken);
        }
        if (query != null) {
            String path = query.toString();
            if (path.length() > 0) {
                String[] keyvalues = path.split("&");
                for (int i = 0; i < keyvalues.length; i++) {
                    String[] kv = keyvalues[i].split("=");
                    if (kv.length == 1) {
                        builder.addParameter(kv[0], "");
                    } else if (kv.length == 2) {
                        builder.addParameter(kv[0], kv[1]);
                    }
                }
            }
        }
        
        HttpRequest request = null;
        try {
            Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
            String method = (String) spinner.getSelectedItem();
            if (method.equals("GET")) {
                request = new HttpGet(builder.build());
            } else if (method.equals("POST")) {
                request = new HttpPost(builder.build());
            } else if (method.equals("PUT")) {
                request = new HttpPut(builder.build());
            } else if (method.equals("DELETE")) {
                request = new HttpDelete(builder.build());
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) getView().findViewById(R.id.fragment_extra_request);
                    try {
                        String uri = builder.build().toASCIIString();
                        tv.setText(uri);
                    } catch (URISyntaxException e) {
                        tv.setText("");
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        (new AsyncTask<HttpRequest, Void, DConnectMessage>() {
            public DConnectMessage doInBackground(final HttpRequest ...args) {
                if (args == null || args.length <= 0) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }

                DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                try {
                    HttpRequest request = args[0];
                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), request);
                    message = (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return message;
            }

            @Override
            protected void onPostExecute(final DConnectMessage result) {

                if (getActivity().isFinishing()) {
                    return;
                }

                if (result == null) {
                    return;
                }
                View view = getView();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.fragment_extra_response);
                    tv.setText(result.toString());
                }
            }
        }).execute(request);
    }
}
