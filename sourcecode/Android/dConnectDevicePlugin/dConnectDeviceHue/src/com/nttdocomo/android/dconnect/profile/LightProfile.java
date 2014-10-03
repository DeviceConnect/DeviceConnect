package com.nttdocomo.android.dconnect.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.param.DcParam.DcParamException;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerLight;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
LightProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * Light Profile. 標準化されたProfile,Interface,Attributeのメソッドの呼び出しを行う振り分けクラス。
 * 例外処理い標準化されたエラー結果を返す。
 */
public abstract class LightProfile extends DConnectProfile implements LightProfileConstants {

    /**
     * ロガー.
     */
    protected DcLoggerLight mLogger = new DcLoggerLight();

    /**
     * 同期の場合の戻り値.
     */
    protected static final boolean SYNC_RESPONSE = true;

    /**
     * 非同期の場合の戻り値.
     */
    protected static final boolean ASYNC_RESPONSE = false;

    @Override
    public String getProfileName() {

        // mLogger.fine(this, "getProfileName", PROFILE_NAME);

        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {

        // mLogger.entering(this, "onGetRequest");

        try {

            if (isNullAttribute(request)) {
                // Profileのみ指定
                return onGetLight(request, response);

            } else if (isLightGroupAttribute(request)) {
                // Profileのみ指定
                return onGetLightGroup(request, response);

            } else {
                // Attribute、Interfaceが定義されていないのに指定されている
                return onGetOther(request, response);

            }

        } catch (DcParamException e) {

            setErrParameter(e, response);
            return SYNC_RESPONSE;

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

        // mLogger.entering(this, "onPostRequest");

        try {

            if (isNullAttribute(request)) {
                // Profileのみ指定
                return onPostLight(request, response);

            } else if (isLightGroupAttribute(request)) {
                // Profileのみ指定
                return onPostLightGroup(request, response);

            } else if (isLightGroupCreateAttribute(request)) {
                // Profileのみ指定
                return onPostLightGroupCreate(request, response);

            } else {
                // Attribute、Interfaceが定義されていないのに指定されている
                return onPostOther(request, response);

            }

        } catch (DcParamException e) {

            setErrParameter(e, response);
            return SYNC_RESPONSE;

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

        // mLogger.entering(this, "onDeleteRequest");

        try {

            if (isNullAttribute(request)) {
                // Profileのみ指定
                return onDeleteLight(request, response);

            } else if (isLightGroupAttribute(request)) {
                // Profileのみ指定
                return onDeleteLightGroup(request, response);

            } else if (isLightGroupClearAttribute(request)) {
                // Profileのみ指定
                return onDeleteLightGroupClear(request, response);

            } else {
                // Attribute、Interfaceが定義されていないのに指定されている
                return onDeleteOther(request, response);

            }

        } catch (DcParamException e) {

            setErrParameter(e, response);
            return SYNC_RESPONSE;

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

        // mLogger.entering(this, "onPutRequest");

        try {

            if (isNullAttribute(request)) {
                // Profileのみ指定
                return onPutLight(request, response);

            } else if (isLightGroupAttribute(request)) {
                // Profileのみ指定
                return onPutLightGroup(request, response);

            } else {
                // Attribute、Interfaceが定義されていないのに指定されている
                return onPutOther(request, response);

            }

        } catch (DcParamException e) {

            setErrParameter(e, response);
            return SYNC_RESPONSE;

        }

    }

    /**
     * onGetLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     * @throws com.nttdocomo.android.dconnect.deviceplugin.param.DcParam.DcParamException
     */
    protected abstract boolean onGetLight(final Intent request, final Intent response) throws DcParamException;

    /**
     * onPostLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onPostLight(final Intent request, final Intent response) throws DcParamException;

    /**
     * onDeleteLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onDeleteLight(final Intent request, final Intent response) throws DcParamException;

    /**
     * onPutLightメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onPutLight(final Intent request, final Intent response) throws DcParamException;

    /**
     * onGetLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onGetLightGroup(final Intent request, final Intent response) throws DcParamException;

    /**
     * onPostLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onPostLightGroup(final Intent request, final Intent response) throws DcParamException;

    /**
     * onDeleteLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onDeleteLightGroup(final Intent request, final Intent response) throws DcParamException;

    /**
     * onPutLightGroupメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onPutLightGroup(final Intent request, final Intent response) throws DcParamException;

    /**
     * onPostLightGroupCreateメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onPostLightGroupCreate(final Intent request, final Intent response)
            throws DcParamException;

    /**
     * onDeleteLightGroupClearメソッドハンドラー.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException パラメータ異常
     */
    protected abstract boolean onDeleteLightGroupClear(final Intent request, final Intent response)
            throws DcParamException;

    /**
     * onGetOtherメソッドハンドラー AttributeやInterfaceがある場合はコチラを継承.<br>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetOther(final Intent request, final Intent response) {

        setErrAttribute(response);

        return SYNC_RESPONSE;
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

        return SYNC_RESPONSE;
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

        return SYNC_RESPONSE;
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

        return SYNC_RESPONSE;
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
     * OKのレスポンスを返す.
     * 
     * @param response レスポンスパラメータ
     */
    protected void setResultOK(final Intent response) {
        setResult(response, DConnectMessage.RESULT_OK);
    }

    /**
     * NotSupportActionのレスポンスを返す.
     * 
     * @param response レスポンスパラメータ
     */
    protected void setErrNotSupportAction(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
        MessageUtils.setNotSupportActionError(response);
    }

    /**
     * UnknownAttributeErrorのレスポンスを返す.
     * 
     * @param response レスポンスパラメータ
     */
    protected void setErrAttribute(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
        MessageUtils.setUnknownAttributeError(response);
    }

    /**
     * InvalidRequestParameterErrorのレスポンスを返す.
     * 
     * @param e Exceptionパラメータ
     * @param response レスポンスパラメータ
     */
    protected void setErrParameter(final Exception e, final Intent response) {
        mLogger.warning(this, "onPutRequest", "setErrParameter", e);
        setResult(response, DConnectMessage.RESULT_ERROR);
        MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
    }

    /**
     * UnknownErrorのレスポンスを返す.
     * 
     * @param e Exceptionパラメータ
     * @param response レスポンスパラメータ
     */
    protected void setErrUnknown(final Exception e, final Intent response) {
        mLogger.warning(this, "onPutRequest", "setErrUnknown", e);
        setResult(response, DConnectMessage.RESULT_ERROR);
        MessageUtils.setUnknownError(response);
    }

}
