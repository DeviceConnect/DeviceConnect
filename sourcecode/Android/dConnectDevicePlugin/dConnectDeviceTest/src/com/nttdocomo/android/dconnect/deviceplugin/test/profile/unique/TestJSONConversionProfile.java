/*
 TestJSONConversionProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.test.profile.unique;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * レスポンスのIntentに格納した値がJSONに変換されることをチェックするテスト.
 * @author NTT DOCOMO, INC.
 */
public class TestJSONConversionProfile extends DConnectProfile {

    private static final String PROFILE_NAME = "json_test";

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(Intent request, Intent response) {
        Bundle b = new Bundle();
        b.putString(IntentDConnectMessage.EXTRA_REQUEST_CODE, "");
        b.putString("uri", "http://localhost:8080");
        b.putByte("byte", (byte) 0);
        b.putChar("char", '0');
        b.putInt("int", 0);
        b.putLong("long", 0L);
        b.putFloat("float", 0.0f);
        b.putDouble("double", 0.0d);
        b.putBoolean("boolean", false);
        b.putSerializable(Byte.class.getName(), new Byte((byte) 0));
        b.putSerializable(Character.class.getName(), new Character('0'));
        b.putSerializable(Integer.class.getName(), new Integer(0));
        b.putSerializable(Long.class.getName(), new Long(0L));
        b.putSerializable(Float.class.getName(), new Float(0.0f));
        b.putSerializable(Double.class.getName(), new Double(0.0d));
        b.putSerializable(Boolean.class.getName(), new Boolean(false));
        b.putString(String.class.getName(), String.class.getName());
        b.putSerializable(int[].class.getName(), new int[] {0});
        b.putSerializable(long[].class.getName(), new long[] {0L});
        b.putSerializable(float[].class.getName(), new float[] {0.0f});
        b.putSerializable(double[].class.getName(), new double[] {0.0d});
        b.putSerializable(boolean[].class.getName(), new boolean[] {false});
        b.putSerializable(Integer[].class.getName(), new Integer[] {0});
        b.putSerializable(Long[].class.getName(), new Long[] {0L});
        b.putSerializable(Float[].class.getName(), new Float[] {0.0f});
        b.putSerializable(Double[].class.getName(), new Double[] {0.0d});
        b.putSerializable(Boolean[].class.getName(), new Boolean[] {false});
        b.putStringArray(String[].class.getName(), new String[] {"String"});
        b.putBundle(Bundle.class.getName(), new Bundle());
        b.putParcelableArray(Bundle[].class.getName(), new Bundle[]{new Bundle()});
        ArrayList<Integer> objList = new ArrayList<Integer>();
        objList.add(0);
        b.putIntegerArrayList("ArrayList<Integer>", objList);
        setResult(response, DConnectMessage.RESULT_OK);
        response.putExtra("extra", b);
        return true;
    }

}
