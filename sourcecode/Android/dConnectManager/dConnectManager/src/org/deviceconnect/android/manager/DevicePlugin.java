/*
 DevicePlugin.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;

/**
 * デバイスプラグイン.
 * @author NTT DOCOMO, INC.
 */
public class DevicePlugin {
    /** デバイスプラグインのパッケージ名. */
    private String mPackageName;
    /** デバイスプラグインのクラス名. */
    private String mClassName;
    /** デバイスID. */
    private String mDeviceId;
    /** デバイスプラグイン名. */
    private String mDeviceName;
    /**
     * サポートしているプロファイルを格納する.
     */
    private List<String> supports = new ArrayList<String>();

    /**
     * デバイスプラグインのパッケージ名を取得する.
     * @return パッケージ名
     */
    public String getPackageName() {
        return mPackageName;
    }
    /**
     * デバイスプラグインのパッケージ名を設定する.
     * @param packageName パッケージ名
     */
    public void setPackageName(final String packageName) {
        this.mPackageName = packageName;
    }
    /**
     * デバイスプラグインのクラス名を取得する.
     * @return クラス名
     */
    public String getClassName() {
        return mClassName;
    }
    /**
     * デバイスプラグインのクラス名を設定する.
     * @param className クラス名
     */
    public void setClassName(final String className) {
        this.mClassName = className;
    }
    /**
     * デバイスプラグインIDを取得する.
     * @return デバイスプラグインID
     */
    public String getDeviceId() {
        return mDeviceId;
    }
    /**
     * デバイスプラグインIDを設定する.
     * @param deviceId デバイスプラグインID
     */
    public void setDeviceId(final String deviceId) {
        this.mDeviceId = deviceId;
    }
    /**
     * デバイスプラグイン名を取得する.
     * @return デバイスプラグイン名
     */
    public String getDeviceName() {
        return mDeviceName;
    }
    /**
     * デバイスプラグイン名を設定する.
     * @param deviceName デバイスプラグイン名
     */
    public void setDeviceName(final String deviceName) {
        mDeviceName = deviceName;
    }
    /**
     * ComponentNameを取得する.
     * @return ComponentNameのインスタンス
     */
    public ComponentName getComponentName() {
        return new ComponentName(mPackageName, mClassName);
    }
    /**
     * サポートするプロファイルを追加する.
     * @param profileName プロファイル名
     */
    public void addProfile(final String profileName) {
        supports.add(profileName);
    }
    /**
     * サポートするプロファイルを設定する.
     * @param profiles プロファイル名一覧
     */
    public void setSupportProfiles(final List<String> profiles) {
        supports = profiles;
    }
    /**
     * デバイスプラグインがサポートするプロファイルの一覧を取得する.
     * @return サポートするプロファイルの一覧
     */
    public List<String> getSupportProfiles() {
        return supports;
    }
    
    @Override
    public String toString() {
        return "DeviceId: " + mDeviceId + "DeviceName: " + mDeviceName
                + " package: " + mPackageName + " class: " + mClassName;
    }
}
