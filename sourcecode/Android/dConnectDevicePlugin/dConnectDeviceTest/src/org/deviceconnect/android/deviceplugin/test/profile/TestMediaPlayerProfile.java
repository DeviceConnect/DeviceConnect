/*
 TestMediaPlayerProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.MediaPlayerProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.MediaPlayerProfileConstants.PlayStatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * JUnit用テストデバイスプラグイン、MediaStreamsPlayプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestMediaPlayerProfile extends MediaPlayerProfile {
    
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
    protected boolean onPutPlay(final Intent request, final Intent response, final String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onPutStop(Intent request, Intent response, String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutPause(Intent request, Intent response, String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutResume(Intent request, Intent response, String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetPlayStatus(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setStatus(response, PlayStatus.PLAY);
        }
        
        return true;
    }

    @Override
    protected boolean onPutMedia(Intent request, Intent response, String deviceId, String mediaId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (TextUtils.isEmpty(mediaId)) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetMedia(Intent request, Intent response, String deviceId, String mediaId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (TextUtils.isEmpty(mediaId)) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setMIMEType(response, "audio/mp3");
            setTitle(response, "test title");
            setType(response, "test type");
            setLanguage(response, "ja");
            setDescription(response, "test description");
            setDuration(response, 60000);
            Bundle creator = new Bundle();
            setCreator(creator, "test creator");
            setRole(creator, "composer");
            setCreators(response, new Bundle[] {creator});
            setKeywords(response, new String[] {"keyword1", "keyword2"});
            setGenres(response, new String[] {"test1", "test2"});
        }
        
        return true;
    }

    @Override
    protected boolean onGetMediaList(Intent request, Intent response, String deviceId, String query, String mimeType,
            String[] orders, Integer offset, Integer limit) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setCount(response, 1);
            Bundle medium = new Bundle();
            setMediaId(medium, "media001");
            setMIMEType(medium, "audio/mp3");
            setTitle(medium, "test title");
            setType(medium, "test type");
            setLanguage(medium, "ja");
            setDescription(medium, "test description");
            setDuration(medium, 60000);
            Bundle creator = new Bundle();
            setCreator(creator, "test creator");
            setRole(creator, "composer");
            setCreators(medium, new Bundle[] {creator});
            setKeywords(medium, new String[] {"keyword1", "keyword2"});
            setGenres(medium, new String[] {"test1", "test2"});
            setMedia(response, new Bundle[] {medium});
        }
        
        return true;
    }

    @Override
    protected boolean onPutVolume(Intent request, Intent response, String deviceId, Double volume) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (volume == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (0.0 > volume || volume > 1.0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetVolume(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setVolume(response, 0.5);    
        }
        
        return true;
    }

    @Override
    protected boolean onPutSeek(Intent request, Intent response, String deviceId, Integer pos) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (pos == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (0 > pos) {
            // MEMO 本テストプラグインでは pos の最大値チェックは行わないが、実際には行うべき.
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onGetSeek(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setPos(response, 0);
        }
        
        return true;
    }

    @Override
    protected boolean onPutMute(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onDeleteMute(Intent request, Intent response, String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetMute(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setMute(response, true);
        }
        
        return true;
    }

    @Override
    protected boolean onPutOnStatusChange(Intent request, Intent response, String deviceId, String sessionKey) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            // テスト用イベントメッセージを1秒後にブロードキャスト
            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_STATUS_CHANGE);
            Bundle mediaPlayer = new Bundle();
            setStatus(mediaPlayer, Status.PLAY);
            setMediaId(mediaPlayer, "test.mp4");
            setMIMEType(mediaPlayer, "video/mp4");
            setPos(mediaPlayer, 0);
            setVolume(mediaPlayer, 0.5);
            setMediaPlayer(message, mediaPlayer);
            Util.sendBroadcast(getContext(), message);
        }
        
        return true;
    }

    @Override
    protected boolean onDeleteOnStatusChange(Intent request, Intent response, String deviceId, String sessionKey) {
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
