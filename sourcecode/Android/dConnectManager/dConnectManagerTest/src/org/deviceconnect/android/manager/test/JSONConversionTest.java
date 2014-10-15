/*
 JSONConversionTest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.test;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.deviceconnect.android.profile.restful.test.RESTfulDConnectTestCase;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;


/**
 * {@link Bundle}を{@link JSONObject}に変換するテストを実行する.
 * @author NTT DOCOMO, INC.
 */
public class JSONConversionTest extends RESTfulDConnectTestCase {

    /**
     * テスト用浮動小数点値: {@value}.
     */
    private static final double TEST_FLOATING_VALUE = 0.01;

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public JSONConversionTest(final String tag) {
        super(tag);
    }

    /**
     * dConnectManagerがBundleをJSONへ正しく変換していることを確認するテスト.
     * 
     * @throws JSONException レスポンスの解析に失敗した場合
     */
    public void testConversion() throws JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/json_test");
        builder.append("?");
        builder.append(DConnectMessage.EXTRA_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectMessage.EXTRA_ACCESS_TOKEN + "=" + getAccessToken());
        HttpUriRequest request = new HttpGet(builder.toString());
        JSONObject response = sendRequest(request);
        assertResultOK(response);
        JSONObject root = response.getJSONObject("extra");
        Assert.assertNotNull("root is null.", root);
        Assert.assertFalse(root.has(IntentDConnectMessage.EXTRA_REQUEST_CODE));
        Assert.assertEquals("http://localhost:8080", root.getString("uri"));
        Assert.assertEquals(0, root.getInt("byte"));
        Assert.assertEquals('0', root.getInt("char"));
        Assert.assertEquals(0, root.getInt("int"));
        Assert.assertEquals(0L, root.getLong("long"));
        Assert.assertEquals(0.0, root.getDouble("float"), TEST_FLOATING_VALUE);
        Assert.assertEquals(0.0, root.getDouble("double"), TEST_FLOATING_VALUE);
        Assert.assertEquals(false, root.getBoolean("boolean"));
        Assert.assertEquals(0, root.getInt(Byte.class.getName()));
        Assert.assertEquals('0', root.getInt(Character.class.getName()));
        Assert.assertEquals(0, root.getInt(Integer.class.getName()));
        Assert.assertEquals(0L, root.getLong(Long.class.getName()));
        Assert.assertEquals(0.0, root.getDouble(Float.class.getName()), TEST_FLOATING_VALUE);
        Assert.assertEquals(0.0, root.getDouble(Double.class.getName()), TEST_FLOATING_VALUE);
        Assert.assertEquals(false, root.getBoolean(Boolean.class.getName()));
        Assert.assertEquals(String.class.getName(), root.getString(String.class.getName()));
        Assert.assertEquals(1, root.getJSONArray(int[].class.getName()).length());
        Assert.assertEquals(0, root.getJSONArray(int[].class.getName()).get(0));
        Assert.assertEquals(1, root.getJSONArray(long[].class.getName()).length());
        Assert.assertEquals(0L, root.getJSONArray(long[].class.getName()).getLong(0));
        Assert.assertEquals(1, root.getJSONArray(float[].class.getName()).length());
        Assert.assertEquals(0.0, root.getJSONArray(float[].class.getName()).getDouble(0), TEST_FLOATING_VALUE);
        Assert.assertEquals(1, root.getJSONArray(double[].class.getName()).length());
        Assert.assertEquals(0.0, root.getJSONArray(double[].class.getName()).getDouble(0), TEST_FLOATING_VALUE);
        Assert.assertEquals(1, root.getJSONArray(boolean[].class.getName()).length());
        Assert.assertEquals(false, root.getJSONArray(boolean[].class.getName()).get(0));
        Assert.assertEquals(1, root.getJSONArray(Integer[].class.getName()).length());
        Assert.assertEquals(0, root.getJSONArray(Integer[].class.getName()).get(0));
        Assert.assertEquals(1, root.getJSONArray(Long[].class.getName()).length());
        Assert.assertEquals(0L, root.getJSONArray(Long[].class.getName()).getLong(0));
        Assert.assertEquals(1, root.getJSONArray(Float[].class.getName()).length());
        Assert.assertEquals(0.0, root.getJSONArray(Float[].class.getName()).getDouble(0), TEST_FLOATING_VALUE);
        Assert.assertEquals(1, root.getJSONArray(Double[].class.getName()).length());
        Assert.assertEquals(0.0, root.getJSONArray(Double[].class.getName()).getDouble(0), TEST_FLOATING_VALUE);
        Assert.assertEquals(1, root.getJSONArray(Boolean[].class.getName()).length());
        Assert.assertEquals(false, root.getJSONArray(Boolean[].class.getName()).get(0));
        Assert.assertEquals(1, root.getJSONArray(String[].class.getName()).length());
        Assert.assertEquals("String", root.getJSONArray(String[].class.getName()).get(0));
        Assert.assertNotNull(root.getJSONObject(Bundle.class.getName()));
        Assert.assertEquals(1, root.getJSONArray(Bundle[].class.getName()).length());
        Assert.assertNotNull(root.getJSONArray(Bundle[].class.getName()).get(0));
        Assert.assertEquals(1, root.getJSONArray("ArrayList<Integer>").length());
        Assert.assertEquals(0, root.getJSONArray("ArrayList<Integer>").get(0));
    }

}
