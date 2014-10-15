/*
 SpheroLightProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.profile;

import java.util.ArrayList;

import org.deviceconnect.android.deviceplugin.sphero.SpheroManager;
import org.deviceconnect.android.deviceplugin.sphero.data.DeviceInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * Lightプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class SpheroLightProfile extends DConnectProfile {

    /**
     * プロファイル名.
     */
    public static final String PROFILE_NAME = "light";

    /**
     * 本体の色設定用ライトのID.
     */
    private static final String COLOR_LED_LIGHT_ID = "1";

    /**
     * バックライトのID.
     */
    private static final String BACK_LED_LIGHT_ID = "2";

    /**
     * 本体の色設定用ライトの名前.
     */
    private static final String COLOR_LED_LIGHT_NAME = "Sphero LED";

    /**
     * バックライトの名前.
     */
    private static final String BACK_LED_LIGHT_NAME = "Sphero CalibrationLED";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_LIGHT_ID = "lightId";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_NAME = "name";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_ON = "on";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_LIGHTS = "lights";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_COLOR = "color";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_FLASHING = "flashing";

    /**
     * パラメータ: {@value} .
     */
    public static final String PARAM_BRIGHTNESS = "brightness";
    
    /** 
     * brightnessの最大値.
     */
    public static final int MAX_BRIGHTNESS = 255;

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {

        String attribute = getAttribute(request);
        if (attribute != null && attribute.length() != 0) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);
            if (info == null) {
                MessageUtils.setNotFoundDeviceError(response);
            } else {
                Bundle[] lights = new Bundle[2];
                synchronized (info) {
                    lights[0] = new Bundle();
                    lights[0].putString(PARAM_LIGHT_ID, COLOR_LED_LIGHT_ID);
                    lights[0].putString(PARAM_NAME, COLOR_LED_LIGHT_NAME);
                    lights[0].putBoolean(PARAM_ON, (Color.BLACK != info.getColor()));

                    lights[1] = new Bundle();
                    lights[1].putString(PARAM_LIGHT_ID, BACK_LED_LIGHT_ID);
                    lights[1].putString(PARAM_NAME, BACK_LED_LIGHT_NAME);
                    lights[1].putBoolean(PARAM_ON, info.getBackBrightness() > 0);
                    
                }
                response.putExtra(PARAM_LIGHTS, lights);
                setResult(response, DConnectMessage.RESULT_OK);
            }
        }

        return true;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {

        String attribute = getAttribute(request);
        if (attribute != null && attribute.length() != 0) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);
            if (info == null) {
                MessageUtils.setNotFoundDeviceError(response);
            } else {
                synchronized (info) {
                    String lightId = request.getStringExtra(PARAM_LIGHT_ID);
                    if (lightId == null) {
                        MessageUtils.setInvalidRequestParameterError(response);
                    } else if (lightId.equals(COLOR_LED_LIGHT_ID)) {

                        String color = request.getStringExtra(PARAM_COLOR);
                        String flashing = request.getStringExtra(PARAM_FLASHING);
                        int[] colors = parseColor(color);
                        long[] pattern = parsePattern(flashing);

                        if (colors == null) {
                            MessageUtils.setInvalidRequestParameterError(response);
                        } else if (pattern == null) {
                            info.setColor(colors[0], colors[1], colors[2]);
                            setResult(response, DConnectMessage.RESULT_OK);
                        } else {
                            SpheroManager.flashFrontLight(info, colors, pattern);
                            setResult(response, DConnectMessage.RESULT_OK);
                        }

                    } else if (lightId.equals(BACK_LED_LIGHT_ID)) {
                        int brightness = request.getIntExtra(PARAM_BRIGHTNESS, MAX_BRIGHTNESS);
                        if (brightness > MAX_BRIGHTNESS || brightness < 0) {
                            MessageUtils.setInvalidRequestParameterError(response);
                        } else {
                            String flashing = request.getStringExtra(PARAM_FLASHING);
                            long[] pattern = parsePattern(flashing);
                            
                            if (pattern != null) {
                                SpheroManager.flashBackLight(info, brightness, pattern);
                            } else {
                                float bf = brightness / (float) MAX_BRIGHTNESS;
                                info.setBackBrightness(bf);
                            }
                            
                            setResult(response, DConnectMessage.RESULT_OK);
                        }
                    } else {
                        MessageUtils.setInvalidRequestParameterError(response);
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {

        String attribute = getAttribute(request);
        if (attribute != null && attribute.length() != 0) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            DeviceInfo info = SpheroManager.INSTANCE.getDevice(deviceId);
            if (info == null) {
                MessageUtils.setNotFoundDeviceError(response);
            } else {
                synchronized (info) {
                    String lightId = request.getStringExtra(PARAM_LIGHT_ID);
                    if (lightId == null) {
                        MessageUtils.setInvalidRequestParameterError(response);
                    } else if (lightId.equals(COLOR_LED_LIGHT_ID)) {
                        info.setColor(0, 0, 0);
                        setResult(response, DConnectMessage.RESULT_OK);
                    } else if (lightId.equals(BACK_LED_LIGHT_ID)) {
                        info.setBackBrightness(0.0f);
                        setResult(response, DConnectMessage.RESULT_OK);
                    } else {
                        MessageUtils.setInvalidRequestParameterError(response);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 16進数の色をRGBの配列に変換する.
     * 
     * @param color カラー
     * @return RGBの配列
     */
    private int[] parseColor(final String color) {

        if (color == null || color.length() == 0) {
            return new int[] { 255, 255, 255 };
        }

        int[] c = new int[3];
        try {
            c[0] = Integer.parseInt(color.substring(0, 2), 16);
            c[1] = Integer.parseInt(color.substring(2, 4), 16);
            c[2] = Integer.parseInt(color.substring(4, 6), 16);
        } catch (Exception e) {
            return null;
        }

        return c;
    }

    /**
     * フラッシュパターンを文字列から解析し、数値の配列に変換する.<br/>
     * 数値の前後の半角のスペースは無視される。その他の半角、全角のスペースは不正なフォーマットとして扱われる。
     * 
     * @param pattern フラッシュパターン文字列。
     * @return 鳴動パターンの配列。解析できないフォーマットの場合nullを返す。
     */
    protected final long[] parsePattern(final String pattern) {

        if (pattern == null || pattern.length() == 0) {
            return null;
        }

        long[] result = null;

        if (pattern.contains(",")) {
            String[] times = pattern.split(",");
            ArrayList<Long> values = new ArrayList<Long>();
            for (String time : times) {
                try {
                    String valueStr = time.trim();
                    if (valueStr.length() == 0) {
                        if (values.size() != times.length - 1) {
                            // 数値の間にスペースがある場合はフォーマットエラー
                            // ex. 100, , 100
                            values.clear();
                        }
                        break;
                    }
                    long value = Long.parseLong(time.trim());
                    values.add(value);
                } catch (NumberFormatException e) {
                    values.clear();
                    mLogger.warning("Exception in the VibrationProfile#parsePattern() method. " + e.toString());
                    break;
                }
            }

            if (values.size() != 0) {
                result = new long[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    result[i] = values.get(i);
                }
            }
        } else {
            try {
                long time = Long.parseLong(pattern);
                result = new long[] { time };
            } catch (NumberFormatException e) {
                mLogger.warning("Exception in the VibrationProfile#parsePattern() method. " + e.toString());
            }
        }

        return result;
    }
}
