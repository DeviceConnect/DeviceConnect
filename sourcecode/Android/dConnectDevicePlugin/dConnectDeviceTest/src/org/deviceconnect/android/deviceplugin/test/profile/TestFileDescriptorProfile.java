/*
 TestFileDescriptorProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import java.io.ByteArrayOutputStream;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.FileDescriptorProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.FileDescriptorProfileConstants.Flag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import org.deviceconnect.android.deviceplugin.test.R;

/**
 * JUnit用テストデバイスプラグイン、FileDescriptorプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestFileDescriptorProfile extends FileDescriptorProfile {

    /**
     * 容量.
     */
    public static final int BYTE = 64000;

    /**
     * Base64エンコードされたファイルデータの文字列表現.
     */
    public static final String FILE_DATA = Base64.encodeToString(new byte[] {0}, Base64.DEFAULT);

    /**
     * 書き込み先のファイルを示すメディアID.
     */
    public static final String PATH = "test.txt";

    /**
     * 書き込むデータを保存したファイルのURI.
     */
    public static final String URI = "test_uri";

    /**
     * ファイルの現在の更新時間.
     */
    public static final String CURR = "2014-06-01T00:00:00+0900";

    /**
     * ファイルの前回の更新時間.
     */
    public static final String PREV = "2014-06-01T00:00:00+0900";

    /**
     * ビットマップの圧縮率.
     */
    private static final int COMPRESSION_QUALITY = 100;
    
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
    protected boolean onGetOpen(final Intent request, final Intent response, final String deviceId, 
            final String path, final Flag flag) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null || flag == Flag.UNKNOWN) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onGetRead(final Intent request, final Intent response, final String deviceId, 
            final String path, final Long length, final Long position) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null || length == null || length < 0 || (position != null && position < 0)) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.test);
            b.compress(CompressFormat.PNG, COMPRESSION_QUALITY, ba);
            if (b.isRecycled()) {
                b.recycle();
                b = null;
            }

            setResult(response, DConnectMessage.RESULT_OK);
            setSize(response, BYTE);
            setFileData(response, FILE_DATA);
        }
       
        return true;
    }

    @Override
    protected boolean onPutClose(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onPutWrite(final Intent request, final Intent response, final String deviceId,
            final String path, final byte[] data, final Long position) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null || data == null || (position != null && position < 0)) {
            MessageUtils.setInvalidRequestParameterError(response, "path=" + path + " , data=" + data + ", position=" + position);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onPutOnWatchFile(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            Intent intent = MessageUtils.createEventIntent();
            setSessionKey(intent, sessionKey);
            setDeviceID(intent, deviceId);
            setProfile(intent, getProfileName());
            setAttribute(intent, ATTRIBUTE_ON_WATCH_FILE);
            
            Bundle obj = new Bundle();
            setPath(obj, PATH);
            setCurr(obj, CURR);
            setPrev(obj, PREV);
            
            setFile(intent, obj);
            
            Util.sendBroadcast(getContext(), intent);
        }
        
        return true;
    }

    @Override
    protected boolean onDeleteOnWatchFile(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

}
