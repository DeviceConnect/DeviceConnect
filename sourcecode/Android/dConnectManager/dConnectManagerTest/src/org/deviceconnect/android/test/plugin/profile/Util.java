/*
 Util.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;

import android.content.Context;
import android.content.Intent;

/**
 * テスト用ユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
final class Util {

    /**
     * ブロードキャストを何ミリ秒後に送信するか.
     */
    private static final long DEFAULT_DELAY = 1000;

    /**
     * 指定したミリ秒後に別スレッドでインテントをブロードキャストする.
     * @param context コンテキスト
     * @param intent インテント
     * @param delay 遅延設定 (単位はミリ秒)
     */
    private static void sendBroadcast(final Context context, final Intent intent, final long delay) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    context.sendBroadcast(intent);
                } catch (InterruptedException e) {
                    // do nothing.
                    return;
                }
            }
        })).start();
    }

    /**
     * {@value #DEFAULT_DELAY}ミリ秒後に別スレッドでインテントをブロードキャストする.
     * @param context コンテキスト
     * @param intent インテント
     */
    public static void sendBroadcast(final Context context, final Intent intent) {
        sendBroadcast(context, intent, DEFAULT_DELAY);
    }

    /**
     * ユーティリティクラスのため、インスタンスを生成させない.
     */
    private Util() {
    }
}
