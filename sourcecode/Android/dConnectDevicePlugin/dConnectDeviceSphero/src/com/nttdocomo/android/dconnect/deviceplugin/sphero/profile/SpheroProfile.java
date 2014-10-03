package com.nttdocomo.android.dconnect.deviceplugin.sphero.profile;

import java.util.List;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * spheroプロファイル.
 */
public class SpheroProfile extends DConnectProfile {

    /**
     * プロファイル名.
     */
    public static final String PROFILE_NAME = "sphero";

    /**
     * インターフェース : {@value} .
     */
    public static final String INTER_QUATERNION = "quaternion";

    /**
     * インターフェース : {@value} .
     */
    public static final String INTER_LOCATOR = "locator";

    /**
     * インターフェース : {@value} .
     */
    public static final String INTER_COLLISION = "collision";

    /**
     * 属性 : {@value} .
     */
    public static final String ATTR_ON_QUATERNION = "onquaternion";

    /**
     * 属性 : {@value} .
     */
    public static final String ATTR_ON_LOCATOR = "onlocator";

    /**
     * 属性 : {@value} .
     */
    public static final String ATTR_ON_COLLISION = "oncollision";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Q0 = "q0";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Q1 = "q1";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Q2 = "q2";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Q3 = "q3";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_QUATERNION = "quaternion";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_INTERVAL = "interval";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_LOCATOR = "locator";

    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_POSITION_X = "positionX";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_POSITION_Y = "positionY";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_VELOCITY_X = "verocityX";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_VELOCITY_Y = "verocityY";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_COLLISION = "collision";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_IMPACT_ACCELERATION = "impactAcceleration";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_IMPACT_AXIS = "impactAxis";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_IMPACT_POWER = "impactPower";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_IMPACT_SPEED = "impactSpeed";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_IMPACT_TIMESTAMP = "impactTimestamp";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_X = "x";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Y = "y";
    
    /**
     * パラメータ : {@value} .
     */
    public static final String PARAM_Z = "z";
    
    /**
     * リクエストタイプ QUATERNION.
     */
    private static final int TYPE_QUA = 1;

    /**
     * リクエストタイプ LOCATOR.
     */
    private static final int TYPE_LOC = 2;

    /**
     * リクエストタイプ COLLISTION.
     */
    private static final int TYPE_COL = 3;

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {

        String inter = getInterface(request);
        String attribute = getAttribute(request);

        int type = 0;

        if (INTER_QUATERNION.equals(inter) && ATTR_ON_QUATERNION.equals(attribute)) {
            type = TYPE_QUA;
        } else if (INTER_LOCATOR.equals(inter) && ATTR_ON_LOCATOR.equals(attribute)) {
            type = TYPE_LOC;
        } else if (INTER_COLLISION.equals(inter) && ATTR_ON_COLLISION.equals(attribute)) {
            type = TYPE_COL;
        } else {
            MessageUtils.setUnknownAttributeError(response);
            return true;
        }

        String deviceId = getDeviceID(request);
        DeviceInfo device = SpheroManager.INSTANCE.getDevice(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response);
            return true;
        }

        EventError error = EventManager.INSTANCE.addEvent(request);
        boolean registeredEvent = false;
        switch (error) {
        case NONE:
            registeredEvent = true;
            setResult(response, DConnectMessage.RESULT_OK);
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }

        if (registeredEvent) {
            switch (type) {
            case TYPE_QUA:
            case TYPE_LOC:
                SpheroManager.INSTANCE.startSensor(device);
                break;
            case TYPE_COL:
                SpheroManager.INSTANCE.startCollision(device);
                break;
            default:
                break;
            }
        }

        return true;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {

        String inter = getInterface(request);
        String attribute = getAttribute(request);

        int type = 0;

        if (INTER_QUATERNION.equals(inter) && ATTR_ON_QUATERNION.equals(attribute)) {
            type = TYPE_QUA;
        } else if (INTER_LOCATOR.equals(inter) && ATTR_ON_LOCATOR.equals(attribute)) {
            type = TYPE_LOC;
        } else if (INTER_COLLISION.equals(inter) && ATTR_ON_COLLISION.equals(attribute)) {
            type = TYPE_COL;
        } else {
            MessageUtils.setUnknownAttributeError(response);
            return true;
        }

        EventError error = EventManager.INSTANCE.removeEvent(request);
        boolean removedEvent = false;
        switch (error) {
        case NONE:
            removedEvent = true;
            setResult(response, DConnectMessage.RESULT_OK);
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }

        String deviceId = getDeviceID(request);
        DeviceInfo device = SpheroManager.INSTANCE.getDevice(deviceId);

        if (removedEvent) {
            switch (type) {
            case TYPE_QUA:
            case TYPE_LOC:
                if (device != null && !SpheroManager.INSTANCE.hasSensorEvent(device)) {
                    SpheroManager.INSTANCE.stopSensor(device);
                }
                break;
            case TYPE_COL:
                List<Event> events = EventManager.INSTANCE.getEventList(
                        deviceId, PROFILE_NAME, 
                        INTER_COLLISION, ATTR_ON_COLLISION);
                
                if (device != null && events.size() == 0) {
                    SpheroManager.INSTANCE.stopCollision(device);
                }
                break;
            default:
                break;
            }
        }

        return true;
    }

}
