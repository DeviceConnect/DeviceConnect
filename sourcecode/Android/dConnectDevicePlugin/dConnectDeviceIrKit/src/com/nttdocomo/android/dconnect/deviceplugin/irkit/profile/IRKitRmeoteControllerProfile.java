package com.nttdocomo.android.dconnect.deviceplugin.irkit.profile;


import android.content.Intent;
import android.util.Log;

import com.nttdocomo.android.dconnect.deviceplugin.irkit.BuildConfig;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitDevice;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitManager;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitManager.GetMessageCallback;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitManager.PostMessageCallback;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * IrKit Profile.
 */
public class IRKitRmeoteControllerProfile extends DConnectProfile {
    
    /** Debug . */
    private static final String TAG = "IRKit";

    /** プロファイル名. */
    public static final String PROFILE_NAME = "remote_controller";
    
    /** 
     * パラメータ: {@value} .
     */
    public static final String PARAM_MESSAGE = "message";

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    public boolean onGetRequest(final Intent request, final Intent response) {
        
        boolean send = true;
        String attribute = getAttribute(request);
        if (attribute != null && attribute.length() != 0) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            final IRKitDeviceService service = (IRKitDeviceService) getContext();
            IRKitDevice device = service.getDevice(deviceId);
            
            if (device == null) {
                MessageUtils.setNotFoundDeviceError(response);
            } else {
                send = false;
                IRKitManager.INSTANCE.fetchMessage(device.getIp(), new GetMessageCallback() {
                    
                    @Override
                    public void onGetMessage(final String message) {
                        if (message == null) {
                            MessageUtils.setUnknownError(response);
                        } else {
                            response.putExtra(PARAM_MESSAGE, message);
                            setResult(response, DConnectMessage.RESULT_OK);
                        }
                        service.sendResponse(response);
                    }
                });
            }
        }
        return send;
    }

    @Override
    public boolean onPostRequest(final Intent request, final Intent response) {

        boolean send = true;
        String attribute = getAttribute(request);

        if (attribute != null && attribute.length() != 0) {
            MessageUtils.setUnknownAttributeError(response);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onPostRequest setUnknownAttributeError error");
            }
        } else {
            String deviceId = getDeviceID(request);
            String message = request.getStringExtra(PARAM_MESSAGE);
            final IRKitDeviceService service = (IRKitDeviceService) getContext();
            IRKitDevice device = service.getDevice(deviceId);
            
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onPostRequest service=" + service + " device" + device);
            }
            
            if (device == null) {
                MessageUtils.setNotFoundDeviceError(response);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostRequest setNotFoundDeviceError");
                }
            } else if (message == null) {
                MessageUtils.setInvalidRequestParameterError(response);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostRequest setInvalidRequestParameterError");
                }
            } else {
                send = false;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostRequest ip=" + device.getIp() + " message=" + message);
                }
                IRKitManager.INSTANCE.sendMessage(device.getIp(), message, new PostMessageCallback() {
                    @Override
                    public void onPostMessage(final boolean result) {
                        if (result) {
                            setResult(response, DConnectMessage.RESULT_OK);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "onPostRequest setUnknownError");
                            }
                            MessageUtils.setUnknownError(response);
                        }
                        service.sendResponse(response);
                    }
                });
            }
        }
        return send;
    }
}
