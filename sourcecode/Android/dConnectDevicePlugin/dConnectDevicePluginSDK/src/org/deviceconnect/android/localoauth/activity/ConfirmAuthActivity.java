/*
 ConfirmAuthActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.activity;

import org.deviceconnect.android.R;
import org.deviceconnect.android.localoauth.fragment.ConfirmAuthFramgment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

/**
 * 認証確認画面.
 * <p>
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
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ConfirmAuthActivity extends Activity {

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

    /** 承認を表す定数. */
    public static final int APPROVAL = 1;

    /** 不承認を表す定数. */
    public static final int DISAPPROVAL = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm_auth);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        } else {
            FragmentManager mgr = getFragmentManager();
            ConfirmAuthFramgment fragment = (ConfirmAuthFramgment) mgr
                    .findFragmentById(R.id.container);
            fragment.notApprovalProc();
            return false;
        }
    }
}
