/*
 DConnectDeliveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.profile;

import java.util.List;

import android.content.Intent;

import com.nttdocomo.android.dconnect.DConnectLocalOAuth;
import com.nttdocomo.android.dconnect.DConnectMessageService;
import com.nttdocomo.android.dconnect.DConnectService;
import com.nttdocomo.android.dconnect.DevicePlugin;
import com.nttdocomo.android.dconnect.DevicePluginManager;
import com.nttdocomo.android.dconnect.manager.request.DeliveryRequest;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;

/**
 * 指定されたリクエストを各デバイスプラグインに送信するためのプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class DConnectDeliveryProfile extends DConnectProfile {
    /** デバイスプラグイン管理クラス. */
    private DevicePluginManager mDevicePluginManager;

    /** LocalOAuth管理クラス. */
    private DConnectLocalOAuth mLocalOAuth;

    /**
     * コンストラクタ.
     * @param mgr デバイスプラグイン管理クラス
     * @param auth LocalOAuth管理クラス
     */
    public DConnectDeliveryProfile(final DevicePluginManager mgr, final DConnectLocalOAuth auth) {
        mDevicePluginManager = mgr;
        mLocalOAuth = auth;
    }

    @Override
    public String getProfileName() {
        return "*";
    }

    @Override
    public boolean onRequest(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);

        // TODO wakeup以外にも例外的な動きをするProfileがある場合には再検討すること。
        // System Profileのwakeupは例外的にpluginIdで宛先を決める
        // ここでは、/system/device/wakeupの場合のみpluginIdを使用するようにする
        String profileName = getProfile(request);
        if (SystemProfileConstants.PROFILE_NAME.equals(profileName)) {
            String inter = getInterface(request);
            String attr = getAttribute(request);
            if (SystemProfileConstants.INTERFACE_DEVICE.equals(inter)
                    && SystemProfileConstants.ATTRIBUTE_WAKEUP.equals(attr)) {
                deviceId = request.getStringExtra(SystemProfileConstants.PARAM_PLUGIN_ID);
                if (deviceId == null) {
                    sendEmptyPluginId(request, response);
                    return true;
                }
            }
        }

        if (deviceId == null) {
            sendEmptyDeviceId(request, response);
        } else {
            List<DevicePlugin> plugins = mDevicePluginManager.getDevicePlugins(deviceId);
            if (plugins != null && plugins.size() > 0) {
                DeliveryRequest req = new DeliveryRequest();
                req.setContext(getContext());
                req.setLocalOAuth(mLocalOAuth);
                req.setUseAccessToken(isUseLocalOAuth(profileName));
                req.setRequest(request);
                req.setDevicePluginManager(mDevicePluginManager);
                req.setDestination(plugins.get(0));
                ((DConnectMessageService) getContext()).addRequest(req);
            } else {
                sendNotFoundDevice(request, response);
            }
        }

        return true;
    }

    /**
     * 送信元のリクエストにdeviceIdがnullの場合のエラーを返却する.
     * @param request 送信元のリクエスト
     * @param response レスポンス
     */
    private void sendEmptyDeviceId(final Intent request, final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
        sendResponse(request, response);
    }

    /**
     * 送信元のリクエストにデバイスプラグインが発見できなかったエラーを返却する.
     * @param request 送信元のリクエスト
     * @param response レスポンス
     */
    private void sendNotFoundDevice(final Intent request, final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
        sendResponse(request, response);
    }

    /**
     * 送信元のリクエストにプラグインIDが発見できなかったエラーを返却する.
     * @param request 送信元のリクエスト
     * @param response レスポンス
     */
    private void sendEmptyPluginId(final Intent request, final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response, "pluginId is required.");
        sendResponse(request, response);
    }

    /**
     * リクエストの送信元にレスポンスを返却する.
     * @param request 送信元のリクエスト
     * @param response 返却するレスポンス
     */
    public final void sendResponse(final Intent request, final Intent response) {
        ((DConnectService) getContext()).sendResponse(request, response);
    }

    /**
     * Local OAuthの使用フラグをチェックする.
     * @param profileName プロファイル名
     * @return 使用する場合はtrue,使用しない場合はfalse
     */
    private boolean isUseLocalOAuth(final String profileName) {
        return !mLocalOAuth.checkProfile(profileName);
    }
}
