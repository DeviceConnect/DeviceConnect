/*
 PebbleManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.BatteryProfileConstants;
import org.deviceconnect.profile.DeviceOrientationProfileConstants;
import org.deviceconnect.profile.SettingsProfileConstants;
import org.deviceconnect.profile.VibrationProfileConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

/**
 * Pebbleとのやり取りを管理するクラス.
 * <p>
 * Pebbleへコマンドを送信するメソッドは、ほとんどがシングルスレッドで動作するように設計されている。<br/>
 * なので、コマンドを並列で処理することはできない。
 * </p>
 * @author NTT DOCOMO, INC.
 */
public final class PebbleManager {

    /** Pebble 側で作成された appinfo.json 内に記述されている UUID. */
    private static final UUID MY_UUID = UUID.fromString("ecfbe3b5-65f4-4532-be4e-3d013058d1f5");

    /** タイムアウト時間を定義. */
    private static final int TIMEOUT = 4 * 1000;
    
    /** profile を示すキー番号. */
    public static final int KEY_PROFILE = 1;
    /** interface を示すキー番号. */
    public static final int KEY_INTERFACE = 2;
    /** attribute を示すキー番号. */
    public static final int KEY_ATTRIBUTE = 3;
    /** action を示すキー番号. */
    public static final int KEY_ACTION = 4;

    ///////// 共通

    /** リザルトコード.{@value}. */
    public static final int KEY_PARAM_RESULT_CODE = 100;
    /** エラーコード.{@value}. */
    public static final int KEY_PARAM_ERROR_CODE = 101;
    /** リクエストコード.{@value}. */
    public static final int KEY_PARAM_REQUEST_CODE = 102;

    ///////// battery
    /** battery charging を表すキー番号. */
    public static final int KEY_PARAM_BATTERY_CHARGING = 200;
    /** battery level を表すキー番号. */
    public static final int KEY_PARAM_BATTERY_LEVEL = 201;

    ///////// binary

    /** バイナリ総受信長を表すキー番号. */
    public static final int KEY_PARAM_BINARY_LENGTH = 300;
    /** バイナリのパケット順を表すキー番号. */
    public static final int KEY_PARAM_BINARY_INDEX = 301;
    /** バイナリのデータ本体を表すキー番号. */
    public static final int KEY_PARAM_BINARY_BODY = 302;

    ///////// device orientation
    /** device orientation の x を表すキー番号. */
    public static final int KEY_PARAM_DEVICE_ORIENTATION_X = 400;
    /** device orientation の y を表すキー番号. */
    public static final int KEY_PARAM_DEVICE_ORIENTATION_Y = 401;
    /** device orientation の z を表すキー番号. */
    public static final int KEY_PARAM_DEVICE_ORIENTATION_Z = 402;
    /** device orientation のインターバルを表すキー番号. */
    public static final int KEY_PARAM_DEVICE_ORIENTATION_INTERVAL = 403;

    ///////// vibration
    /** バイブレーションパターンの長さを表すキー番号.*/
    public static final int KEY_PARAM_VIBRATION_LEN = 500;
    /** バイブレーションパターンを表すキー番号.*/
    public static final int KEY_PARAM_VIBRATION_PATTERN = 501;
    
    ///////// setting
    /** settig date を表すキー番号.*/
    public static final int KEY_PARAM_SETTING_DATE = 600;
    
    /** get action を表す数値. */
    public static final int ACTION_GET = 1;
    /** post action を表す数値. */
    public static final int ACTION_POST = 2;
    /** put action を表す数値. */
    public static final int ACTION_PUT = 3;
    /** delete action を表す数値. */
    public static final int ACTION_DELETE = 4;
    /** event action を表す数値. */
    public static final int ACTION_EVENT = 5;

    /** battery profile を表す数値. */
    public static final int PROFILE_BATTERY = 1;
    /** device orientation を表す数値. */
    public static final int PROFILE_DEVICE_ORIENTATION = 2;
    /** vibration profile を表す数値. */
    public static final int PROFILE_VIBRATION = 3;
    /** setting profile を表す数値. */
    public static final int PROFILE_SETTING = 4;
    /** system profile を表す数値 system/events 用. */
    public static final int PROFILE_SYSTEM = 5;
    /** binary転送 profile を表す数値. */
    public static final int PROFILE_BINARY = 255;

    /** battery の全状態を表す数値. */
    public static final int BATTERY_ATTRIBUTE_ALL = 1;
    /** battery の充電状態を表す数値. */
    public static final int BATTERY_ATTRIBUTE_CHARING = 2;
    /** battery の充電容量を表す数値. */
    public static final int BATTERY_ATTRIBUTE_LEVEL = 3;
    /** battery の充電容量の変化イベントを表す数値. */
    public static final int BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE = 4;
    /** battery の受電状態の変化イベントを表す数値. */
    public static final int BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE = 5;

    /** device orientation を表す数値. */
    public static final int DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION = 1;

    /** vibration attribute を表す数値. */
    public static final int VIBRATION_ATTRIBUTE_VIBRATE = 1;

    /** setting attribute volume を表す数値. */
    public static final int SETTING_ATTRIBUTE_VOLUME = 1;
    /** setting attribute date を表す数値. */
    public static final int SETTING_ATTRIBUTE_DATE = 2;

    /** system attribute events を表す数値. */
    public static final int SYSTEM_ATTRIBUTE_EVENTS = 1;
    
    /** pebble の横ドット数. */
    public static final int PEBBLE_SCREEN_WIDTH = 144;
    /** pebble の縦ドット数. */
    public static final int PEBBLE_SCREEN_HEIGHT = 168;

    /** 充電中を表す数値. */
    public static final int BATTERY_CHARGING_ON = 1;
    /** 充電中でないことを表す数値. */
    public static final int BATTERY_CHARGING_OFF = 2;

    /** トランザクション(通信時)の数値. */
    private static final int TRANSACATION_ID = 255;

    /**
     * このクラスが属するコンテキスト.
     */
    private Context mContext;

    /**
     * キューイング用スレッド.
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    /**
     * イベント用のリスナー管理マップ.
     */
    private final Map<Integer, OnReceivedEventListener> mEvtListeners 
    = new ConcurrentHashMap<Integer, OnReceivedEventListener>();

    /**
     * 接続状態通知リスナー一覧.
     */
    private final List<OnConnectionStatusListener> mConnectStatusListeners 
    = new ArrayList<OnConnectionStatusListener>();

    /**
     * ロックオブジェクト.
     */
    private final Object mLockObj = new Object();

    /**
     * Pebbleからの処理結果.
     */
    private final Map<Integer, PebbleDictionary> mResponseDataMap = new ConcurrentHashMap<Integer, PebbleDictionary>();

    /** PebbleDictionary. */
    private PebbleDictionary mRequestDictionary;

    /**
     * バイナリ送信状態.
     */
    private enum BinarySendState {
        /** 送信中. */
        STATE_NONE,
        /** 送信成功. */
        STATE_ACK,
        /** 送信失敗. */
        STATE_NACK
    };

    /** バイナリ送信状態を保持. */
    private BinarySendState mBinarySendState;

    /** バイナリ送信をブロックする. */
    private final Object mBinaryLockObj = new Object();

    /**
     * Pebbleからのイベントを受け取るためのハンドラ.
     */
    private PebbleKit.PebbleDataReceiver mHandler = new PebbleKit.PebbleDataReceiver(MY_UUID) {
        @Override
        public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
            // Pebble に対して、可能な限り素早くACKを返す
            PebbleKit.sendAckToPebble(getContext(), transactionId);

            // アクションを取得する
            Long action = data.getInteger(KEY_ACTION);
            if (action != null && action.intValue() == ACTION_EVENT) {
                // イベント処理の場合は、各リスナーに通知を行う
                Long profile = data.getInteger(KEY_PROFILE);
                if (profile != null) {
                    OnReceivedEventListener l = mEvtListeners.get(profile.intValue());
                    if (l != null) {
                        l.onReceivedEvent(data);
                    }
                }
            } else {
                // リクエストコードを取得
                Long requestCode = data.getInteger(KEY_PARAM_REQUEST_CODE);
                if (requestCode != null) {
                    // 結果をマップに保持しておく
                    mResponseDataMap.put(requestCode.intValue(), data);
                    synchronized (mLockObj) {
                        mLockObj.notifyAll();
                    }
                }
            }
        }
    };

    /**
     * PebbleからのACKイベントを受け取るためのハンドラ.
     */
    private PebbleKit.PebbleAckReceiver mAckHandler = new PebbleKit.PebbleAckReceiver(MY_UUID) {
        @Override
        public void receiveAck(final Context context, final int transactionId) {
            if (transactionId == TRANSACATION_ID && mRequestDictionary != null) {
                mRequestDictionary = null;
            } else {
                mBinarySendState = BinarySendState.STATE_ACK;
                synchronized (mBinaryLockObj) {
                    mBinaryLockObj.notifyAll();
                }
            }
        }
    };

    /**
     * PebbleからのNACKイベントを受け取るためのハンドラ.
     */
    private PebbleKit.PebbleNackReceiver mNackHandler = new PebbleKit.PebbleNackReceiver(MY_UUID) {
        @Override
        public void receiveNack(final Context context, final int transactionId) {
            final PebbleDictionary dic = mRequestDictionary;
            if (transactionId == TRANSACATION_ID && mRequestDictionary != null) {
                // Pebbleアプリを起動要求を行った後に1回だけリトライする
                sendStartAppOnPebble(new Runnable() {
                    @Override
                    public void run() {
                        PebbleKit.sendDataToPebble(getContext(), MY_UUID, dic);
                    }
                });
                mRequestDictionary = null;
            } else {
                mBinarySendState = BinarySendState.STATE_NACK;
                synchronized (mBinaryLockObj) {
                    mBinaryLockObj.notifyAll();
                }
            }
        }
    };

    /**
     * Pebbleからの接続イベントを受け取るためのハンドラ.
     */
    private BroadcastReceiver mConnectHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            for (OnConnectionStatusListener l : mConnectStatusListeners) {
                l.onConnect();
            }
        }
    };

    /**
     * Pebbleからの切断イベントを受け取るためのハンドラ.
     */
    private BroadcastReceiver mDisconnectHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            for (OnConnectionStatusListener l : mConnectStatusListeners) {
                l.onDisconnect();
            }
        }
    };

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     */
    public PebbleManager(final Context context) {
        mContext = context;
        init();
    }

    /**
     * コンテキストを取得する.
     * @return コンテキスト
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 初期化を行う.
     */
    private void init() {
        PebbleKit.registerReceivedDataHandler(getContext(), mHandler);
        PebbleKit.registerPebbleConnectedReceiver(getContext(), mConnectHandler);
        PebbleKit.registerPebbleDisconnectedReceiver(getContext(), mDisconnectHandler);
        PebbleKit.registerReceivedAckHandler(getContext(), mAckHandler);
        PebbleKit.registerReceivedNackHandler(getContext(), mNackHandler);
    }

    /**
     * Pebble側のアプリを終了する.
     */
    public void destory() {
        getContext().unregisterReceiver(mHandler);
        getContext().unregisterReceiver(mConnectHandler);
        getContext().unregisterReceiver(mDisconnectHandler);
        getContext().unregisterReceiver(mAckHandler);
        getContext().unregisterReceiver(mNackHandler);
        PebbleKit.closeAppOnPebble(getContext(), MY_UUID);
        mExecutor.shutdown();
    }

    /**
     * Pebbleにアプリ終了イベントを送信する.
     */
    public void sendCloseAppOnPebble() {
        PebbleKit.closeAppOnPebble(getContext(), MY_UUID);
    }

    /**
     * イベント受信用のリスナーを設定する.
     * <p>
     * 指定されたプロファイルのイベントがリスナーに通知される。<br/>
     * 設定されていないプロファイルの通知は無視される。
     * </p>
     * @param profile プロファイル
     * @param listener リスナー
     */
    public void addEventListener(final int profile, final OnReceivedEventListener listener) {
        mEvtListeners.put(profile, listener);
    }

    /**
     * イベント受信用のリスナーを削除する.
     * @param profile 削除するリスナーのプロファイル
     */
    public void removeEventListener(final int profile) {
        mEvtListeners.remove(profile);
    }

    /**
     * 接続状態通知リスナーを追加する.
     * 
     * @param listener リスナー
     */
    public void addConnectStatusListener(final OnConnectionStatusListener listener) {
        mConnectStatusListeners.add(listener);
    }

    /**
     * 接続状態通知リスナーを削除する.
     * 
     * @param listener リスナー
     */
    public void removeConnectStatusListener(final OnConnectionStatusListener listener) {
        mConnectStatusListeners.remove(listener);
    }

    /**
     * Pebble側で対応するアプリを起動させる.
     */
    public void sendStartAppOnPebble() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                PebbleKit.startAppOnPebble(getContext(), MY_UUID);
            }
        });
    }

    /**
     * Pebbleアプリに起動依頼を送信後、数秒した後にrunを実行する.
     * <p>
     * この関数は、別スレッドで動作するので、他の関数とは違う挙動をする。<br/>
     * なので、この関数を使用する場合には、考慮する必要が有る。
     * </p>
     * @param run 起動後に実行する処理
     */
    private void sendStartAppOnPebble(final Runnable run) {
        final int sleepMilliSec = 2000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                PebbleKit.startAppOnPebble(getContext(), MY_UUID);
                try {
                    Thread.sleep(sleepMilliSec);
                    run.run();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
    }

    /**
     * Pebbleにコマンドを送信する.
     * <p>
     * この関数は非同期で動作する。<br/>
     * Pebbleからの返答はlistenerに返却される。
     * </p>
     * @param dic 送信するデータ
     * @param listener リスナー
     */
    public void sendCommandToPebble(final PebbleDictionary dic, final OnSendCommandListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final int retryCountMax = 3;
                PebbleDictionary result  = null;
                // リクエストコードを割り振る
                final int requestCode = UUID.randomUUID().hashCode();
                dic.addInt32(KEY_PARAM_REQUEST_CODE, requestCode);
                
                // リクエストをキャッシュしておく
                mRequestDictionary = dic;

                for (int retryCount = 0; retryCount < retryCountMax;  retryCount++) {
                    // Pebbleにデータを送信
                    PebbleKit.sendDataToPebbleWithTransactionId(getContext(), MY_UUID, dic, TRANSACATION_ID);
                    try {
                        synchronized (mLockObj) {
                            mLockObj.wait(TIMEOUT);
                        }
                    } catch (InterruptedException e) {
                        // do nothing.
                    }
                    // Pebbleへの送信結果
                    result = mResponseDataMap.remove(requestCode);
                    if (result != null) {
                        if (mBinarySendState == BinarySendState.STATE_ACK) {
                            break;
                        }
                    }
                }
                if (listener != null) {
                    listener.onReceivedData(result);
                }
            }
        });
    }

    /**
     * 指定されたデータをPebbleに送信する.
     * @param data 送信するデータ.
     * @param listener 送信確認用のリスナー.
     */
    public void sendDataToPebble(final byte[] data, final OnSendDataListener listener) {
        mExecutor.execute(new Runnable() {
            /**
             * 分割するデータサイズを定義.
             * 分割サイズは、Pebbleアプリ側でも定義してあるので
             * 大きくする場合には、Pebbleアプリ側の定義も修正すること。
             */
            private static final int BUF_SIZE = 64;
            @Override
            public void run() {
                final int sleepMilliSec = 100;
                final int streamRemainLegnth = data.length;
                final int count = streamRemainLegnth / BUF_SIZE + 1;
                // 最初にデータのサイズを送る
                if (!sendLength(streamRemainLegnth)) {
                    if (listener != null) {
                        listener.onSend(false);
                    }
                    return;
                }
                // Pebbleにデータ送信
                for (int i = 0; i < count; i++) {
                    mBinarySendState = BinarySendState.STATE_NONE;
                    if (!sendBody(data, i)) {
                        if (listener != null) {
                            listener.onSend(false);
                        }
                        return;
                    }
                    try {
                        Thread.sleep(sleepMilliSec);
                    } catch (InterruptedException e) {
                    }
                }
                if (listener != null) {
                    listener.onSend(true);
                }
            }
            /**
             * Pebbleからのレスポンスを待つ.
             * 
             * @return ACKの場合はtrue、NACKの場合はfalseを返却する
             */
            private boolean waitAck() {
                try {
                    synchronized (mBinaryLockObj) {
                        mBinaryLockObj.wait(TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    // do nothing.
                }
                return (mBinarySendState == BinarySendState.STATE_ACK);
            }
            /**
             * データのサイズをPebbleに通知する.
             * 
             * @param streamTotalLength データサイズ
             * @return 通知に成功した場合はtrue、それ以外はfalse
             */
            private boolean sendLength(final int streamTotalLength) {
                final int retryMax = 3;
                final int sleepMilliSec = 2000;
                PebbleDictionary data = new PebbleDictionary();
                data.addInt8(KEY_PROFILE, (byte) PROFILE_BINARY);
                data.addInt32(KEY_PARAM_BINARY_LENGTH, streamTotalLength);
                for (int retry = 0; retry < retryMax; retry++) {
                    PebbleKit.sendDataToPebble(mContext, MY_UUID, data);
                    if (waitAck()) {
                        return true;
                    } else {
                        // nackが返ってきたみたいなので、アプリ起動してから
                        // リトライを試みる。
                        PebbleKit.startAppOnPebble(getContext(), MY_UUID);
                        try {
                            Thread.sleep(sleepMilliSec);
                        } catch (InterruptedException e) {
                            // do nothing.
                        }
                    }
                }
                return false;
            }
            /**
             * データの中身を分割して送信する.
             * 
             * @param stream 送信するデータ
             * @param index 分割したデータのインデックス
             * @return 通知に成功した場合はtrue、それ以外はfalse
             */
            public boolean sendBody(final byte[] stream, final int index) {
                final int retryMax = 3;
                final int sleepMilliSec = 100;
                byte[] send = Arrays.copyOfRange(stream, index * BUF_SIZE, (index + 1) * BUF_SIZE);
                PebbleDictionary data = new PebbleDictionary();
                data.addInt8(KEY_PROFILE, (byte) PROFILE_BINARY);
                data.addInt16(KEY_PARAM_BINARY_INDEX, (byte) index);
                data.addBytes(KEY_PARAM_BINARY_BODY, send);
                for (int retry = 0; retry < retryMax; retry++) {
                    PebbleKit.sendDataToPebble(mContext, MY_UUID, data);
                    if (waitAck()) {
                        return true;
                    } else {
                        try {
                            Thread.sleep(sleepMilliSec);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * PebbleにNotificationを送信する.
     * <p>
     * senderには、アプリ名を渡す。
     * </p>
     * @param title タイトル
     * @param body メッセージボディ
     */
    public void sendNotificationToPebble(final String title, final String body) {
        ApplicationInfo info = getContext().getApplicationInfo();
        String sender = getContext().getPackageManager().getApplicationLabel(info).toString();
        sendNotificationToPebble(title, body, sender);
    }

    /**
     * PebbleにNotificationを送信する.
     * <p>
     * titleやbodyに日本語が入った場合には文字化けが発生する。
     * </p>
     * @param title タイトル
     * @param body メッセージボディ
     * @param sender 送信者名
     */
    public void sendNotificationToPebble(final String title, final String body, final String sender) {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("title", title);
        data.put("body", body);
        data.put("flags", "" + Notification.FLAG_AUTO_CANCEL);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", sender);
        i.putExtra("notificationData", notificationData);

        getContext().sendBroadcast(i);
    }

    /**
     * リクエストからPebbleDictionaryを作成する.
     * <p>
     * PebbleDictionaryの作成に失敗した場合はnullを返却する。
     * </p>
     * 
     * TODO 実装が中途半端なので、実用に耐えられない。
     * 
     * @param request リクエスト
     * @return PebbleDictionaryのインスタンス
     */
    public PebbleDictionary createPebbleDictionary(final Intent request) {
        PebbleDictionary dic = new PebbleDictionary();
        byte profile = convertProfile(request.getStringExtra(DConnectMessage.EXTRA_PROFILE));
        dic.addInt8(KEY_ACTION, convertAction(request.getAction()));
        dic.addInt8(KEY_PROFILE, profile);
        dic.addInt8(KEY_INTERFACE, convertInterface(profile, request.getStringExtra(DConnectMessage.EXTRA_INTERFACE)));
        dic.addInt8(KEY_ATTRIBUTE, convertAttribute(profile, request.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE)));
        return dic;
    }

    /**
     * ActionをPebble用のアクションに変換する.
     * @param action Dconnectのアクション
     * @return Pebble用のアクション
     */
    private byte convertAction(final String action) {
        if (DConnectMessage.METHOD_GET.equals(action)) {
            return ACTION_GET;
        } else if (DConnectMessage.METHOD_POST.equals(action)) {
            return ACTION_POST;
        } else if (DConnectMessage.METHOD_PUT.equals(action)) {
            return ACTION_PUT;
        } else if (DConnectMessage.METHOD_DELETE.equals(action)) {
            return ACTION_DELETE;
        }
        return -1;
    }

    /**
     * プロファイルをPebble用のデータに変換する.
     * @param profile プロファイル名
     * @return Pebble用のプロファイル
     */
    private byte convertProfile(final String profile) {
        if (BatteryProfileConstants.PROFILE_NAME.equals(profile)) {
            return PROFILE_BATTERY;
        } else if (DeviceOrientationProfileConstants.PROFILE_NAME.equals(profile)) {
            return PROFILE_DEVICE_ORIENTATION;
        } else if (VibrationProfileConstants.PROFILE_NAME.equals(profile)) {
            return PROFILE_VIBRATION;
        } else if (SettingsProfileConstants.PROFILE_NAME.equals(profile)) {
            return PROFILE_SETTING;
        }
        return -1;
    }

    /**
     * インターフェースをPebble用のデータに変換する.
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @return Pebble用のインターフェース
     */
    private byte convertInterface(final int profile, final String inter) {
        return -1;
    }

    /**
     * アトリビュートをPebble用のデータに変換する.
     * @param profile プロファイル名
     * @param attribute アトリビュート名
     * @return Pebble用のアトリビュート
     */
    private byte convertAttribute(final int profile, final String attribute) {
        switch (profile) {
        case PROFILE_BATTERY:
            return convertBatteryAttribute(attribute);
        case PROFILE_DEVICE_ORIENTATION:
            return convertDeviceOrientationAttribute(attribute);
        }
        return -1;
    }

    /**
     * バッテリーのアトリビュートをPebble用に変換する.
     * @param attribute アトリビュート
     * @return Pebble用のアトリビュート
     */
    private byte convertBatteryAttribute(final String attribute) {
        if (BatteryProfileConstants.ATTRIBUTE_CHARGING.equals(attribute)) {
            return BATTERY_ATTRIBUTE_CHARING;
        } else if (BatteryProfileConstants.ATTRIBUTE_LEVEL.equals(attribute)) {
            return BATTERY_ATTRIBUTE_LEVEL;
        } else if (BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE.equals(attribute)) {
            return BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE;
        } else if (BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE .equals(attribute)) {
            return BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE;
        }
        return -1;
    }

    /**
     * DeviceOrientationのアトリビュートをPebble用に変換する.
     * @param attribute アトリビュート
     * @return Pebble用のアトリビュート
     */
    private byte convertDeviceOrientationAttribute(final String attribute) {
        if (DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION.equals(attribute)) {
            return DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION;
        }
        return -1;
    }

    /**
     * 指定された整数の下位2byteをbyte配列に変換する.
     * <p>
     * 上位2byteは無視する。<br/>
     * <br/>
     * PebbleのVibrationの最大振動時間は10000msなので、
     * shortの範囲があれば十分。
     * </p>
     * @param value 変換する整数
     * @return byte[]
     */
    private static byte[] convertIntToByte(final int value) {
        final int shiftValue = 8;
        final int mask = 0xff;
        byte[] buf = new byte[2];
        buf[0] = (byte) ((value >>  shiftValue) & mask);
        buf[1] = (byte) ((value & mask));
        return buf;
    }

    /**
     * Vibrationのパターンをpebbleで使用できるように変換する.
     * <p>
     * パターンは、100,100,100,100で、ON,OFF,ON,OFFの時間が入っている。
     * </p>
     * @param pattern パターン
     * @return Pebble用のバイブレーションパターン
     */
    public static byte[] convertVibrationPattern(final String pattern) {
        if (pattern == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] p = pattern.split(",");
        for (int i = 0; i < p.length; i++) {
            int value = Integer.parseInt(p[i]);
            byte[] v = convertIntToByte(value);
            try {
                baos.write(v);
            } catch (IOException e) {
                break;
            }
        }
        return baos.toByteArray();
    }

    /**
     * バイブレーションのパターンをPebble用に変換する.
     * 
     * @param pattern パターンデータ
     * @return Pebble用のバイブレーションパターン
     */
    public static byte[] convertVibrationPattern(final long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < pattern.length; i++) {
            int value = (int) pattern[i];
            byte[] v = convertIntToByte(value);
            try {
                baos.write(v);
            } catch (IOException e) {
                break;
            }
        }
        return baos.toByteArray();
    }
    
    /**
     * Pebbleで読み込めるような画像に変換する.
     * 
     * @param data 画像データ
     * @return 変換後のデータ
     */
    public static byte[] convertImage(byte[] data) {
        final int width = 144;
        final int height = 120;
        return convertImage(data, width, height);
    }

    /**
     * Pebbleで読み込めるような画像に変換する.
     * 
     * 指定されたサイズの枠収まるように変換する。
     * 
     * @param data 画像データ
     * @param width 横幅
     * @param height 縦幅
     * @return 変換後のデータ
     */
    public static byte[] convertImage(byte[] data, final int width, final int height) {
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap b2 = PebbleBitmapUtil.scale(b, width, height);
        byte[] buf = PebbleBitmapUtil.convertImageThresholding(b2);
        b.recycle();
        b2.recycle();
        return buf;
    }

    /**
     * コマンドを送信した結果を受信するためのリスナー.
     */
    public interface OnSendCommandListener {
        /**
         * コマンドの実行結果を取得する.
         * <p>
         * Pebbleに送信失敗した場合などは、nullが返却される。
         * </p>
         * @param dic 実行結果
         */
        void onReceivedData(PebbleDictionary dic);
    }

    /**
     * イベントを受信するためのリスナー.
     */
    public interface OnReceivedEventListener {
        /**
         * イベントを受信した.
         * @param dic 受信したイベント
         */
        void onReceivedEvent(PebbleDictionary dic);
    }
    /**
     * バイナリ送信を行った結果を受信するためのリスナー.
     */
    public interface OnSendDataListener {
        /**
         * バイナリの送信結果を取得する.
         * @param successed 送信に成功した場合はtrue、それ以外はfalse
         */
        void onSend(boolean successed);
    }
    
    /**
     * Pebble接続通知リスナー.
     */
    public interface OnConnectionStatusListener {
        /**
         * 接続された.
         */
        void onConnect();
        /**
         * 切断された.
         */
        void onDisconnect();
    }
}
