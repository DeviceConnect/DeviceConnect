package com.nttdocomo.android.dconnect.deviceplugin.chromecast.profile;

import android.content.Intent;
import com.nttdocomo.android.dconnect.deviceplugin.chromecast.ChromeCastService;
import com.nttdocomo.android.dconnect.deviceplugin.util.ChromeCastMessage;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NotificationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Notification プロファイル (Chromecast)
 * <p>
 * Chromecastのノーティフィケーションの操作機能を提供する
 * </p>
 * 
 */
public class ChromeCastNotificationProfile extends NotificationProfile {
	
	private static final String ERROR_MESSAGE_DEVICE_NOT_ENABLE 	= "Device is not enable";
	
	/**
     * デバイスが有効か否かを返す<br/>
     * デバイスが無効の場合、レスポンスにエラーを設定する
     * 
     * @param	response	レスポンス
     * @param	app			ChromeCastMediaPlayer
     * @return	デバイスが有効か否か（有効: true, 無効: false）
     */
	private boolean isDeviceEnable(final Intent response, ChromeCastMessage app){
		if(!app.isDeviceEnable()){
			MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_DEVICE_NOT_ENABLE);
			setResult(response, DConnectMessage.RESULT_ERROR);
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean onPostNotify(Intent request, Intent response,
			String deviceId, NotificationType type, final Direction dir,
			final String lang, final String body, final String tag,
			final byte[] iconData) {
		ChromeCastMessage app = ((ChromeCastService) getContext()).getChromeCastMessage();
		if(body == null){
			MessageUtils.setInvalidRequestParameterError(response, "body is null");
			response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
			return true;
		}
		switch (type) {
		case PHONE:	break;
		case MAIL:	break;
		case SMS:	break;
		case EVENT:	break;
		default:
			MessageUtils.setInvalidRequestParameterError(response, "type is null or invalid");
			response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
			return true;
		}
		
		if(!isDeviceEnable(response, app))	return true;
		app.sendMessage(response, "{\"function\":\"write\", \"type\":\"" + type.getValue() + "\", \"message\":\"" + body + "\"}");
		return false;
	}

	@Override
	protected boolean onDeleteNotify(final Intent request,
			final Intent response, final String deviceId,
			final String notificationId) {
		ChromeCastMessage app = ((ChromeCastService) getContext()).getChromeCastMessage();
		if(!isDeviceEnable(response, app))	return true;
		app.sendMessage(response, "{\"function\":\"clear\"}");
		return false;
	}

}
