/*
HueCommonAttribute
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.profile.attribute;

import android.content.Intent;
import com.nttdocomo.android.dconnect.deviceplugin.hue.HueDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueLightProfile;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;


/**
 * Hue共通アトリビュート.
 *
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
