/*
 DConnectObservationService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.observer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.deviceconnect.android.manager.DConnectService;
import org.deviceconnect.android.manager.DConnectSettings;
import org.deviceconnect.android.observer.activity.WarningDialogActivity;
import org.deviceconnect.android.observer.receiver.ObserverReceiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * dConnectの生存確認を行うサービス.
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectObservationService extends Service {

    /**
     * オブザーバー監視開始アクション.
     */
    public static final String ACTION_START = "org.deviceconnect.android.intent.action.observer.START";
    
    /**
     * オブザーバー監視停止アクション.
     */
    public static final String ACTION_STOP = "org.deviceconnect.android.intent.action.observer.STOP";

    /**
     * 監視アクション.
     */
    public static final String ACTION_CHECK = "org.deviceconnect.android.intent.action.observer.CHECK";

    /**
     * dConnectManagerのサービス名.
     */
    private static final String DCONNECT_SERVICE_NAME = DConnectService.class.getCanonicalName();

    /**
     * リクエストコード.
     */
    private static final int REQUEST_CODE = 0x0F0F0F;

    /**
     * d-Connectのホスト名.
     */
    private String mHost;

    /**
     * d-Connectのポート番号.
     */
    private int mPort;

    /**
     * チェックの間隔.
     */
    private int mInterval;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopObservation();
        DConnectSettings settings = DConnectSettings.getInstance();
        settings.load(DConnectObservationService.this);
        mHost = settings.getHost();
        mPort = settings.getPort();
        mInterval = settings.getObservationInterval();
        // onDestroyが呼ばれずに死ぬこともあるようなので必ず最初に解除処理を入れる。
        stopObservation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopObservation();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        
        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_CHECK.equals(action)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean running = isManagerRunning();
                    boolean holding = isHoldingPort();

                    if (!running && holding) {
                        stopObservation();
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), WarningDialogActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        getApplication().startActivity(i);
                        stopSelf();
                    }
                }
            }).start();
        } else if (ACTION_START.equals(action)) {
            startObservation();
        } else if (ACTION_STOP.equals(action)) {
            stopObservation();
            stopSelf();
        }

        return START_STICKY;
    }

    /**
     * 監視を開始する.
     */
    private synchronized void startObservation() {
        Intent intent = new Intent(this, ObserverReceiver.class);
        intent.setAction(ACTION_CHECK);
        PendingIntent sender = PendingIntent
                .getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + mInterval, mInterval, sender);
    }

    /**
     * 監視を終了する.
     */
    private synchronized void stopObservation() {
        Intent intent = new Intent(this, ObserverReceiver.class);
        intent.setAction(ACTION_CHECK);
        PendingIntent sender = PendingIntent
                .getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    /**
     * dConnectManagerが起動しているかチェックする.
     * 
     * @return 起動している場合true、その他はfalseを返す。
     */
    private boolean isManagerRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo info : services) {
            if (DCONNECT_SERVICE_NAME.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * ポートを占有しているかチェックする.
     * 
     * @return ポートが使用されている場合はtrue、その他はfalseを返す。
     */
    private boolean isHoldingPort() {
        try {
            Socket socket = new Socket(mHost, mPort);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
