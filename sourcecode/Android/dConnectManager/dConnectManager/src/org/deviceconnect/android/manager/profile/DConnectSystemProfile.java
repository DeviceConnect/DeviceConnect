/*
 DConnectSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.manager.DConnectMessageService;
import org.deviceconnect.android.manager.DConnectService;
import org.deviceconnect.android.manager.DevicePlugin;
import org.deviceconnect.android.manager.DevicePluginManager;
import org.deviceconnect.android.manager.request.DConnectRequest;
import org.deviceconnect.android.manager.request.RemoveEventsRequest;
import org.deviceconnect.android.manager.setting.KeywordDialogAcitivty;
import org.deviceconnect.android.manager.setting.SettingActivity;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.SystemProfileConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * System プロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectSystemProfile extends SystemProfile {
    /** プロファイル管理クラス. */
    private DConnectProfileProvider mProvider;

    /** プラグイン管理クラス. */
    private DevicePluginManager mPluginMgr;

    /**
     * コンストラクタ.
     * 
     * @param provider プロファイル管理クラス
     * @param pluginMgr プラグイン管理クラス
     */
    public DConnectSystemProfile(final DConnectProfileProvider provider, final DevicePluginManager pluginMgr) {
        super(provider);
        mProvider = provider;
        mPluginMgr = pluginMgr;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attri = getAttribute(request);
        if (inter == null && attri == null) {
            return onGetSystemRequest(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_KEYWORD.equals(attri)) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_EVENTS.equals(attri)) {
            return sendNotSupportActionError(request, response);
        }
        // 各デバイスプラグインに渡すのfalse
        return false;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attri = getAttribute(request);
        if (inter == null && attri == null) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_KEYWORD.equals(attri)) {
            return onPutKeywordRequest(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_EVENTS.equals(attri)) {
            return sendNotSupportActionError(request, response);
        }
        // 各デバイスプラグインに渡すのfalse
        return false;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attri = getAttribute(request);
        if (inter == null && attri == null) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_KEYWORD.equals(attri)) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_EVENTS.equals(attri)) {
            return onDeleteEvents(request, response);
        }
        // 各デバイスプラグインに渡すのfalse
        return false;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attri = getAttribute(request);
        if (inter == null && attri == null) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_KEYWORD.equals(attri)) {
            return sendNotSupportActionError(request, response);
        } else if (SystemProfileConstants.ATTRIBUTE_EVENTS.equals(attri)) {
            return sendNotSupportActionError(request, response);
        }
        // 各デバイスプラグインに渡すのfalse
        return false;
    }

    /**
     * Not Support action Errorを返却する.
     * @param request リクエスト
     * @param response レスポンス
     * @return イベントを止める場合はtrue、配送する場合はfalseを返却
     */
    private boolean sendNotSupportActionError(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    /**
     * dConnectManagerのシステムプロファイルを取得する.
     * @param request リクエスト
     * @param response レスポンス
     * @return dConnectManagerでリクエストを止める場合はtrue、それ以外はfalse
     */
    private boolean onGetSystemRequest(final Intent request, final Intent response) {
        setResult(response, DConnectMessage.RESULT_OK);
        setVersion(response, getCurrentVersionName());

        // サポートしているプロファイル一覧設定
        List<String> supports = new ArrayList<String>();
        List<DConnectProfile> profiles = mProvider.getProfileList();
        for (int i = 0; i < profiles.size(); i++) {
            supports.add(profiles.get(i).getProfileName());
        }
        setSupports(response, supports.toArray(new String[supports.size()]));

        // プラグインの一覧を設定
        List<Bundle> plugins = new ArrayList<Bundle>();
        List<DevicePlugin> p = mPluginMgr.getDevicePlugins();
        for (int i = 0; i < p.size(); i++) {
            DevicePlugin plugin = p.get(i);
            String deviceId = mPluginMgr.appendDeviceId(plugin, null);
            Bundle b = new Bundle();
            b.putString(PARAM_ID, deviceId);
            b.putString(PARAM_NAME, plugin.getDeviceName());
            plugins.add(b);
        }
        response.putExtra(PARAM_PLUGINS, plugins.toArray(new Bundle[plugins.size()]));

        // レスポンスを返却
        ((DConnectService) getContext()).sendResponse(request, response);

        // 各デバイスプラグインに送信する場合にはfalseを返却、
        // dConnectManagerで止める場合にはtrueを返却する
        // ここでは、各デバイスには渡さないのでtrueを返却する。
        return true;
    }

    /**
     * キーワード表示用のリクエスト解析を行います.
     * @param request リクエスト
     * @param response レスポンス
     * @return dConnectManagerでリクエストを止める場合はtrue、それ以外はfalse
     */
    private boolean onPutKeywordRequest(final Intent request, final Intent response) {
        DConnectRequest req = new DConnectRequest() {
            /** ロックオブジェクト. */
            protected final Object mLockObj = new Object();
            /** リクエストコード. */
            protected int mRequestCode;

            @Override
            public void setResponse(final Intent response) {
                super.setResponse(response);
                synchronized (mLockObj) {
                    mLockObj.notifyAll();
                }
            }

            @Override
            public boolean hasRequestCode(final int requestCode) {
                return mRequestCode == requestCode;
            }

            @Override
            public void run() {
                // リクエストコードを作成する
                mRequestCode = UUID.randomUUID().hashCode();

                // キーワード表示用のダイアログを表示
                Intent intent = new Intent(getContext(), KeywordDialogAcitivty.class);
                intent.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, mRequestCode);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

                // ダイアログからの返答を待つ
                if (mResponse == null) {
                    waitForResponse();
                }

                // レスポンスを返却
                setResult(response, DConnectMessage.RESULT_OK);
                ((DConnectService) getContext()).sendResponse(request, response);
            }

            /**
             * 各デバイスからのレスポンスを待つ.
             * 
             * この関数から返答があるのは以下の条件になる。
             * <ul>
             * <li>デバイスプラグインからレスポンスがあった場合
             * <li>指定された時間無いにレスポンスが返ってこない場合
             * </ul>
             */
            protected void waitForResponse() {
                synchronized (mLockObj) {
                    try {
                        mLockObj.wait(mTimeout);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

        };
        req.setContext(getContext());
        req.setRequest(request);
        ((DConnectMessageService) getContext()).addRequest(req);

        // 各デバイスプラグインに送信する場合にはfalseを返却、
        // dConnectManagerで止める場合にはtrueを返却する
        // ここでは、各デバイスには渡さないのでtrueを返却する。
        return true;
    }

    /**
     * 指定されたセッションキーのイベントを削除する.
     * @param request リクエスト
     * @param response レスポンス
     * @return dConnectManagerでリクエストを止める場合はtrue、それ以外はfalse
     */
    private boolean onDeleteEvents(final Intent request, final Intent response) {
        // dConnectManagerに登録されているイベントを削除
        String sessionKey = request.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "sessionKey is null.");
            ((DConnectService) getContext()).sendResponse(request, response);
        } else {
            EventManager.INSTANCE.removeEvents(sessionKey);

            // 各デバイスプラグインにイベントを削除依頼を送る
            RemoveEventsRequest req = new RemoveEventsRequest();
            req.setContext(getContext());
            req.setRequest(request);
            req.setDevicePluginManager(mPluginMgr);
            ((DConnectMessageService) getContext()).addRequest(req);
        }

        // 各デバイスプラグインに送信する場合にはfalseを返却、
        // dConnectManagerで止める場合にはtrueを返却する
        // ここでは、各デバイスには渡さないのでtrueを返却する。
        return true;
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return SettingActivity.class;
    }

    /**
     * AndroidManifest.xmlのversionNameを取得する.
     * 
     * @return versionName
     */
    private String getCurrentVersionName() {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }
}
