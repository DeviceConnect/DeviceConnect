/*
 WearNotificationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.deviceplugin.wear.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NotificationProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Notificationプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearNotificationProfile extends NotificationProfile {

    /** Tag. */
    private static final String TAG = "WEAR";

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = WearNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptydeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }

    @Override
    protected boolean onPostNotify(final Intent request, final Intent response, final String deviceId,
            final NotificationType type, final Direction dir, final String lang, final String body, final String tag,
            final byte[] iconData) {
        if (deviceId == null) {
            createNotFoundDevice(response);
        } else if (!checkDeviceId(deviceId)) {
            createEmptydeviceId(response);
        } else if (type == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            Resources myRes = null;
            Bitmap myBitmap = null;
            NotificationCompat.Builder myNotificationBuilder = null;
            NotificationManagerCompat myNotificationManager = null;
            Random random = new Random();
            int myNotificationId = random.nextInt(1000000);
            List<Event> events = EventManager.INSTANCE.getEventList(deviceId, WearNotificationProfile.PROFILE_NAME,
                    null, WearNotificationProfile.ATTRIBUTE_ON_SHOW);

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent intent = EventManager.createEventMessage(event);
                intent.putExtra(WearNotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                getContext().sendBroadcast(intent);
            }

            Intent mIntent = new Intent(this.getContext(),
                    org.deviceconnect.android.deviceplugin.wear.WearDeviceService.class);
            mIntent.setAction(WearConst.DEVICE_TO_WEAR_NOTIFICATION_OPEN);
            mIntent.putExtra(WearConst.PARAM_DEVICEID, deviceId);
            mIntent.putExtra(WearConst.PARAM_NOTIFICATIONID, myNotificationId);
            PendingIntent pendingIntent = PendingIntent.getService(this.getContext(), 0, mIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            switch (type) {
            case UNKNOWN:
                myRes = this.getContext().getResources();
                myBitmap = BitmapFactory.decodeResource(myRes, R.drawable.notification_04);
                myNotificationBuilder = new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.notification_04).setContentTitle("Unkown").setContentText(body)
                        .setContentIntent(pendingIntent).setLargeIcon(myBitmap);
                // Notification を発行
                myNotificationManager = NotificationManagerCompat.from(this.getContext());
                myNotificationManager.notify(myNotificationId, myNotificationBuilder.build());
                response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                setResult(response, IntentDConnectMessage.RESULT_OK);
                break;
            case PHONE:

                myRes = this.getContext().getResources();
                myBitmap = BitmapFactory.decodeResource(myRes, R.drawable.notification_00);
                myNotificationBuilder = new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.notification_00).setContentTitle("Phone").setContentText(body)
                        .setContentIntent(pendingIntent).setLargeIcon(myBitmap);

                // Notification を発行
                myNotificationManager = NotificationManagerCompat.from(this.getContext());
                myNotificationManager.notify(myNotificationId, myNotificationBuilder.build());
                response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                setResult(response, IntentDConnectMessage.RESULT_OK);
                break;
            case MAIL:

                myRes = this.getContext().getResources();
                myBitmap = BitmapFactory.decodeResource(myRes, R.drawable.notification_01);
                myNotificationBuilder = new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.notification_01).setContentTitle("Mail").setContentText(body)
                        .setContentIntent(pendingIntent).setLargeIcon(myBitmap);

                // Notification を発行
                myNotificationManager = NotificationManagerCompat.from(this.getContext());
                myNotificationManager.notify(myNotificationId, myNotificationBuilder.build());
                response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                setResult(response, IntentDConnectMessage.RESULT_OK);
                break;
            case SMS:

                myRes = this.getContext().getResources();
                myBitmap = BitmapFactory.decodeResource(myRes, R.drawable.notification_02);
                myNotificationBuilder = new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.notification_02).setContentTitle("SMS").setContentText(body)
                        .setContentIntent(pendingIntent).setLargeIcon(myBitmap);

                // Notification を発行
                myNotificationManager = NotificationManagerCompat.from(this.getContext());
                myNotificationManager.notify(myNotificationId, myNotificationBuilder.build());
                response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                setResult(response, IntentDConnectMessage.RESULT_OK);
                break;
            case EVENT:

                myRes = this.getContext().getResources();
                myBitmap = BitmapFactory.decodeResource(myRes, R.drawable.notification_03);
                myNotificationBuilder = new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.notification_03).setContentTitle("Event").setContentText(body)
                        .setContentIntent(pendingIntent).setLargeIcon(myBitmap);

                // Notification を発行
                myNotificationManager = NotificationManagerCompat.from(this.getContext());
                myNotificationManager.notify(myNotificationId, myNotificationBuilder.build());
                response.putExtra(NotificationProfile.PARAM_NOTIFICATION_ID, myNotificationId);
                setResult(response, IntentDConnectMessage.RESULT_OK);
                break;
            default:
                MessageUtils.setInvalidRequestParameterError(response);
                break;
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteNotify(final Intent request, final Intent response, final String deviceId,
            final String notificationId) {
        if (deviceId == null) {
            createNotFoundDevice(response);
        } else if (!checkDeviceId(deviceId)) {
            createEmptydeviceId(response);
        } else if (notificationId == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            NotificationManager mNotificationManager = (NotificationManager) this.getContext().getSystemService(
                    this.getContext().NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(notificationId));
            setResult(response, IntentDConnectMessage.RESULT_OK);

            List<Event> events = EventManager.INSTANCE.getEventList(deviceId, WearNotificationProfile.PROFILE_NAME,
                    null, WearNotificationProfile.ATTRIBUTE_ON_CLOSE);

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent intent = EventManager.createEventMessage(event);
                intent.putExtra(WearNotificationProfile.PARAM_NOTIFICATION_ID, notificationId);
                getContext().sendBroadcast(intent);
            }
        }
        return true;
    }

    @Override
    protected boolean onPutOnClick(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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
    protected boolean onPutOnClose(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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
    protected boolean onPutOnShow(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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
    protected boolean onDeleteOnClick(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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
    protected boolean onDeleteOnClose(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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
    protected boolean onDeleteOnShow(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
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

}
