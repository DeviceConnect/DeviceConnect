/*
 TestFileProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.FileProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.Bundle;

/**
 * JUnit用テストデバイスプラグイン、Fileプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestFileProfile extends FileProfile {

    /**
     * 容量.
     */
    public static final int BYTE = 64000;

    /**
     * ファイル名.
     */
    public static final String FILE_NAME = "test.png";

    /**
     * ファイルのメディアID.
     */
    public static final String PATH = "/test.png";

    /**
     * ファイルのMIMEタイプ.
     */
    public static final String MIME_TYPE = "image/png";

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     * @param fileMgr ファイルマネージャ
     */
    public TestFileProfile(final FileManager fileMgr) {
        super(fileMgr);
    }
    
    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        return TestNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response, "Device ID is empty.");
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response, "Device is not found.");
    }

    @Override
    protected boolean onGetReceive(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            String uri = getFileManager().getContentUri() + "/" + getFilenameFromPath(path);
            setURI(response, uri);
            setMIMEType(response, MIME_TYPE);
        }
        return true;
    }

    @Override
    protected boolean onGetList(final Intent request, final Intent response, final String deviceId, final String path,
            final String mimeType, final String order, final Integer offset, final Integer limit) {
        if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            List<Bundle> files = new ArrayList<Bundle>();
            Bundle file = new Bundle();
            setFileName(file, FILE_NAME);
            setPath(file, PATH);
            setMIMEType(file, MIME_TYPE);
            setUpdateDate(file, new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ", Locale.getDefault()).format(new Date()));
            setFileSize(file, BYTE);
            setFileType(response, FileType.FILE);
            files.add(file);
            setFiles(response, files);
            setCount(response, files.size());
        }
        return true;
    }

    @Override
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, 
            final String path, final String mimeType, final byte[] data) {
        if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            String u = null;
            try {
                // MEMO: テスト簡素化のため、テストプラグイン内ではディレクトリツリーを持たせない.
                String filename = getFilenameFromPath(path);
                u = getFileManager().saveFile(filename, data);
            } catch (IOException e) {
                u = null;
            }
            if (u == null) {
                MessageUtils.setUnknownError(response, "Failed to save file.");
            } else {
                setResult(response, DConnectMessage.RESULT_OK);
            }
        }
        return true;
    }

    @Override
    protected boolean onPostMkdir(Intent request, Intent response, String deviceId, String path) {
        if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onDeleteRmdir(Intent request, Intent response, String deviceId, String path, boolean force) {
        if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    private String getFilenameFromPath(String path) {
        String[] components = path.split("/");
        if (components.length == 0) {
            return path;
        }
        return components[components.length - 1];
    }

    @Override
    protected boolean onDeleteRemove(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            getFileManager().removeFile(getFilenameFromPath(path));
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }
}
