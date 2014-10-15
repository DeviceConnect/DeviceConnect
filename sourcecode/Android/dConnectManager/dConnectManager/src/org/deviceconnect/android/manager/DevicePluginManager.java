/*
 DevicePluginManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;

/**
 * デバイスプラグインを管理するクラス.
 * @author NTT DOCOMO, INC.
 */
public class DevicePluginManager {
    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");
    /** デバイスプラグインに格納されるメタタグ名. */
    private static final String PLUGIN_META_DATA = "org.deviceconnect.android.deviceplugin";
    /** マスクを定義. */
    private static final int MASK = 0xFF;
    /** デバイスプラグイン一覧. */
    private final Map<String, DevicePlugin> mPlugins = new ConcurrentHashMap<String, DevicePlugin>();
    /** このクラスが属するコンテスト. */
    private Context mContext;
    /** dConnectManagerのドメイン名. */
    private String mDConnectDomain;

    /** イベントリスナー. */
    private DevicePluginEventListener mEventListener;

    /**
     * コンストラクタ.
     * @param context このクラスが属するコンテキスト
     * @param domain dConnect Managerのドメイン
     */
    public DevicePluginManager(final Context context, final String domain) {
        this.mContext = context;
        setDConnectDomain(domain);
    }
    /**
     * イベントリスナーを設定する.
     * @param listener リスナー
     */
    public void setEventListener(final DevicePluginEventListener listener) {
        mEventListener = listener;
    }
    /**
     * dConnect Managerのドメイン名を設定する.
     * @param domain ドメイン名
     */
    public void setDConnectDomain(final String domain) {
        mDConnectDomain = domain;
    }
    /**
     * アプリ一覧からデバイスプラグイン一覧を作成する.
     */
    public void createDevicePluginList() {
        PackageManager pkgMgr = mContext.getPackageManager();
        List<PackageInfo> pkgList = pkgMgr.getInstalledPackages(PackageManager.GET_RECEIVERS);
        if (pkgList != null) {
            for (PackageInfo pkg : pkgList) {
                ActivityInfo[] receivers = pkg.receivers;
                if (receivers != null) {
                    for (int i = 0; i < receivers.length; i++) {
                        String packageName = receivers[i].packageName;
                        String className = receivers[i].name;
                        checkAndAddDevicePlugin(new ComponentName(packageName, className));
                    }
                }
            }
        }
    }

    /**
     * 指定されたIntentからデバイスプラグインを確認して、リストに追加する.
     * @param intent 追加するデバイスプラグインのIntent
     */
    public void checkAndAddDevicePlugin(final Intent intent) {
        checkAndAddDevicePlugin(getPackageName(intent));
    }

    /**
     * 指定されたパッケージの中にデバイスプラグインが存在するかチェックし追加する.
     * パッケージの中にデバイスプラグインがない場合には何もしない。
     * @param packageName パッケージ名
     */
    public void checkAndAddDevicePlugin(final String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName is null.");
        }
        PackageManager pkgMgr = mContext.getPackageManager();
        try {
            PackageInfo pkg = pkgMgr.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
            if (pkg != null) {
                ActivityInfo[] receivers = pkg.receivers;
                if (receivers != null) {
                    for (int i = 0; i < receivers.length; i++) {
                        String pkgName = receivers[i].packageName;
                        String className = receivers[i].name;
                        checkAndAddDevicePlugin(new ComponentName(pkgName, className));
                    }
                }
            }
        } catch (NameNotFoundException e) {
            return;
        }
    }

    /**
     * コンポーネントにデバイスプラグインが存在するかチェックし追加する.
     * コンポーネントの中にデバイスプラグインがない場合には何もしない。
     * @param component コンポーネント
     */
    public void checkAndAddDevicePlugin(final ComponentName component) {
        ActivityInfo receiverInfo = null;
        try {
            PackageManager pkgMgr = mContext.getPackageManager();
            receiverInfo = pkgMgr.getReceiverInfo(component, PackageManager.GET_META_DATA);
            if (receiverInfo.metaData != null) {
                Object value = receiverInfo.metaData.get(PLUGIN_META_DATA);
                if (value != null) {
                    String packageName = receiverInfo.packageName;
                    String className = receiverInfo.name;
                    String hash = md5(packageName + className);
                    if (hash == null) {
                        throw new RuntimeException("Can't generate md5.");
                    }
                    sLogger.info("Added DevicePlugin: [" + hash + "]");
                    sLogger.info("    PackageName: " + packageName);
                    sLogger.info("    className: " + className);
                    // TODO 既に同じ名前のデバイスプラグインが存在した場合の処理
                    // 現在は警告を表示し、上書きする.
                    if (mPlugins.containsKey(hash)) {
                        sLogger.warning("DevicePlugin[" + hash + "] already exists.");
                    }

                    DevicePlugin plugin = new DevicePlugin();
                    plugin.setClassName(className);
                    plugin.setPackageName(packageName);
                    plugin.setDeviceId(hash);
                    plugin.setDeviceName(receiverInfo.applicationInfo.loadLabel(pkgMgr).toString());
                    plugin.setSupportProfiles(checkDevicePluginXML(receiverInfo));
                    mPlugins.put(hash, plugin);
                    if (mEventListener != null) {
                        mEventListener.onDeviceFound(plugin);
                    }
                }
            }
        } catch (NameNotFoundException e) {
            return;
        }
    }

    /**
     * 指定されたIntentのデバイスプラグインを削除する.
     * @param intent 削除するデバイスプラグインのIntent
     */
    public void checkAndRemoveDevicePlugin(final Intent intent) {
        checkAndRemoveDevicePlugin(getPackageName(intent));
    }

    /**
     * 指定されたパッケージ名に対応するデバイスプラグインを削除する.
     * @param packageName パッケージ名
     */
    public void checkAndRemoveDevicePlugin(final String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName is null.");
        }
        for (String key : mPlugins.keySet()) {
            DevicePlugin plugin = mPlugins.get(key);
            if (plugin.getPackageName().equals(packageName)) {
                mPlugins.remove(key);
                if (mEventListener != null) {
                    mEventListener.onDeviceLost(plugin);
                }
            }
        }
    }

    /**
     * 指定されたコンポーネントがデバイスプラグインがチェックを行い、デバイスプラグインの場合には削除を行う.
     * @param component 削除するコンポーネント
     */
    public void checkAndRemoveDevicePlugin(final ComponentName component) {
        ActivityInfo receiverInfo = null;
        try {
            PackageManager pkgMgr = mContext.getPackageManager();
            receiverInfo = pkgMgr.getReceiverInfo(component, PackageManager.GET_META_DATA);
            if (receiverInfo.metaData != null) {
                String value = receiverInfo.metaData.getString(PLUGIN_META_DATA);
                if (value != null) {
                    String packageName = receiverInfo.packageName;
                    String className = receiverInfo.name;
                    String hash = md5(packageName + className);
                    sLogger.info("Removed DevicePlugin: [" + hash + "]");
                    sLogger.info("    PackageName: " + packageName);
                    sLogger.info("    className: " + className);
                    DevicePlugin plugin = mPlugins.remove(hash);
                    if (plugin != null && mEventListener != null) {
                        mEventListener.onDeviceLost(plugin);
                    }
                }
            }
        } catch (NameNotFoundException e) {
            return;
        }
    }

    /**
     * 指定されたデバイスIDと一致するデバイスプラグインを取得する.
     * @param deviceId デバイスID
     * @return デバイスプラグイン
     */
    public DevicePlugin getDevicePlugin(final String deviceId) {
        return mPlugins.get(deviceId);
    }

    /**
     * 指定されたデバイスIDからデバイスプラグインを取得する.
     * 指定されたdeviceIdに対応するデバイスプラグインが存在しない場合にはnullを返却する。
     * デバイスIDのネーミング規則は以下のようになる。
     * [device].[deviceplugin].[dconnect].deviceconnect.org
     * [dconnect].deviceconnect.org が deviceIdに渡されたときには、
     * すべてのプラグインをListに格納して返します。
     * @param deviceId パースするデバイスID
     * @return デバイスプラグイン
     */
    public List<DevicePlugin> getDevicePlugins(final String deviceId) {
        if (deviceId == null) {
            return null;
        }
        String pluginName = deviceId;
        int idx = pluginName.lastIndexOf(mDConnectDomain);
        if (idx > 0) {
            pluginName = pluginName.substring(0, idx - 1);
        } else {
            // ここで見つからない場合には、デバイスIDとして不正なので
            // nullを返却する。
            return null;
        }
        idx = pluginName.lastIndexOf(DConnectMessageService.SEPARATOR);
        if (idx > 0) {
            pluginName = pluginName.substring(idx + 1);
        }
        if (mPlugins.containsKey(pluginName)) {
            List<DevicePlugin> plugins = new ArrayList<DevicePlugin>();
            plugins.add(mPlugins.get(pluginName));
            return plugins;
        } else {
            return null;
        }
    }

    /**
     * すべてのデバイスプラグインを取得する.
     * @return すべてのデバイスプラグイン
     */
    public List<DevicePlugin> getDevicePlugins() {
        return new ArrayList<DevicePlugin>(mPlugins.values());
    }

    /**
     * デバイスIDにd-Connect Managerのドメイン名を追加する.
     * 
     * デバイスIDがnullのときには、デバイスIDは無視します。
     * 
     * @param plugin デバイスプラグイン
     * @param deviceId デバイスID
     * @return d-Connect Managerのドメインなどが追加されたデバイスID
     */
    public String appendDeviceId(final DevicePlugin plugin, final String deviceId) {
        if (deviceId == null) {
            return plugin.getDeviceId() + DConnectMessageService.SEPARATOR + mDConnectDomain;
        } else {
            return deviceId + DConnectMessageService.SEPARATOR + plugin.getDeviceId() 
                    + DConnectMessageService.SEPARATOR + mDConnectDomain;
        }
    }

    /**
     * リクエストに含まれるセッションキーを変換する.
     * 
     * セッションキーにデバイスプラグインIDとreceiverを追加する。
     * 下記のように、分解できるようになっている。
     * 【セッションキー.デバイスプラグインID@receiver】
     *
     * @param request リクエスト
     */
    public void appendPluginIdToSessionKey(final Intent request) {
        String deviceId = request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        List<DevicePlugin> plugins = getDevicePlugins(deviceId);
        String sessionKey = request.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        if (plugins != null && sessionKey != null) {
            sessionKey = sessionKey + DConnectMessageService.SEPARATOR + plugins.get(0).getDeviceId();
            ComponentName receiver = (ComponentName) request.getExtras().get(DConnectMessage.EXTRA_RECEIVER);
            if (receiver != null) {
                sessionKey = sessionKey + DConnectMessageService.SEPARATOR_SESSION 
                        + receiver.flattenToString();
            }
            request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, sessionKey);
            sLogger.info("sessionKey [dConnectManager->DevicePlugin]: " + sessionKey);
        }
    }

    /**
     * リクエストに含まれるセッションキーを変換する.
     * 
     * セッションキーにデバイスプラグインIDとreceiverを追加する。
     * 下記のように、分解できるようになっている。
     * 【セッションキー.デバイスプラグインID@receiver】
     *
     * @param request リクエスト
     * @param plugin デバイスプラグイン
     */
    public void appendPluginIdToSessionKey(final Intent request, final DevicePlugin plugin) {
        String sessionKey = request.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        if (plugin != null && sessionKey != null) {
            sessionKey = sessionKey + DConnectMessageService.SEPARATOR + plugin.getDeviceId();
            ComponentName receiver = (ComponentName) request.getExtras().get(DConnectMessage.EXTRA_RECEIVER);
            if (receiver != null) {
                sessionKey = sessionKey + DConnectMessageService.SEPARATOR_SESSION 
                        + receiver.flattenToString();
            }
            request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, sessionKey);
            sLogger.info("sessionKey [dConnectManager->DevicePlugin]: " + sessionKey);
        }
    }

    /**
     * 指定されたリクエストのdeviceIdからプラグインIDを削除する.
     * @param request リクエスト
     */
    public void splitePluginIdToDeviceId(final Intent request) {
        String deviceId = request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        List<DevicePlugin> plugins = getDevicePlugins(deviceId);
        // 各デバイスプラグインへ渡すデバイスIDを作成
        String id = DevicePluginManager.spliteDeviceId(plugins.get(0), deviceId);
        request.putExtra(IntentDConnectMessage.EXTRA_DEVICE_ID, id);
        sLogger.info("deviceId [dConnectManager->DevicePlugin]: " + id);
    }

    /**
     * デバイスIDを分解して、d-Connect Managerのドメイン名を省いた本来のデバイスIDにする.
     * d-Connect Managerのドメインを省いたときに、何もない場合には空文字を返します。
     * @param plugin デバイスプラグイン
     * @param deviceId デバイスID
     * @return d-Connect Managerのドメインが省かれたデバイスID
     */
    public static String spliteDeviceId(final DevicePlugin plugin, final String deviceId) {
        String p = plugin.getDeviceId();
        int idx = deviceId.indexOf(p);
        if (idx > 0) {
            return deviceId.substring(0, idx - 1);
        }
        return "";
    }

    /**
     * deviceplugin.xmlの確認を行う.
     * @param info receiverのタグ情報
     * @return プロファイル一覧
     */
    public List<String> checkDevicePluginXML(final ActivityInfo info) {
        PackageManager pkgMgr = mContext.getPackageManager();
        XmlResourceParser xpp = info.loadXmlMetaData(pkgMgr, PLUGIN_META_DATA);
        try {
            return parseDevicePluginXML(xpp);
        } catch (XmlPullParserException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * xml/deviceplugin.xmlの解析を行う.
     * 
     * @param xpp xmlパーサ
     * @throws XmlPullParserException xmlの解析に失敗した場合に発生
     * @throws IOException xmlの読み込みに失敗した場合
     * @return プロファイル一覧
     */
    private List<String> parseDevicePluginXML(final XmlResourceParser xpp) 
            throws XmlPullParserException, IOException {
        ArrayList<String> list = new ArrayList<String>();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            final String name = xpp.getName();
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                break;
            case XmlPullParser.START_TAG:
                if (name.equals("profile")) {
                    list.add(xpp.getAttributeValue(null, "name"));
                }
                break;
            case XmlPullParser.END_TAG:
                break;
            default:
                break;
            }
            eventType = xpp.next();
        }
        return list;
    }

    /**
     * バイト配列を16進数の文字列に変換する.
     * @param buf 文字列に変換するバイト
     * @return 文字列
     */
    private String hexToString(final byte[] buf) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            hexString.append(Integer.toHexString(MASK & buf[i]));
        }
        return hexString.toString();
    }

    /**
     * 指定された文字列をMD5の文字列に変換する.
     * MD5への変換に失敗した場合にはnullを返却する。
     * @param s MD5にする文字列
     * @return MD5にされた文字列
     */
    private String md5(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes("ASCII"));
            return hexToString(digest.digest());
        } catch (UnsupportedEncodingException e) {
            sLogger.warning("Not support Charset.");
        } catch (NoSuchAlgorithmException e) {
            sLogger.warning("Not support MD5.");
        }
        return null;
    }

    /**
     * インストール・アンインストールしたアプリのパッケージ名を取得する.
     * @param intent パッケージ名を取得するIntent
     * @return パッケージ名
     */
    private String getPackageName(final Intent intent) {
        String pkgName = intent.getDataString();
        int idx = pkgName.indexOf(":");
        if (idx != -1) {
            pkgName = pkgName.substring(idx + 1);
        }
        return pkgName;
    }

    /**
     * デバイスプラグインの発見、見失う通知を行うリスナー.
     * @author NTT DOCOMO, INC.
     */
    public interface DevicePluginEventListener {
        /**
         * デバイスプラグインが発見されたことを通知する.
         * @param plugin 発見されたデバイスプラグイン
         */
        void onDeviceFound(DevicePlugin plugin);

        /**
         * デバイスプラグインを見失ったことを通知する.
         * @param plugin 見失ったデバイスプラグイン
         */
        void onDeviceLost(DevicePlugin plugin);
    }
}
