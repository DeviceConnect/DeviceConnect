package com.nttdocomo.android.dconnect.uiapp.fragment.profile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.fragment.SmartDeviceFragment;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * Fileプロファイル用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class FileProfileFragment extends SmartDeviceFragment {

    /**
     * ファイルをリストで表示する.
     */
    private ListView mListView;

    /**
     * ファイルリスト管理アダプタ.
     */
    private ListAdapter mListAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_file_service, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

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
                @SuppressWarnings("unchecked")
                final Map<String, Object> item = (Map<String, Object>) mListAdapter.getItem(position);
                int type = (Integer) item.get(FileProfileConstants.PARAM_FILE_TYPE);
                if (type == FileProfileConstants.FileType.FILE.getValue()) {
                    // 指定されたフォルダを検索
                    String path = (String) item.get(FileProfileConstants.PARAM_PATH);
                    GetFileListTask task = new GetFileListTask();
                    task.execute(path);
                }
            }
        });
        // ルートフォルダ検索
        GetFileListTask task = new GetFileListTask();
        task.execute();
        return view;
    }

    /**
     * 写真撮影タスク.
     */
    private class GetFileListTask extends AsyncTask<String, Integer, DConnectMessage> {

        @Override
        protected DConnectMessage doInBackground(final String... params) {
            String path = null;
            if (params != null && params.length > 0) {
                path = params[0];
            }
            try {
                return getFileList(path);
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
                List<Map<String, Object>> files = (List<Map<String, Object>>) 
                        result.get(FileProfileConstants.PARAM_FILES);
                mListAdapter.setList(files);
                mListAdapter.notifyDataSetChanged();
            }
        }
        /**
         * QX10に写真撮影の命令を送信する.
         * @param path フォルダ名
         * @return メディアID
         * @throws IOException I/Oエラーが発生した場合
         */
        private DConnectMessage getFileList(final String path) throws IOException {
            String deviceId = getSmartDevice().getId();

            URIBuilder builder = new URIBuilder();
            builder.setProfile(FileProfileConstants.PROFILE_NAME);
            builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
            builder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
            if (path != null) {
                builder.addParameter(FileProfileConstants.PARAM_PATH, path);
            }

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
        private List<Map<String, Object>> mFiles;
        
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
            mFiles = objects;
        }
        
        @Override
        public int getCount() {
            return mFiles.size();
        }

        @Override
        public Object getItem(final int position) {
            return mFiles.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.fragment_file_item, (ViewGroup) null);
            }

            @SuppressWarnings("unchecked")
            final Map<String, Object> item = (Map<String, Object>) getItem(position);
            if (item != null) {
                Integer type = (Integer) item.get(FileProfileConstants.PARAM_FILE_TYPE);
                if (type == null) {
                } else if (type == FileProfileConstants.FileType.FILE.getValue()) {
                } else {
                }
                TextView nameText = (TextView) view.findViewById(R.id.text1);
                nameText.setText((String) item.get(FileProfileConstants.PARAM_FILE_NAME));
            }
            return view;
        }
    }
}
