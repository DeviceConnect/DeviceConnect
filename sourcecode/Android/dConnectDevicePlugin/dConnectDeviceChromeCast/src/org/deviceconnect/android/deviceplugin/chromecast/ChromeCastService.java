/*
 ChromeCastService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastApplication;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastDiscovery;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastHttpServer;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastMediaPlayer;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastMessage;
import org.deviceconnect.android.deviceplugin.chromecast.profile.ChromeCastMediaPlayerProfile;
import org.deviceconnect.android.deviceplugin.chromecast.profile.ChromeCastNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.chromecast.profile.ChromeCastNotificationProfile;
import org.deviceconnect.android.deviceplugin.chromecast.profile.ChromeCastSystemProfile;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.MediaPlayerProfile;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
import com.google.android.gms.common.api.Status;

/**
 * メッセージサービス (Chromecast)
 * <p>
 * Chromecastデバイスプラグインのサービス
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastService extends DConnectMessageService implements
        ChromeCastDiscovery.Callbacks,
        ChromeCastMediaPlayer.Callbacks,
        ChromeCastMessage.Callbacks{
    
    private static final int serverPort = 38088;

    private ChromeCastDiscovery discovery;
    private ChromeCastApplication application;
    private ChromeCastMediaPlayer mediaPlayer;
    private ChromeCastMessage message;
    private ChromeCastHttpServer server;
    private ChromeCastMediaPlayerProfile mediaPlayerProfile;
    private String mDeviceId_OnStatusChange = null;
    private String mSessionKey_OnStatusChange = null;
    private boolean enableCastMediaPlayerStatusUpdate = false;

    @Override
    public void onCreate() {
        super.onCreate();
        
        String appId = getString(R.string.application_id);
        String appMsgUrn = getString(R.string.application_message_urn);

        try {
            server = new ChromeCastHttpServer("0.0.0.0", serverPort);
            server.start();
        }catch(Exception e) {
            e.getStackTrace();
        }
        
        discovery = new ChromeCastDiscovery(this, appId);
        discovery.setCallbacks(this);
        discovery.registerEvent();
        application = new ChromeCastApplication(this, appId);
        mediaPlayer = new ChromeCastMediaPlayer(application);
        mediaPlayer.setCallbacks(this);
        message = new ChromeCastMessage(application, appMsgUrn);
        message.setCallbacks(this);

        EventManager.INSTANCE.setController(new DBCacheController(this));
        addProfile(new ChromeCastNetworkServiceDiscoveryProfile());
        addProfile(new ChromeCastNotificationProfile());
        mediaPlayerProfile = new ChromeCastMediaPlayerProfile();
        addProfile(mediaPlayerProfile);
    }
	
    @Override
    public void onDestroy() {
        server.stop();
        super.onDestroy();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new ChromeCastSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new ChromeCastNetworkServiceDiscoveryProfile();
    }

    @Override
    public void onCastDeviceUpdate(ArrayList<String> devices) {}

    @Override
    public void onCastDeviceSelected(CastDevice selectedDevice) {
        CastDevice currentDevice = application.GetSelectedDevice();
        if(currentDevice != null){
            if(!currentDevice.getDeviceId().equals(selectedDevice.getDeviceId())){
                application.SetSelectedDevice(selectedDevice);
                application.reconnect();
            }else{
                application.connect();
            }
        }else{
            application.SetSelectedDevice(selectedDevice);
            application.connect();
        }
    }

    @Override
    public void onCastDeviceUnselected() {
        application.reconnect();
    }
    
    /**
     * ChromeCastDiscoveryを返す
     * @param   なし
     * @return  ChromeCastDiscovery
     */
    public ChromeCastDiscovery getChromeCastDiscovery() {return discovery;}
    /**
     * ChromeCastApplicationを返す
     * @param   なし
     * @return  ChromeCastApplication
     */
    public ChromeCastApplication getChromeCastApplication() {return application;}
    /**
     * ChromeCastMediaPlayerを返す
     * @param   なし
     * @return  ChromeCastMediaPlayer
     */
    public ChromeCastMediaPlayer getChromeCastMediaPlayer() {return mediaPlayer;}
    /**
     * ChromeCastMessageを返す
     * @param   なし
     * @return  ChromeCastMessage
     */
    public ChromeCastMessage getChromeCastMessage() {return message;}
    /**
     * ChromeCastHttpServerを返す
     * @param   なし
     * @return  ChromeCastHttpServer
     */
    public ChromeCastHttpServer getChromeCastHttpServer() {return server;}

    /**
     * StatusChange通知を有効にする
     * 
     * @param   response
     * @param   deviceId
     * @param   sessionKey
     * @return  なし
     */
    public void registerOnStatusChange(final Intent response, final String deviceId, final String sessionKey) {
        mDeviceId_OnStatusChange = deviceId;
        mSessionKey_OnStatusChange = sessionKey;
        enableCastMediaPlayerStatusUpdate = true;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register OnStatusChange event");
        sendBroadcast(response);
    }
	
    /**
     * StatusChange通知を無効にする
     * 
     * @param   response
     * @return  なし
     */
    public void unregisterOnStatusChange(final Intent response) {
        mDeviceId_OnStatusChange = null;
        mSessionKey_OnStatusChange = null;
        enableCastMediaPlayerStatusUpdate = false;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister OnStatusChange event");
        sendBroadcast(response);
    }

    @Override
    public void onChromeCastMediaPlayerStatusUpdate(MediaStatus status) {
        MediaInfo info = status.getMediaInfo();
        String playStatusString = mediaPlayerProfile.getPlayStatus(status.getPlayerState());

        if(enableCastMediaPlayerStatusUpdate){
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId_OnStatusChange, 
                    MediaPlayerProfile.PROFILE_NAME, null,
                    MediaPlayerProfile.ATTRIBUTE_ON_STATUS_CHANGE);

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                if (event.getSessionKey().equals(mSessionKey_OnStatusChange)) {
                    Intent intent = EventManager.createEventMessage(event);
                    MediaPlayerProfile.setAttribute(intent,
                            MediaPlayerProfile.ATTRIBUTE_ON_STATUS_CHANGE);
                    Bundle mediaPlayer = new Bundle();
                    MediaPlayerProfile.setStatus(mediaPlayer, playStatusString);
                    if(info != null){
                        MediaPlayerProfile.setMediaId(mediaPlayer, info.getContentId());
                        MediaPlayerProfile.setMIMEType(mediaPlayer, info.getContentType());
                    }else{
                        MediaPlayerProfile.setMediaId(mediaPlayer, "");
                        MediaPlayerProfile.setMIMEType(mediaPlayer, "");
                    }
                    MediaPlayerProfile.setPos(mediaPlayer, (int) status.getStreamPosition() / 1000);
                    MediaPlayerProfile.setVolume(mediaPlayer, status.getStreamVolume());
                    MediaPlayerProfile.setMediaPlayer(intent, mediaPlayer);
                    getContext().sendBroadcast(intent);
                }
            }
        }
    }

    /**
     * ステータスに基づいて、レスポンスする
     * @param   response    レスポンス
     * @param   result      ステータス
     * @param   message     メッセージ
     * @return  なし
     */
    private void onChromeCastResult(Intent response, Status result, String message) {
        if(result == null){
            MessageUtils.setIllegalDeviceStateError(response, message);
            sendBroadcast(response);
        }else{
            if (result.isSuccess()){
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
            }else{
                if(message == null){
                    MessageUtils.setIllegalDeviceStateError(response);
                }else{
                    MessageUtils.setIllegalDeviceStateError(response, message + " is error");
                }
            }
            sendBroadcast(response);
        }
    }
    @Override
    public void onChromeCastMediaPlayerResult(Intent response, MediaChannelResult result, String message) {
        if(result == null){
            MessageUtils.setIllegalDeviceStateError(response, message);
            sendBroadcast(response);
        }else{
            onChromeCastResult(response, result.getStatus(), message);
        }
    }
    @Override
    public void onChromeCastMessageResult(Intent response, Status result, String message) {
        onChromeCastResult(response, result, message);
    }
}
