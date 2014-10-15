/*
 ConfirmAuthActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.activity;

import org.deviceconnect.android.localoauth.fragment.ConfirmAuthFragmentListener;
import org.deviceconnect.android.localoauth.fragment.ConfirmAuthFramgment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 認証確認画面.<br>
 * ・Intentに下記のパラメータを指定して起動すること。<br>
 * - EXTRA_THREADID : ServiceのスレッドIDを設定する。<br>
 * - EXTRA_APPLICATIONNAME : アプリケーション名を設定する。<br>
 * - EXTRA_SCOPES : スコープを設定する。<br>
 * - EXTRA_DISPLAY_SCOPES : 表示スコープを設定する。<br>
 * - EXTRA_MESSAGEID :
 * 許可／拒否のステータスを、BindされたServiceにMessage通知する際に使用するメッセージIDを設定する。<br>
 * Messageのarg1にスレッドIDを、arg2に承認ステータス(1なら許可/0なら拒否)を返す。<br>
 * - EXTRA_DEVICEID : デバイスID(デバイスプラグインの場合のみ設定する)<br>
 * - EXTRA_IS_FOR_DEVICEPLUGIN : デバイスプラグインの認証確認画面の場合はtrueを、アプリの場合はfalseを設定する。<br>
 * - EXTRA_SERVICE_PACKAGE_NAME : Messageを受信するServiceのパッケージ名.<br>
 * @author NTT DOCOMO, INC.
 */
public class ConfirmAuthActivity extends FragmentActivity implements ConfirmAuthFragmentListener {

    /** EXTRA: 呼び出し元のスレッドID. */
    public static final String EXTRA_THREADID = "thread_id";

    /** EXTRA: アプリケーション名(例: "SW2デバイスプラグイン", "Twitterイベントプラグイン"). */
    public static final String EXTRA_APPLICATIONNAME = "application_name";

    /** EXTRA: スコープ(例: "file", "notification", "vibration"). */
    public static final String EXTRA_SCOPES = "scopes";

    /** EXTRA: 表示スコープ(例: "ファイル", "通知", "バイブレーション"). */
    public static final String EXTRA_DISPLAY_SCOPES = "display_scopes";
    
    /** EXTRA: デバイスプラグイン用の発行？(true: デバイスプラグイン用 / false: アプリ用). */
    public static final String EXTRA_IS_FOR_DEVICEPLUGIN = "isForDeviceplugin";

    /** EXTRA: デバイスID(デバイスプラグインの場合のみ設定する). */
    public static final String EXTRA_DEVICEID = "deviceId";

    /** EXTRA: 承認・拒否されたかを返すメッセージID. */
    public static final String EXTRA_APPROVAL_MESSAGEID = "approvalMessageId";
    
    /** EXTRA: threadIdが有効かを返すメッセージID. */
    public static final String EXTRA_CHECK_THREADID_MESSAGEID = "checkThreadIdMessageId";
    
    

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfirmAuthFramgment fragment = new ConfirmAuthFramgment();
        fragment.setDialogListener(this);
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * OKボタンをおした時.
     */
    public void doPositiveClick() {
        finish();
    }

    /**
     * NGボタンをおした時.
     */
    @Override
    public void doNegativeClick() {
        finish();
    }
}
