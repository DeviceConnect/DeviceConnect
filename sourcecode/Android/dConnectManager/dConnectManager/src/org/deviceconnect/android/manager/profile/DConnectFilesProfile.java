/*
 DConnectFilesProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.android.manager.DConnectMessageService;
import org.deviceconnect.android.manager.DConnectService;
import org.deviceconnect.android.manager.request.DConnectRequest;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.DConnectProfileConstants;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

/**
 * ファイルにアクセスするためのプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class DConnectFilesProfile extends DConnectProfile {
    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** プロファイル名. */
    public static final String PROFILE_NAME = "files";

    /** 属性: {@value}. */
    public static final String PARAM_MIME_TYPE = "mimetype";

    /** 属性: {@value}. */
    public static final String PARAM_DATA = "data";

    /** 拡張子とMimetypeを持つマップ. */
    private final Map<String, String> mExtMap = new HashMap<String, String>();

    /**
     * コンストラクタ.
     * @param context コンテキスト
     */
    public DConnectFilesProfile(final Context context) {
        loadMimeType(context);
    }

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        DConnectRequest req = new DConnectRequest() {
            @Override
            public boolean hasRequestCode(final int requestCode) {
                return false;
            }
            @Override
            public void run() {
                String uri = request.getStringExtra(DConnectProfileConstants.PARAM_URI);
                byte[] buf = getContentData(uri);
                if (buf == null) {
                    MessageUtils.setInvalidRequestParameterError(response);
                } else {
                    setResult(response, DConnectMessage.RESULT_OK);
                    response.putExtra(PARAM_DATA, buf);
                    response.putExtra(PARAM_MIME_TYPE, getExtension(uri));
                }
                sendResponse(response);
            }
        };
        req.setContext(getContext());
        req.setRequest(request);
        ((DConnectMessageService) getContext()).addRequest(req);

        // 各デバイスプラグインに送信する場合にはfalseを返却、
        // dConnectManagerで止める場合にはtrueを返却する
        // ここでは、各デバイスには渡さないのでtrueを返却する。
        return true;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    /**
     * mimetype一覧を読み込む.
     * @param context コンテキスト
     */
    private void loadMimeType(final Context context) {
        InputStream fin = null;
        BufferedReader br = null;
        try {
            AssetManager assetManager = context.getResources().getAssets();
            fin = assetManager.open("mimetype.csv");
            br = new BufferedReader(new InputStreamReader(fin));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",");
                if (tmp != null && tmp.length == 2) {
                    mExtMap.put(tmp[0].trim(), tmp[1].trim());
                }
            }
        } catch (IOException e) {
            sLogger.warning("Exception in DConnectFilesProfile.");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    sLogger.warning("Exception in DConnectFilesProfile.");
                }
            }
        }
    }

    /**
     * 拡張子からMime Typeを判別する.
     * @param path ファイルパス
     * @return MimeType
     */
    private String getExtension(final String path) {
        String mimetype = "application/octet-stream";
        int idx = path.lastIndexOf(".");
        if (idx > 0) {
            String ext = path.substring(idx + 1);
            if (mExtMap.containsKey(ext)) {
                return mExtMap.get(ext);
            }
        }
        return mimetype;
    }
}
