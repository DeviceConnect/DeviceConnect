/*
 MediaStreamRecordingProfileFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment.profile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.fragment.SmartDeviceFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.event.EventHandler;
import org.deviceconnect.message.http.event.HttpEventManager;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;

/**
 * Media Stream Recordingプロファイルフラグメント.
 */
public class MediaStreamRecordingProfileFragment extends SmartDeviceFragment {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreateView",
                new Object[] {inflater, container, savedInstanceState});
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_mediastream, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        // 写真撮影
        Button takePhotoBtn = (Button) view.findViewById(R.id.fragment_mediastream_take_photo);
        takePhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                (new TaskPhotoTask()).execute();
            }
        });
        
        Switch onphoto = (Switch) view.findViewById(R.id.fragment_mediastream_onphoto);
        onphoto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            }
        });
        
        Switch ondata = (Switch) view.findViewById(R.id.fragment_mediastream_ondata);
        ondata.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    (new RegisterOnDataAvaiableTask()).execute();
                } else {
                    (new UnegisterOnDataAvaiableTask()).execute();
                }
            }
        });

        mLogger.exiting(this.getClass().getName(), "onCreateView");
        return view;
    }

    /**
     * 写真撮影タスク.
     */
    private class TaskPhotoTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(final Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = takePhoto();
            } catch (IOException e) {
                mLogger.warning(e.toString());
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            mLogger.entering(getClass().getName(), "onPostExecute", result);

            if (getActivity().isFinishing()) {
                return;
            }

            getView().findViewById(R.id.fragment_mediastream_take_photo).setEnabled(true);

            if (result != null) {
                ((ImageView) getView().findViewById(
                        R.id.fragment_mediastream_content)).setImageBitmap(result);
            }

            mLogger.exiting(this.getClass().getName(), "onPostExecute");
        }

        /**
         * QX10に写真撮影の命令を送信する.
         * @return メディアID
         * @throws IOException I/Oエラーが発生した場合
         */
        private Bitmap takePhoto() throws IOException {
            mLogger.entering(this.getClass().getName(), "takePhoto");

            String deviceId = getSmartDevice().getId();
            String uri = null;
            mLogger.fine("deviceid=" + deviceId);

            // make device take photo request
            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setProfile(MediaStreamRecordingProfileConstants.PROFILE_NAME);
            uriBuilder.setAttribute(MediaStreamRecordingProfileConstants.ATTRIBUTE_TAKE_PHOTO);
            uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
            uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

            DConnectMessage message;
            try {
                HttpResponse response = getDConnectClient().execute(
                        getDefaultHost(), new HttpPost(uriBuilder.build()));
                message = (new HttpMessageFactory()).newDConnectMessage(response);

                if (message.getInt(DConnectMessage.EXTRA_RESULT) == DConnectMessage.RESULT_OK) {
                    uri = message.getString(MediaStreamRecordingProfileConstants.PARAM_URI);
                } else {
                    throw new IOException("takephoto is error: " + message);
                }
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }

            Bitmap bitmap = null;
            try {
                URL url = new URL(uri);
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                throw new IOException(e);
            }
            mLogger.exiting(this.getClass().getName(), "takePhoto", bitmap);
            return bitmap;
        }
    }

    /**
     * 写真撮影タスク.
     */
    private class RegisterOnDataAvaiableTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            URIBuilder builder = new URIBuilder();
            builder.setProfile(MediaStreamRecordingProfileConstants.PROFILE_NAME);
            builder.setAttribute(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
            builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
            builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
            try {
                HttpEventManager.INSTANCE.registerEvent(builder, mHandler);
            } catch (IOException e) {
                mLogger.warning("error");
            }
            return null;
        }
    }

    /**
     * 写真撮影タスク.
     */
    private class UnegisterOnDataAvaiableTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            URIBuilder builder = new URIBuilder();
            builder.setProfile(MediaStreamRecordingProfileConstants.PROFILE_NAME);
            builder.setAttribute(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
            builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, getSmartDevice().getId());
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
            builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
            try {
                HttpEventManager.INSTANCE.registerEvent(builder, mHandler);
            } catch (IOException e) {
                mLogger.warning("error");
            }
            return null;
        }
    }

    /**
     * イベント処理.
     */
    private EventHandler mHandler = new EventHandler() {
        @Override
        public void onEvent(final JSONObject event) {
        }
    };
}
