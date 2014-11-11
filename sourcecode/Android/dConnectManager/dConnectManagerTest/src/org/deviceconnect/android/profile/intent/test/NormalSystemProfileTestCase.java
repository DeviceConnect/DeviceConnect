/*
 NormalSystemProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestSystemProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.SystemProfileConstants;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcelable;



/**
 * Network Service Discoveryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalSystemProfileTestCase extends IntentDConnectTestCase
    implements TestSystemProfileConstants {

    /** テスト用デバイスプラグインID. */
    private String testPluginID;

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalSystemProfileTestCase(final String string) {
        super(string);
    }

    /**
     * スマートフォンのシステムプロファイルを取得する.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra: 
     *     profile=system
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetSystem() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SystemProfileConstants.PROFILE_NAME);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(VERSION,
                response.getStringExtra(SystemProfileConstants.PARAM_VERSION));
        String[] supports = response.getStringArrayExtra(SystemProfileConstants.PARAM_SUPPORTS);
        assertNotNull(supports);
        Parcelable[] plugins = response.getParcelableArrayExtra(SystemProfileConstants.PARAM_PLUGINS);
        assertNotNull(plugins);
        Bundle testPlugin = null;
        for (int i = 0; i < plugins.length; i++) {
            Bundle plugin = (Bundle) plugins[i];
            if ("Device Connect Device Plugin for Test".equals(plugin.getString(SystemProfileConstants.PARAM_NAME))) {
                testPlugin = plugin;
                break;
            }
        }
        assertNotNull(testPlugin);
        String id = testPlugin.getString(SystemProfileConstants.PARAM_ID);
        assertNotNull(id);
        testPluginID = id;
        assertNotNull(testPlugin.getString(SystemProfileConstants.PARAM_NAME));
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra: 
     *     profile=system
     *     attribute=device
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetSystemDevice() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SystemProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SystemProfileConstants.ATTRIBUTE_DEVICE);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(getCurrentVersionName(),
                response.getStringExtra(SystemProfileConstants.PARAM_VERSION));
        String[] supports = response.getStringArrayExtra(SystemProfileConstants.PARAM_SUPPORTS);
        assertNotNull(supports);
        Bundle connect = response.getBundleExtra(SystemProfileConstants.PARAM_CONNECT);
        assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_WIFI));
        assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_BLUETOOTH));
        assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_NFC));
        assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_BLE));
    }

    /**
     * デバイスプラグインの機能を有効にするテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=system
     *     interface=device
     *     attribute=wakeup
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・versionに"1.0"が返ってくること。
     * </pre>
     */
    public void testPutSystemWakeup() {
        testGetSystem();
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(SystemProfileConstants.PARAM_PLUGIN_ID, testPluginID);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SystemProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SystemProfileConstants.ATTRIBUTE_DEVICE);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SystemProfileConstants.ATTRIBUTE_WAKEUP);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * AndroidManifest.xmlのversionNameを取得する.
     * @return versionName
     */
    private String getCurrentVersionName() {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }
}
