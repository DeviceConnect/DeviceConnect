/*
 NormalAuthorizationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.intent.test;

import com.nttdocomo.dconnect.profile.BatteryProfileConstants;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;

/**
 * Authorizationプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalAuthorizationProfileTestCase extends IntentDConnectTestCase {

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalAuthorizationProfileTestCase(final String string) {
        super(string);
    }

    @Override
    protected boolean isLocalOAuth() {
        return false;
    }

    @Override
    protected boolean isSearchDevices() {
        return false;
    }

    /**
     * クライアント作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/create_client?packageName=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・clientIdにstring型の値が返ること。
     * ・clientSecretにstring型の値が返ること。
     * </pre>
     */
    public void testCreateClient() {
        String[] clientInfo = createClient("abc");
        String clientId = clientInfo[0];
        String clientSecret = clientInfo[1];
        assertNotNull(clientId);
        assertNotNull(clientSecret);
    }

    /**
     * クライアント作成済みのパッケージについてクライアントを作成し直すテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/create_client?packageName=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・異なるclientIdが返ること。
     * ・異なるclientSecretが返ること。
     * </pre>
     */
    public void testCreateClientOverwrite() {
        final String packageName = "abc";
        String[] clientInfo = createClient(packageName);
        assertNotNull(clientInfo);
        assertNotNull(clientInfo[0]);
        assertNotNull(clientInfo[1]);
        String[] newClientInfo = createClient(packageName);
        assertNotNull(newClientInfo);
        assertNotNull(newClientInfo[0]);
        assertNotNull(newClientInfo[1]);
        assertFalse(newClientInfo[0].equals(clientInfo[0]));
        assertFalse(newClientInfo[1].equals(clientInfo[1]));
    }

    /**
     * 1つのスコープに対してアクセストークン取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=notification&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・クライアント作成に成功すること。
     * ・アクセストークン取得に成功すること。
     * </pre>
     */
    public void testRequestAccessToken() {
        String[] clientInfo = createClient("abc");
        String clientId = clientInfo[0];
        String clientSecret = clientInfo[1];
        assertNotNull(clientId);
        assertNotNull(clientSecret);
        
        String accessToken = requestAccessToken(clientId, clientSecret,
                new String[] {NotificationProfileConstants.PROFILE_NAME});
        assertNotNull(accessToken);
    }

    /**
     * 複数のスコープに対してアクセストークン取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=battery,connect,deviceorientation&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・クライアント作成に成功すること。
     * ・アクセストークン取得に成功すること。
     * </pre>
     */
    public void testRequestAccessTokenMultiScope() {
        String[] clientInfo = createClient("abc");
        String clientId = clientInfo[0];
        String clientSecret = clientInfo[1];
        assertNotNull(clientId);
        assertNotNull(clientSecret);
        
        String accessToken = requestAccessToken(clientId, clientSecret, new String[] {
                BatteryProfileConstants.PROFILE_NAME,
                ConnectProfileConstants.PROFILE_NAME,
                DeviceOrientationProfileConstants.PROFILE_NAME,
        });
        assertNotNull(accessToken);
    }
}
