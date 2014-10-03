/**
  HostNotificationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;

import com.nttdocomo.android.dconnect.deviceplugin.host.R;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NotificationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * ホストデバイスプラグイン, Notification プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class HostNotificationProfile extends NotificationProfile {

    /** Debug Tag. */
    private static final String TAG = "HOST";
    
    /** Randam Seed. */
    private static final int RANDAM_SEED = 1000000;

    /**
     * Notificationテータスレシーバー.
     */
    private NotificationStatusReceiver mNotificationStatusReceiver;

    /**
     * Notification Flag.
     */
    private static final String ACTON_NOTIFICATION = "dconnect.notifiy";

    /** Error. */
    private static final int ERROR_VALUE_IS_NULL = 100;
    
    /**
     * POSTメッセージ受信時の処理.
     */
    @Override
    public boolean onPostNotify(final Intent request, final Intent response, final String deviceId,
            final NotificationType type, final Direction dir, final String lang, final String body, final String tag,
            final byte[] iconData) {

        // super.onPostRequest(request, response);


        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            if (NotificationProfile.ATTRIBUTE_NOTIFY.equals(getAttribute(request))) {
                if (body == null) {
                    MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "body is null");
                    return true;
                } else {
                    if (mNotificationStatusReceiver == null) {
                        mNotificationStatusReceiver = new NotificationStatusReceiver();
                        getContext().getApplicationContext().registerReceiver(mNotificationStatusReceiver,
                                new IntentFilter(ACTON_NOTIFICATION));
                    }

                    int iconType = 0;
                    String mTitle = "";
                    // Typeの処理
                    if (type == NotificationType.PHONE) {
                        iconType = R.drawable.notification_00;
                        mTitle = "PHONE";
                    } else if (type == NotificationType.MAIL) {
                        iconType = R.drawable.notification_01;
                        mTitle = "MAIL";
                    } else if (type == NotificationType.SMS) {
                        iconType = R.drawable.notification_02;
                        mTitle = "SMS";
                    } else if (type == NotificationType.EVENT) {
                        iconType = R.drawable.notification_03;
                        mTitle = "EVENT";
                    } else {
                        MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "not support type");
                        return true;
                    }
                    String encodeBody = "";
                    try {
                        encodeBody = URLDecoder.decode(body, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                 
                    }

                    Random random = new Random();
                    int notifyId =  random.nextInt(RANDAM_SEED);
                    
                    // Build intent for notification content
                    Intent notifyIntent = new Intent(ACTON_NOTIFICATION);
                    notifyIntent.putExtra("notificationId", notifyId);
                    notifyIntent.putExtra("deviceId", deviceId);
                    
                    PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.getContext(), 
                            notifyId, 
                            notifyIntent,
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

                    NotificationCompat.Builder notificationBuilder = 
                            new NotificationCompat.Builder(this.getContext())
                            .setSmallIcon(iconType)
                            .setContentTitle("" + mTitle)
                            .setContentText(encodeBody)
                            .setContentIntent(mPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManager mNotification = (NotificationManager) getContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    // Build the notification and issues it with notification
                    // manager.
                    mNotification.notify(notifyId, notificationBuilder.build());

                    response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, notifyId);
                    setResult(response, IntentDConnectMessage.RESULT_OK);
                    
                    List<Event> events = EventManager.INSTANCE.getEventList(
                            deviceId, 
                            HostNotificationProfile.PROFILE_NAME,
                            null, 
                            HostNotificationProfile.ATTRIBUTE_ON_SHOW);

                    for (int i = 0; i < events.size(); i++) {
                        Event event = events.get(i);
                        Intent intent = EventManager.createEventMessage(event);
                        intent.putExtra(HostNotificationProfile.PARAM_NOTIFICATION_ID, notifyId);
                        getContext().sendBroadcast(intent);
                    }
                    
                    return true;
                }
            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "not support profile");
                return true;
            }
        }
        return true;
    }

    /**
     * デバイスへのノーティフィケーション消去リクエストハンドラー.<br/>
     * ノーティフィケーションを消去し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param notificationId 通知ID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteNotify(final Intent request, final Intent response, final String deviceId,
            final String notificationId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
           
            NotificationManager mNotificationManager =
                    (NotificationManager) this.getContext().getSystemService(
                       this.getContext().NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(notificationId));
            setResult(response, IntentDConnectMessage.RESULT_OK);
            
            List<Event> events = EventManager.INSTANCE.getEventList(
                       deviceId, 
                       HostNotificationProfile.PROFILE_NAME,
                       null, 
                       HostNotificationProfile.ATTRIBUTE_ON_CLOSE);

           for (int i = 0; i < events.size(); i++) {
               Event event = events.get(i);
               Intent intent = EventManager.createEventMessage(event);
               intent.putExtra(HostNotificationProfile.PARAM_NOTIFICATION_ID, notificationId);
               getContext().sendBroadcast(intent);
           }

        }
        return true;
    }
    
    @Override
    protected boolean onPutOnClick(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            
            mNotificationStatusReceiver = new NotificationStatusReceiver();
            IntentFilter intentFilter = new IntentFilter(ACTON_NOTIFICATION);
            this.getContext().registerReceiver(mNotificationStatusReceiver, intentFilter);
            
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) { 
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
            
        }
        return true;
    }

    @Override
    protected boolean onPutOnClose(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) { 
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
        }
        return true;
    }

    @Override
    protected boolean onPutOnShow(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) { 
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }

    
    @Override
    protected boolean onDeleteOnClick(final Intent request,
            final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnClose(final Intent request,
            final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnShow(final Intent request,
            final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }


    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }
    
    /**
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {

        MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "SessionKey not found");
    }
    
    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
    
    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
    }
    
    /**
     * ノーティフィケーション.
     */
    private class NotificationStatusReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            // クリティカルセクション（バッテリーステータスの状態更新etc）にmutexロックを掛ける。
            synchronized (this) {
                
                String actionName = intent.getAction();
                int notificationId = intent.getIntExtra("notificationId", -1);
                String mDeviceId = intent.getStringExtra("deviceId");

                
                List<Event> events = EventManager.INSTANCE.getEventList(
                        mDeviceId, 
                        HostNotificationProfile.PROFILE_NAME,
                        null, 
                        HostNotificationProfile.ATTRIBUTE_ON_CLICK);

                for (int i = 0; i < events.size(); i++) {
                    Event event = events.get(i);
                    Intent mIntent = EventManager.createEventMessage(event);
                    mIntent.putExtra(HostNotificationProfile.PARAM_NOTIFICATION_ID, "" + notificationId);
                    getContext().sendBroadcast(mIntent);
                }
                
                // 状態更新の為のmutexロックを外して、待っていた処理に通知する。
                notifyAll();
            }
        }
    }
}
