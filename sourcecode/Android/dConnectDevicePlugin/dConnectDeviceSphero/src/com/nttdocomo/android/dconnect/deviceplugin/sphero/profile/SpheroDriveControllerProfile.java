package com.nttdocomo.android.dconnect.deviceplugin.sphero.profile;

import android.content.Intent;
import android.util.Log;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.BuildConfig;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Controller Profile.
 */
public class SpheroDriveControllerProfile extends DConnectProfile {

    /**
     * プロファイル名.
     */
    public static final String PROFILE_NAME = "drive_controller";

    /**
     * アトリビュート : {@value} .
     */
    public static final String ATTRIBUTE_MOVE = "move";
    
    /**
     * アトリビュート : {@value} .
     */
    public static final String ATTRIBUTE_STOP = "stop";

    /**
     * アトリビュート : {@value} .
     */
    public static final String ATTRIBUTE_ROTATE = "rotate";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_ANGLE = "angle";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_SPEED = "speed";

    
    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        
        String attribute = getAttribute(request);
        if (ATTRIBUTE_ROTATE.equals(attribute)) {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);

            if (info != null) {
                Integer angle = parseInteger(request, PARAM_ANGLE);
                if (angle == null || angle < 0 || angle > 360) {
                    MessageUtils.setInvalidRequestParameterError(response);
                } else {
                    synchronized (info) {
                        info.getDevice().rotate(angle.floatValue());
                    }
                    setResult(response, DConnectMessage.RESULT_OK);
                }
            } else {
                MessageUtils.setNotFoundDeviceError(response);
            }
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }
        
        return true;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {

        String attribute = getAttribute(request);
        if (ATTRIBUTE_STOP.equals(attribute)) {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);

            if (info != null) {
                synchronized (info) {
                    info.getDevice().stop();
                }
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                MessageUtils.setNotFoundDeviceError(response);
            }
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return true;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {

        String attribute = getAttribute(request);
        if (ATTRIBUTE_MOVE.equals(attribute)) {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);

            if (info != null) {
                final Integer angle = parseInteger(request, PARAM_ANGLE);
                final Double speed = parseDouble(request, PARAM_SPEED);
                if (angle == null || speed == null || angle < 0 || angle > 360 || speed < 0 || speed > 1.0) {
                    MessageUtils.setInvalidRequestParameterError(response);
                } else {
                    synchronized (info) {
                        info.getDevice().drive(angle.floatValue(), speed.floatValue());
                    }
                    if (BuildConfig.DEBUG) {
                        Log.d("", "angle : " + angle.floatValue());
                        Log.d("", "speed : " + speed.floatValue());
                    }
                    setResult(response, DConnectMessage.RESULT_OK);
                }
            } else {
                MessageUtils.setNotFoundDeviceError(response);
            }
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return true;
    }

}
