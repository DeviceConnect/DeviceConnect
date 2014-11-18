/*
 SWNotificationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import org.deviceconnect.android.deviceplugin.sw.SWConstants;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NotificationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

/**
 * SonySWデバイスプラグインの{@link NotificationProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWNotificationProfile extends NotificationProfile {

    @Override
    protected boolean onPostNotify(Intent request, Intent response, String deviceId, NotificationType type,
            final Direction dir, final String lang, final String body, final String tag, final byte[] iconData) {

        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        if (NotificationType.UNKNOWN.equals(type) || type == null) {
            MessageUtils.setInvalidRequestParameterError(response, "type is not specified.");
            return true;
        }
        String uri = request.getStringExtra(PARAM_URI);
        long sourceId = NotificationUtil.getSourceId(getContext(), 
                SWConstants.EXTENSION_SPECIFIC_ID);
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        if (body != null) {
            eventValues.put(Notification.EventColumns.DISPLAY_NAME, body);
            eventValues.put(Notification.EventColumns.MESSAGE, body);
        }
        if (uri != null) {
            String decodedUri = Uri.decode(uri);
            eventValues.put(Notification.EventColumns.IMAGE_URI, decodedUri);
        }
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, System.currentTimeMillis());
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
        Uri addedEvent = NotificationUtil.addEvent(getContext(), eventValues);
        setResult(response, DConnectMessage.RESULT_OK);
        setNotificationId(response, addedEvent.getLastPathSegment());
        return true;
    }

    @Override
    protected boolean onDeleteNotify(final Intent request, final Intent response,
            final String deviceId, final String notificationId) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        if (notificationId == null) {
            MessageUtils.setInvalidRequestParameterError(response, "notificationId is not specified.");
            return true;
        }
        Uri event = Uri.withAppendedPath(Notification.Event.URI, notificationId);
        int num = getContext().getContentResolver().delete(event, null, null);
        if (num > 0) {
            setResult(response, DConnectMessage.RESULT_OK);
        } else {
            MessageUtils.setUnknownError(response, "No notification event is found to be deleted: " + event);
        }
        return true;
    }

}
