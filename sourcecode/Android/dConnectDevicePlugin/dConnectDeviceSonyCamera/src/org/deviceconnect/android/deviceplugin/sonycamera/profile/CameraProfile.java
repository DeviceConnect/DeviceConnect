/*
CameraProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.profile;

import android.content.Intent;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;

/**
 * カメラプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class CameraProfile extends DConnectProfile {

    /** プロファイル名. */
    public static final String PROFILE_NAME = "camera";

    /**
     * 属性: {@value} .
     */
    public static final String ATTRIBUTE_ZOOM = "zoom";
    /**
     * パラメータ:{@value} .
     */
    public static final String PARAM_ZOOM_DIAMETER = "zoomdiameter";

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    public boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = false;

        if (attribute.equals(ATTRIBUTE_ZOOM)) {
            String deviceId = getDeviceID(request);
            String direction = getDirection(request);
            String movement = getMovement(request);
            result = onPutActZoom(request, response, deviceId, direction, movement);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }
        return result;
    }

    @Override
    public boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = false;

        if (attribute.equals(ATTRIBUTE_ZOOM)) {
            String deviceId = getDeviceID(request);
            result = onGetZoomDiameter(request, response, deviceId);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }
        return result;
    }

    //---------GET------------
    /**
     * ズーム倍率取得メソッド.
     * @param request request
     * @param response response
     * @param deviceId deviceId
     * @return result
     */
    protected boolean onGetZoomDiameter(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return false;
    }

    /** 
     * Movementパラメータのゲッター.
     * @param request リクエスト
     * @return movement
     */
    public static String getMovement(final Intent request) {
        String movement = request.getStringExtra("movement");
        return movement;
    }

    /**
     *  Directionパラメータのゲッター.
     *  @param request リクエスト
     *  @return direction
     */
    public static String getDirection(final Intent request) {
        String direction = request.getStringExtra("direction");
        return direction;
    }

    // -------------------------------
    // PUT
    // -------------------------------
    /**
     * ズーム機能利用メソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param direction :"in", "out"
     * @param movement :"start", "stop", "1shot"
     * @param deviceId デバイスID
     * @return SonyCameraDeviceService#onPutActZoom
     * @throws IOException
     */
    protected boolean onPutActZoom(final Intent request, final Intent response, final String deviceId,
            final String direction, final String movement) {
        setUnsupportedError(response);
        return false;
    }
}
