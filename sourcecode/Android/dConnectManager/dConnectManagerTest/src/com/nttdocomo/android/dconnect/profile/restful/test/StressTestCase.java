/*
 StressTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;

import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * dConnectManagerの負荷テスト.
 * @author NTT DOCOMO, INC.
 */
public class StressTestCase extends RESTfulDConnectTestCase {

    /** リクエストの連続送信回数. */
    private static final int REQUEST_COUNT = 1000;

    /** バッファサイズを定義. */
    private static final int BUF_SIZE = 4096;

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public StressTestCase(String tag) {
        super(tag);
    }

    /**
     * 負荷テストを実行する.
     * <p>
     * dConnectManager自身が実装するAPIに対してリクエストを行う.
     * </p>
     */
    public void testStressTestDConnectManagerProfileSystem()  {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(SystemProfileConstants.PROFILE_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        HttpUriRequest request = new HttpGet(builder.toString());
        try {
            JSONObject[] responses = new JSONObject[REQUEST_COUNT];
            for (int i = 0; i < responses.length; i++) {
                // リクエスト送信間隔をできるだけ短くするために、レスポンスのチェックは後まわしにする.
                responses[i] = sendRequest(request);
            }
            for (int i = 0; i < responses.length; i++) {
                assertResultOK(responses[i]);
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 負荷テストを実行する.
     * <p>
     * リクエストに含まれるバイナリデータの一時保存処理に対して負荷をかける.
     * </p>
     */
    public void testStressTestDConnectManagerProfileFileSend() throws IOException  {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(SystemProfileConstants.PROFILE_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        HttpUriRequest request = createFileSendRequest();
        try {
            JSONObject[] responses = new JSONObject[REQUEST_COUNT];
            for (int i = 0; i < responses.length; i++) {
                // リクエスト送信間隔をできるだけ短くするために、レスポンスのチェックは後まわしにする.
                responses[i] = sendRequest(request);
            }
            for (int i = 0; i < responses.length; i++) {
                assertResultOK(responses[i]);
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 負荷テストを実行する.
     * <p>
     * テスト用デバイスプラグインの実装するAPIに対してリクエストを行う.
     * </p>
     */
    public void testStressTestDevicePluginProfile() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile("unique");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        HttpUriRequest request = new HttpGet(builder.toString());
        try {
            JSONObject[] responses = new JSONObject[REQUEST_COUNT];
            for (int i = 0; i < responses.length; i++) {
                // リクエスト送信間隔をできるだけ短くするために、レスポンスのチェックは後まわしにする.
                responses[i] = sendRequest(request);
            }
            for (int i = 0; i < responses.length; i++) {
                assertResultOK(responses[i]);
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 負荷テストを実行する.
     * <p>
     * dConnectManagerに対して複数同時リクエストを行う.
     * </p>
     * @throws InterruptedException スレッドに割り込みが発生した場合
     */
    public void testStressTestDConnectManagerAsync() throws InterruptedException  {
        final int num = 100;
        final JSONObject[] responses = new JSONObject[num];
        final Count count = new Count(num);
        // スレッドの準備
        Thread[] threads = new Thread[num];
        for (int i = 0; i < num; i++) {
            final int pos = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    URIBuilder builder = TestURIBuilder.createURIBuilder();
                    builder.setProfile("unique");
                    builder.setAttribute("heavy");
                    builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
                    builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
                    builder.addParameter("key", Integer.toString(pos));
                    final HttpUriRequest request = new HttpGet(builder.toString());
                    responses[pos] = sendRequest(request);
                    count.signal();
                }
            });
        }
        // dConnectManagerへの複数同時アクセスを実行
        for (int i = 0; i < num; i++) {
            threads[i].start();
        }
        count.start();
        try {
            for (int i = 0; i < responses.length; i++) {
                JSONObject response = responses[i];
                assertResultOK(response);
                assertTrue(response.has("key"));
                String key = response.getString("key");
                assertEquals(Integer.toString(i), key);
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 負荷テストを実行する.
     * <p>
     * dConnectManagerに対して複数同時リクエストを行う.
     * 特に、イベント管理DBに対する負荷テストを行う.
     * </p>
     * @throws InterruptedException スレッドに割り込みが発生した場合
     */
    public void testStressTestDConnectManagerEventRegisterAsync() throws InterruptedException  {
        stressEventAttribute("PUT");
        stressEventAttribute("DELETE");
    }

    private void stressEventAttribute(final String method) throws InterruptedException {
        final int num = 50;
        final JSONObject[] responses = new JSONObject[num];
        final Count count = new Count(num);
        // スレッドの準備
        Thread[] threads = new Thread[num];
        for (int i = 0; i < num; i++) {
            final int pos = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    URIBuilder builder = TestURIBuilder.createURIBuilder();
                    builder.setProfile("unique");
                    builder.setAttribute("event");
                    builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
                    builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
                    builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
                    builder.addParameter("key", Integer.toString(pos));
                    final HttpUriRequest request;
                    if ("PUT".equals(method)) {
                        request = new HttpPut(builder.toString());
                    } else if ("DELETE".equals(method)) {
                        request = new HttpDelete(builder.toString());
                    } else {
                        request = null;
                    }
                    if (request != null) {
                        responses[pos] = sendRequest(request);
                    }
                    count.signal();
                }
            });
        }
        // dConnectManagerへの複数同時アクセスを実行
        for (int i = 0; i < num; i++) {
            threads[i].start();
        }
        count.start();
        try {
            for (int i = 0; i < responses.length; i++) {
                JSONObject response = responses[i];
                assertResultOK(response);
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    private HttpUriRequest createFileSendRequest() throws IOException {
        final String name = "test.png";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(FileProfileConstants.PARAM_PATH, "/test/test.png");

        AssetManager manager = getApplicationContext().getAssets();
        InputStream in = null;
        try {
            MultipartEntity entity = new MultipartEntity();
            in = manager.open(name);
            // ファイルのデータを読み込む
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[BUF_SIZE];
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            // ボディにデータを追加
            entity.addPart(FileProfileConstants.PARAM_DATA, new BinaryBody(baos.toByteArray(), name));

            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(entity);
            return request;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private static class Count {
        int cnt;
        Count(int cnt) {
            this.cnt = cnt;
        }
        synchronized void signal() {
            cnt--;
            notify();
        }
        synchronized void start() throws InterruptedException {
            while (cnt > 0) {
                wait();
            }
        }
    }
}
