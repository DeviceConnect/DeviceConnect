/*
 MediaPlayerProfileFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.fragment.SmartDeviceFragment;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.profile.MediaPlayerProfileConstants;
import org.deviceconnect.utils.URIBuilder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * MediaPlayerプロファイル用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class MediaPlayerProfileFragment extends SmartDeviceFragment {

    /**
     * ファイルをリストで表示する.
     */
    private ListView mListView;

    /**
     * ファイルリスト管理アダプタ.
     */
    private ListAdapter mListAdapter;
    
    /**
     * 現在、選択されているメディアデータ.
     */
    private Map<String, Object> mCurrent;
    
    /**
     * 命令を実行するためのサービス.
     */
    private ExecutorService mExecService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_mediaplayer_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        ImageButton playBtn = (ImageButton) view.findViewById(R.id.btn_play);
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                setMediaData();
            }
        });

        ImageButton pauseBtn = (ImageButton) view.findViewById(R.id.btn_pause);
        pauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                pauseMedia();
            }
        });

        // ファイル一覧の設定
        List<Map<String, Object>> empty = new ArrayList<Map<String, Object>>();
        mListAdapter = new ListAdapter(getActivity(), empty);

        // ListViewの設定
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                mCurrent = (Map<String, Object>) mListAdapter.getItem(position);
            }
        });

        GetMediaListTask task = new GetMediaListTask();
        task.execute();

        return view;
    }

    /**
     * メディア取得.
     */
    private class GetMediaListTask extends AsyncTask<String, Integer, DConnectMessage> {

        @Override
        protected DConnectMessage doInBackground(final String... params) {
            try {
                return getMediaList();
            } catch (IOException e) {
                return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
            }
        }

        @Override
        protected void onPostExecute(final DConnectMessage result) {
            if (getActivity().isFinishing()) {
                return;
            }

            if (result == null) {
                return;
            }
            if (result.getInt(DConnectMessage.EXTRA_RESULT) == DConnectMessage.RESULT_OK) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> media = (List<Map<String, Object>>) 
                        result.get(MediaPlayerProfileConstants.PARAM_MEDIA);
                if (media == null) {
                    media = new ArrayList<Map<String, Object>>();
                }
                mListAdapter.setList(media);
                mListAdapter.notifyDataSetChanged();
            }
        }
        /**
         * QX10に写真撮影の命令を送信する.
         * @return メディアID
         * @throws IOException I/Oエラーが発生した場合
         */
        private DConnectMessage getMediaList() throws IOException {
            String deviceId = getSmartDevice().getId();

            URIBuilder builder = new URIBuilder();
            builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
            builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MEDIA_LIST);
            builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

            DConnectMessage message = null;
            try {
                HttpResponse response = getDConnectClient().execute(
                        getDefaultHost(), new HttpGet(builder.build()));
                
                message = (new HttpMessageFactory()).newDConnectMessage(response);
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
            return message;
        }
    }
    /**
     * リストを表示するためのアダプタ.
     * @author NTT DOCOMO, INC.
     */
    private class ListAdapter extends BaseAdapter {
        /**
         * インフレータ.
         */
        private LayoutInflater mInflater;

        /**
         * ファイル一覧.
         */
        private List<Map<String, Object>> mMedia;

        /**
         * コンストラクタ.
         * @param context コンテキスト
         * @param objects ファイル一覧
         */
        public ListAdapter(final Context context, final List<Map<String, Object>> objects) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            setList(objects);
        }

        /**
         * ファイル一覧を設定する.
         * @param objects ファイル一覧
         */
        public void setList(final List<Map<String, Object>> objects) {
            mMedia = objects;
        }

        @Override
        public int getCount() {
            return mMedia.size();
        }

        @Override
        public Object getItem(final int position) {
            return mMedia.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.fragment_file_item, null);
            }

            final Map<String, Object> item = (Map<String, Object>) getItem(position);
            if (item != null) {
                TextView nameText = (TextView) view.findViewById(R.id.text1);
                nameText.setText((String) item.get(MediaPlayerProfileConstants.PARAM_TITLE));
            }
            return view;
        }
    }

    /**
     * メディアを再生します.
     */
    private void playMedia() {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                String deviceId = getSmartDevice().getId();

                URIBuilder builder = new URIBuilder();
                builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
                builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_PLAY);
                builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
                builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                DConnectMessage message = null;
                try {
                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPut(builder.build()));
                    message = (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (URISyntaxException e) {
                } catch (IOException e) {
                }
            }
        });
    }

    /**
     * メディアを停止する.
     */
    private void pauseMedia() {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                String deviceId = getSmartDevice().getId();

                URIBuilder builder = new URIBuilder();
                builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
                builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_PAUSE);
                builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
                builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

                DConnectMessage message = null;
                try {
                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPut(builder.build()));
                    message = (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (URISyntaxException e) {
                } catch (IOException e) {
                }
            }
        });
    }
    
    /**
     * 現在設定されているメディアデータを設定する.
     */
    private void setMediaData() {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                if (mCurrent == null) {
                    return;
                }

                String deviceId = getSmartDevice().getId();
                String mediaId = (String) mCurrent.get(MediaPlayerProfileConstants.PARAM_MEDIA_ID);
                
                URIBuilder builder = new URIBuilder();
                builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
                builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MEDIA);
                builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
                builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
                builder.addParameter(MediaPlayerProfileConstants.PARAM_MEDIA_ID, mediaId);

                DConnectMessage message = null;
                try {
                    HttpResponse response = getDConnectClient().execute(
                            getDefaultHost(), new HttpPut(builder.build()));
                    message = (new HttpMessageFactory()).newDConnectMessage(response);
                    if (message.getInt(DConnectMessage.EXTRA_RESULT) == DConnectMessage.RESULT_OK) {
                        playMedia();
                    }
                } catch (URISyntaxException e) {
                } catch (IOException e) {
                }
            }
        });
    }
}
