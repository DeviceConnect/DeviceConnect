/*
LightProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.profile;

import org.deviceconnect.android.deviceplugin.util.DcLoggerLight;
import org.deviceconnect.android.message.MessageUtils;

import android.content.Intent;


/**
 * Light Profile. 
 * <p>
 * 標準化されたProfile,Interface,Attributeのメソッドの呼び出しを行う振り分けクラス.
 * 例外処理では標準化されたエラー結果を返す.
 * </p>
 * @author NTT DOCOMO, INC.
 */
public abstract class LightProfile extends DConnectProfile implements LightProfileConstants {

    /**
     * ロガー.
     */
    protected DcLoggerLight mLogger = new DcLoggerLight();

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        if (isNullAttribute(request)) {
            return onGetLight(request, response);
        } else if (isLightGroupAttribute(request)) {
            return onGetLightGroup(request, response);
        } else {
            return onGetOther(request, response);
        }
    }

    /**
     * POSTメソッドハンドラー.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        if (isNullAttribute(request)) {
            return onPostLight(request, response);
        } else if (isLightGroupAttribute(request)) {
            return onPostLightGroup(request, response);
        } else if (isLightGroupCreateAttribute(request)) {
            return onPostLightGroupCreate(request, response);
        } else {
            return onPostOther(request, response);
        }
    }

    /**
     * DELETEメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        if (isNullAttribute(request)) {
            return onDeleteLight(request, response);
        } else if (isLightGroupAttribute(request)) {
            return onDeleteLightGroup(request, response);
        } else if (isLightGroupClearAttribute(request)) {
            return onDeleteLightGroupClear(request, response);
        } else {
            return onDeleteOther(request, response);
        }
    }

    /**
     * PUTメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        if (isNullAttribute(request)) {
            return onPutLight(request, response);
        } else if (isLightGroupAttribute(request)) {
            return onPutLightGroup(request, response);
        } else {
            return onPutOther(request, response);
        }
    }

    /**
     * onGetLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onGetLight(final Intent request, final Intent response);

    /**
     * onPostLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onPostLight(final Intent request, final Intent response);

    /**
     * onDeleteLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onDeleteLight(final Intent request, final Intent response);

    /**
     * onPutLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onPutLight(final Intent request, final Intent response);

    /**
     * onGetLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onGetLightGroup(final Intent request, final Intent response);

    /**
     * onPostLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onPostLightGroup(final Intent request, final Intent response);

    /**
     * onDeleteLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onDeleteLightGroup(final Intent request, final Intent response);

    /**
     * onPutLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onPutLightGroup(final Intent request, final Intent response);

    /**
     * onPostLightGroupCreateメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onPostLightGroupCreate(final Intent request, final Intent response);

    /**
     * onDeleteLightGroupClearメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected abstract boolean onDeleteLightGroupClear(final Intent request, final Intent response);

    /**
     * onGetOtherメソッドハンドラー AttributeやInterfaceがある場合はコチラを継承.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetOther(final Intent request, final Intent response) {
        setErrAttribute(response);
        return true;
    }

    /**
     * onPostOtherメソッドハンドラー AttributeやInterfaceがある場合はコチラを継承.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostOther(final Intent request, final Intent response) {
        setErrAttribute(response);
        return true;
    }

    /**
     * onDeleteOtherメソッドハンドラー AttributeやInterfaceがある場合はコチラを継承.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOther(final Intent request, final Intent response) {
        setErrAttribute(response);
        return true;
    }

    /**
     * onPutOtherメソッドハンドラー AttributeやInterfaceがある場合はコチラを継承.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOther(final Intent request, final Intent response) {
        setErrAttribute(response);
        return true;
    }

    /**
     * Attributeがnullかどうか.
     * 
     * @param request リクエストパラメータ
     * @return Attributeがnullの場合はtrue
     */
    protected boolean isNullAttribute(final Intent request) {
        return getAttribute(request) == null;
    }

    /**
     * Interfaceがnullかどうか.
     * 
     * @param request リクエストパラメータ
     * @return Interfaceがnullの場合はtrue
     */
    protected boolean isNullInterface(final Intent request) {
        return getInterface(request) == null;
    }

    /**
     * Attributeがlight/groupかどうか.
     * 
     * @param request リクエストパラメータ
     * @return Attributeがnullの場合はtrue
     */
    protected boolean isLightGroupAttribute(final Intent request) {
        String attribute = getAttribute(request);
        return isNullInterface(request) && ATTRIBUTE_GROUP.equals(attribute);
    }

    /**
     * Attributeがlight/group/createかどうか.
     * 
     * @param request リクエストパラメータ
     * @return Attributeがnullの場合はtrue
     */
    protected boolean isLightGroupCreateAttribute(final Intent request) {
        String myInterface = getInterface(request);
        String attribute = getAttribute(request);
        return INTERFACE_GROUP.equals(myInterface) && ATTRIBUTE_CREATE.equals(attribute);
    }

    /**
     * Attributeがlight/group/clearかどうか.
     * 
     * @param request リクエストパラメータ
     * @return Attributeがnullの場合はtrue
     */
    protected boolean isLightGroupClearAttribute(final Intent request) {
        String myInterface = getInterface(request);
        String attribute = getAttribute(request);
        return INTERFACE_GROUP.equals(myInterface) && ATTRIBUTE_CLEAR.equals(attribute);
    }

    /**
     * NotSupportActionのレスポンスを返す.
     * 
     * @param response レスポンスパラメータ
     */
    protected void setErrNotSupportAction(final Intent response) {
        MessageUtils.setNotSupportActionError(response);
    }

    /**
     * UnknownAttributeErrorのレスポンスを返す.
     * 
     * @param response レスポンスパラメータ
     */
    protected void setErrAttribute(final Intent response) {
        MessageUtils.setUnknownAttributeError(response);
    }

    /**
     * InvalidRequestParameterErrorのレスポンスを返す.
     * 
     * @param e Exceptionパラメータ
     * @param response レスポンスパラメータ
     */
    protected void setErrParameter(final Exception e, final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
    }

    /**
     * UnknownErrorのレスポンスを返す.
     * 
     * @param e Exceptionパラメータ
     * @param response レスポンスパラメータ
     */
    protected void setErrUnknown(final Exception e, final Intent response) {
        MessageUtils.setUnknownError(response);
    }

}
