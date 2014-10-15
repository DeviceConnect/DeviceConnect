/*
HueCommonAttribute
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile.attribute;

import org.deviceconnect.android.deviceplugin.hue.HueDeviceService;
import org.deviceconnect.android.deviceplugin.hue.profile.HueLightProfile;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;



/**
 * Hue共通アトリビュート.
 * @author NTT DOCOMO, INC.
 */
public abstract class HueCommonAttribute extends HueLightProfile {

    /**
     * Device Service Context.
     */
    protected HueDeviceService mContext;

    /**
     * Constructor.
     * @param context context
     */
    public HueCommonAttribute(final HueDeviceService context) {
        super();
      mContext = context;
    }

    /**
     * OKレスポンス設定.
     * @param response response
     */
    protected void setResultOK(final Intent response) {
        setResult(response, DConnectMessage.RESULT_OK); 
    }

    /**
     * Error レスポンス設定.
     * @param response response
     */
    protected void setResultERR(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR); 
    }

    /**
     * 成功レスポンス送信.
     * @param response response
     */
    protected void sendResultOK(final Intent response) {
        setResultOK(response); 
        
        mContext.sendBroadcast(response);
    }

    /**
     * Errorレスポンス送信.
     * @param response response
     */
     protected void sendResultERR(final Intent response) {

        setResultERR(response); 
        
        mContext.sendBroadcast(response);

    }

     /**
      * エラーメッセージ込みレスポンス送信.
      * @param response response
      * @param errorCode error code
      * @param message error message
      */
    protected void sendResultERR(final Intent response, final int errorCode,
            final String message) {

        MessageUtils.setError(response, errorCode, message);

        mContext.sendBroadcast(response);

    }
    
}
