/*
 PebbleNotificationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.profile;import org.deviceconnect.android.deviceplugin.pebble.PebbleDeviceService;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NotificationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
/** * Pebble用 Notification Profile.
 * @author NTT DOCOMO, INC. */public class PebbleNotificationProfile extends NotificationProfile {    @Override    public boolean onPostNotify(final Intent request, final Intent response, final String deviceId,            final NotificationType type, final Direction dir, final String lang, String body, final String tag,            final byte[] iconData) {        if (deviceId == null) {            MessageUtils.setEmptyDeviceIdError(response);            return true;        } else if (!PebbleUtil.checkDeviceId(deviceId)) {            MessageUtils.setNotFoundDeviceError(response);            return true;        } else if (type == null || type == NotificationType.UNKNOWN) {            MessageUtils.setInvalidRequestParameterError(response, "Type : UNKNOWN");
            return true;        } else {            // タグをタイトルとしておく            // タグが設定されていない場合にはno titleを表示            String title = tag;            if (title == null) {                title = " ";            }            if (body == null) {                body = " ";            }            if (body.length() == 0) {                body = " ";            }            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();            mgr.sendNotificationToPebble(title, body);            setResult(response, DConnectMessage.RESULT_OK);            //IDは取得できないので、処理のしようがない            setNotificationId(response, "0");            return true;        }    }}