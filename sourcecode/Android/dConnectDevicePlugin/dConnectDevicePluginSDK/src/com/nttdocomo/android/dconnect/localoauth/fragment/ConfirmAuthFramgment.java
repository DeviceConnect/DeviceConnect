/*
 ConfirmAuthFramgment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth.fragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.restlet.ext.oauth.internal.AbstractTokenManager;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.R;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Service;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Settings;
import com.nttdocomo.android.dconnect.localoauth.ScopeUtil;
import com.nttdocomo.android.dconnect.localoauth.activity.ConfirmAuthActivity;

/**
 * 認可ダイアログ.
 * @author NTT DOCOMO, INC.
 */
public class ConfirmAuthFramgment extends DialogFragment {

    /** 1分の秒数. */
    private static final int MINUTE = 60;

    /** 1時間の秒数. */
    private static final int HOUR = 60 * MINUTE;

    /** 1日の秒数. */
    private static final int DAY = 24 * HOUR;

    /** 呼び出し元のスレッドID. */
    private long mThreadId = 0;

    /** Messageを使ってBindされたServiceに送るメッセージID(承認／拒否通知). */
    private int mApprovalMessageId;
    
    /** Messageを使ってBindされたServiceに送るメッセージID(ThreadId有効確認要求通知). */
    private int mCheckThreadIdMessageId;

    /** Messenger for communicating with the service. */
    private Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    private boolean mBound;

    /** ダイアログのインスタンス. */
    private Dialog mDialog;
    
    /** 画面表示後自動クリック処理を実行するまでの時間[msec]. */
    private static final long AUTO_CLICK_WAIT_TIME = 3000;
    
    /** 自動クリックタイマー(テスト用). */
    private Timer mAutoClickTimer = null;
    
    /** ハンドラ. */
    private Handler mHandler = new Handler();
    
    /** 受信用メッセンジャー設定. */
    private Messenger mSelfMessenger;

    /** フラグメントイベントリスナー. */
    private ConfirmAuthFragmentListener mListener = null;
    
    
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.setContentView(R.layout.confirm_auth_activity);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        /* Intentから値取得 */
        Intent intent = getActivity().getIntent();
        mThreadId = intent.getLongExtra(ConfirmAuthActivity.EXTRA_THREADID, -1);
        String applicationName = intent.getStringExtra(ConfirmAuthActivity.EXTRA_APPLICATIONNAME);
        String[] displayScopes = intent.getStringArrayExtra(ConfirmAuthActivity.EXTRA_DISPLAY_SCOPES);
        mApprovalMessageId = intent.getIntExtra(ConfirmAuthActivity.EXTRA_APPROVAL_MESSAGEID, -1);
        mCheckThreadIdMessageId = intent.getIntExtra(ConfirmAuthActivity.EXTRA_CHECK_THREADID_MESSAGEID, -1);

        String expirePeriodFormat = getString(R.string.expire_period_format);
        String expirePeriodValue = toExpirePeriodDateString(AbstractTokenManager.DEFAULT_TOKEN_EXPIRE_PERIOD);
        String expirePeriod = String.format(expirePeriodFormat, expirePeriodValue);

        // 有効期限
        TextView textViewExpirePeriod = (TextView) mDialog.findViewById(R.id.textViewExpirePeriod);
        textViewExpirePeriod.setText(expirePeriod);

        // アプリ名
        TextView textViewApplicationName = (TextView) mDialog.findViewById(R.id.textViewAccessToken);
        textViewApplicationName.setText(applicationName);

        /* スコープ一覧表示 */
        ListView listViewScopes = (ListView) mDialog.findViewById(R.id.listViewScopes);
        listViewScopes.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.scopes_list_item, R.id.textViewScope,
                displayScopes));
        
        /* 承認・拒否ボタン */
        Button buttonApproval = (Button) mDialog.findViewById(R.id.buttonApproval);
        buttonApproval.setOnClickListener(mOnButtonApprovalClickListener);
        
        /* Activityの外側(拒否ボタン押下と同等の動作をする) */
        LinearLayout linearLayoutFrame = (LinearLayout) mDialog.findViewById(R.id.linearLayoutFrame);
        linearLayoutFrame.setOnClickListener(mOnButtonNotApprovalClickListener);

        /* ハンドラ */
        mHandler = new Handler();

        /* 受信用メッセンジャー設定 */
        mSelfMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                if (msg.what == LocalOAuth2Main.MESSAGE_PUBLISH_ACCESSTOKEN_SUCCESS) {
                    /* アクセストークン発行成功したらActivityを閉じる */
                    mListener.doPositiveClick();
                } else if (msg.what == LocalOAuth2Main.MESSAGE_PUBLISH_ACCESSTOKEN_EXCEPTION) {
                    /* アクセストークン発行中にエラーが発生したらActivityを閉じる */
                    mListener.doNegativeClick();
                } else if (msg.what == LocalOAuth2Main.MESSAGE_CHECK_THREADID_RESULT) {
                    /* threadIdはキューに存在しないのでActivityを閉じる(残ってしまったアプリ履歴からの起動の場合を想定) */
                    if (msg.arg1 == 0) {
                        /* アクセストークン発行中にエラーが発生したらActivityを閉じる */
                        mListener.doNegativeClick();
                    }
                } 
            }
        });
        
        /* 自動テストモードの場合は、タイマーを開始する */
        if (LocalOAuth2Main.isAutoTestMode()) {
            if (mAutoClickTimer == null) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                approvalProc();
                            }
                        });
                    }
                };
                mAutoClickTimer = new Timer(true);
                mAutoClickTimer.schedule(timerTask, AUTO_CLICK_WAIT_TIME);
            }            
        }
        
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        /* ServiceConnectionを渡してServiceとBindする */
        Intent intent = new Intent();
        intent.setClass(getActivity(), LocalOAuth2Service.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        
        cancelAutoClickTimer();
        
        super.onStop();
        getActivity().unbindService(mConnection);
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);
        notApprovalProc();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        
        /*
         * Activityが縦横回転するときはActivityを作り直すようで本メソッドが呼び出されgetActivity()がnullを返す。
         * ダイアログを終了するときはgetActivity()が有効値を返す。
         * ダイアログを終了時のみfinish()を実行するようにした。
         * (参考)http://kabagaeru.blogspot.jp/2014/02/android-dialogfragment.html
         */
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /** Class for interacting with the main interface of the service. */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            
            /* Bindが接続されたらthreadId有効チェック */
            sendMessageCheckThreadId(mThreadId);
        }
        @Override
        public void onServiceDisconnected(final ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    /**
     * Message通知処理.
     * 
     * @param isApproval true: 許可 / false: 拒否
     */
    public void sendMessage(final Boolean isApproval) {
        if (!mBound) {
            return;
        }

        /* Messageを使って許可・拒否のステータスをServiceに送る */
        int iIsApproval = 0;
        if (isApproval) {
            iIsApproval = 1;
        }
        Message msg = Message.obtain(null, mApprovalMessageId, (int) mThreadId, iIsApproval);
        msg.replyTo = mSelfMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * threadId有効チェックメッセージ送信.
     * @param threadId 判定するスレッドID
     */
    private void sendMessageCheckThreadId(final long threadId) {
        Message msg = Message.obtain(null, mCheckThreadIdMessageId, (int) mThreadId, 0);
        msg.replyTo = mSelfMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 許可ボタンをタップしたときの処理.
     */
    private OnClickListener mOnButtonApprovalClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            
            /* 自動クリックタイマーが起動中なら停止する */
            cancelAutoClickTimer();
            
            approvalProc();
        }
    };

    /**
     * 拒否ボタンをタップしたときの処理.
     */
    private OnClickListener mOnButtonNotApprovalClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            notApprovalProc();
        }
    };

    /**
     * 承認ボタンを押下されたときの処理.
     */
    private void approvalProc() {
        /* Bindを通じてServiceにメッセージを送る */
        sendMessage(true);
        /* ※Activityを閉じる処理は、mSelfMessenger.handleMessage()内で行う。 */
    }
    
    /**
     * 拒否ボタンを押下されたときの処理.
     */
    private void notApprovalProc() {
        /* Bindを通じてServiceにメッセージを送る */
        sendMessage(false);
        /* Activityを閉じる */
        mListener.doNegativeClick();
    }
    /**
     * 有効期限日を文字列で返す.
     * @param expirePeriodSec 有効期限[秒]
     * @return 有効期限日を文字列
     */
    private String toExpirePeriodDateString(final long expirePeriodSec) {
        Calendar now = Calendar.getInstance();
        long expirePeriodDateMSec = now.getTimeInMillis() + expirePeriodSec * LocalOAuth2Settings.MSEC;
        String s = ScopeUtil.getDisplayExpirePeriodDate(expirePeriodDateMSec);
        return s;
    }
    
    /**
     * 自動クリックタイマーを停止する.
     */
    private void cancelAutoClickTimer() {
        if (mAutoClickTimer != null) {
            mAutoClickTimer.cancel();
            mAutoClickTimer = null;
        }
    }
    
    /**
     * フラグメントイベントリスナーを追加.
     * @param listener リスナー
     */
    public void setDialogListener(final ConfirmAuthFragmentListener listener) {
        this.mListener = listener;
    }
}
