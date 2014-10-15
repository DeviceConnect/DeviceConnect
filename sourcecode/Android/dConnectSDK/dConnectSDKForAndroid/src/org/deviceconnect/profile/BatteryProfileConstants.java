/*
 BatteryProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Battery Profile API 定数群.<br/>
 * Battery Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface BatteryProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value}.
     */
    String PROFILE_NAME = "battery";
    
    /** 
     * 属性: {@value}.
     */
    String ATTRIBUTE_ON_CHARGING_CHANGE = "onchargingchange";
    
    /** 
     * 属性: {@value}.
     */
    String ATTRIBUTE_ON_BATTERY_CHANGE = "onbatterychange";
    
    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_CHARGING = "charging";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_CHARGING_TIME = "chargingTime";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_DISCHARGING_TIME = "dischargingTime";
    
    /** 
     * 属性: {@value}.
     */
    String ATTRIBUTE_LEVEL = "level";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /** 
     * パス: {@value}.
     */
    String PATH_ON_CHARGING_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_CHARGING_CHANGE;

    /** 
     * 属性: {@value}.
     */
    String PATH_ON_BATTERY_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_BATTERY_CHANGE;

    /**
     * 属性: {@value}.
     */
    String PATH_CHARGING = PATH_PROFILE + SEPARATOR + ATTRIBUTE_CHARGING;

    /**
     * 属性: {@value}.
     */
    String PATH_CHARGING_TIME = PATH_PROFILE + SEPARATOR + ATTRIBUTE_CHARGING_TIME;

    /**
     * 属性: {@value}.
     */
    String PATH_DISCHARGING_TIME = PATH_PROFILE + SEPARATOR + ATTRIBUTE_DISCHARGING_TIME;

    /** 
     * 属性: {@value}.
     */
    String PATH_LEVEL = PATH_PROFILE + SEPARATOR + ATTRIBUTE_LEVEL;
    
    /**
     * パラメータ: {@value}.
     */
    String PARAM_CHARGING = "charging";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_CHARGING_TIME = "chargingTime";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_DISCHARGING_TIME = "dischargingTime";
    
    /** 
     * パラメータ: {@value}.
     */
    String PARAM_LEVEL = "level";
    
    /** 
     * パラメータ: {@value}.
     */
    String PARAM_BATTERY = "battery";

}
