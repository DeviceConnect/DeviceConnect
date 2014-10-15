/*
 NotificationProfileFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.fragment.SmartDeviceFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.NotificationProfileConstants;
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
 * ノーティフィケーションサービスフラグメント.
 */
public class NotificationProfileFragment extends SmartDeviceFragment {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");
    /** 通知タイプを選択するスピナ. */
    private Spinner mSpinner;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreateView",
                new Object[] {inflater, container, savedInstanceState});

        View view = inflater.inflate(R.layout.fragment_notification_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.notification_type_list, android.R.layout.simple_spinner_item);
        mSpinner = (Spinner) view.findViewById(R.id.fragment_notification_service_type);
        mSpinner.setAdapter(adapter);

        Button sendButton = (Button) view.findViewById(R.id.fragment_notification_service_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onClickSend(view);
            }
        });

        mLogger.exiting(getClass().getName(), "onCreateView", view);
        return view;
    }

    /**
     * 送信ボタンクリックハンドラー.
     * @param view 送信ボタン
     */
    protected void onClickSend(final View view) {
        mLogger.entering(getClass().getName(), "onClickSend", view);

        CharSequence body = ((TextView) getView().findViewById(
                R.id.fragment_notification_service_body)).getText();

        (new AsyncTask<String, Integer, DConnectMessage>() {
            public DConnectMessage doInBackground(final String ...args) {

                if (args == null || args.length == 0) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }

                String type = String.valueOf(mSpinner.getSelectedItemPosition());
                String body = args[0];

                DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                try {
                    URIBuilder uriBuilder = new URIBuilder();
                    uriBuilder.setProfile(NotificationProfileConstants.PROFILE_NAME);
                    uriBuilder.setAttribute(NotificationProfileConstants.ATTRIBUTE_NOTIFY);
                    uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID,
                            getSmartDevice().getId());
                    uriBuilder.addParameter(NotificationProfileConstants.PARAM_TYPE, type);
                    uriBuilder.addParameter(NotificationProfileConstants.PARAM_BODY, body);
                    uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPost(uriBuilder.build()));
                    message = (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return message;
            }
        }).execute(body.toString());

        mLogger.exiting(getClass().getName(), "onClickSend");
    }

}
