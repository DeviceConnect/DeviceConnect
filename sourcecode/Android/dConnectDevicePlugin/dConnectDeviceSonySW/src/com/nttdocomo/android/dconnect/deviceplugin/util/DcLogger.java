package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.nttdocomo.android.dconnect.deviceplugin.sw.BuildConfig;
import com.nttdocomo.android.dconnect.logger.AndroidHandler;

import android.util.Log;

/**
DcLogger
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * ログ出力.
 * 
 * @see http://www.atmarkit.co.jp/ait/articles/0801/08/news128_2.html
 */
public abstract class DcLogger {

    /**
     * ロガー. 継承先のコンストラクタでオブジェクト生成する事 例：logger =
     * Logger.getLogger("dconnect.dplugin.hue");
     */
    protected Logger mLogger = null;

    /**
     * ログ出力確認.
     * 
     * @return Log出力する場合true
     */
    protected boolean isLogging() {
        return (mLogger != null) && BuildConfig.DEBUG;
    }

    /**
     * 　改行後のスペース.
     */
    protected final String spPad = "           ";

    /**
     * 関数開始時ログ出力 関数の最初に記述すること.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void entering(final Object sourceClass, final String sourceMethod, final Object param) {

        entering(getClassName(sourceClass), sourceMethod, param);

    }

    /**
     * 関数開始時ログ出力 関数の最初に記述すること.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     */
    public void entering(final Object sourceClass, final String sourceMethod) {

        entering(sourceClass, sourceMethod, "");
    }

    /**
     * 関数開始時ログ出力 関数の最初に記述すること.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void entering(final String sourceClassName, final String sourceMethod, final Object param) {

        if (!isLogging()) {
            return;
        }

        mLogger.entering(sourceClassName, sourceMethod, nvl(param));

        Log.d(mLogger.getName(), "■ ===================");
        Log.d(mLogger.getName(), "■ entering " + getMsg(sourceClassName, sourceMethod, param));
    }

    /**
     * 文字列変換して返還.
     * 
     * @param obj Object
     * @return res
     */
    protected String nvl(final Object obj) {

        String res = "";

        if (obj != null) {
            res = obj.toString();
        }

        return res;
    }

    /**
     * 関数終了時ログ出力 関数の最後に記述すること.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void exiting(final Object sourceClass, final String sourceMethod, final Object param) {

        exiting(getClassName(sourceClass), sourceMethod, param);

    }

    /**
     * 関数終了時ログ出力 関数の最後に記述すること.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     */
    public void exiting(final Object sourceClass, final String sourceMethod) {

        exiting(sourceClass, sourceMethod, "");

    }

    /**
     * 関数終了時ログ出力 関数の最後に記述すること.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void exiting(final String sourceClassName, final String sourceMethod, final Object param) {

        if (!isLogging()) {
            return;
        }

        mLogger.exiting(sourceClassName, sourceMethod, nvl(param));

        Log.d(mLogger.getName(), "□  exiting " + getMsg(sourceClassName, sourceMethod, param));

    }

    /**
     * 例外発生時ログ出力 例外処理に記述すること.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param memo メモ
     * @param e 例外
     */
    public void warning(final Object sourceClass, final String sourceMethod, final String memo, final Exception e) {

        warning(getClassName(sourceClass), sourceMethod, memo, e);

    }

    /**
     * 例外発生時ログ出力 例外処理に記述すること.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param memo メモ
     * @param e 例外
     */
    public void warning(final String sourceClassName, final String sourceMethod, final String memo, final Exception e) {

        if (!isLogging()) {
            return;
        }

        String eString = "";
        if (e != null) {
            eString = e.toString();
        }

        String msg = sourceClassName + ".\n" + spPad + sourceMethod + "\n" + spPad + eString + "\n" + spPad + memo
                + "\n";

        mLogger.warning(msg);

        Log.e(mLogger.getName(), "★  Err     " + msg, e);

        e.printStackTrace();
    }

    /**
     * なんらかの情報出したい時.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void info(final Object sourceClass, final String sourceMethod, final Object param) {

        info(getClassName(sourceClass), sourceMethod, param);

    }

    /**
     * なんらかの情報出したい時.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void info(final String sourceClassName, final String sourceMethod, final Object param) {

        if (!isLogging()) {
            return;
        }

        String msg = getMsg(sourceClassName, sourceMethod, param);

        mLogger.info(msg);

        Log.i(mLogger.getName(), "●  info    " + msg);
    }

    /**
     * なんらかの情報出したい時.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void fine(final Object sourceClass, final String sourceMethod, final Object param) {

        fine(getClassName(sourceClass), sourceMethod, param);
    }

    /**
     * なんらかの情報出したい時.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     */
    public void fine(final String sourceClassName, final String sourceMethod, final Object param) {

        if (!isLogging()) {
            return;
        }

        String msg = getMsg(sourceClassName, sourceMethod, param);

        mLogger.fine(msg);

        Log.i(mLogger.getName(), "●  fine    " + msg);
    }

    /**
     * ハンドラ設定.
     * 
     * @param handler ハンドラ
     */
    public void addHandler(final AndroidHandler handler) {

        // Log.dなどで別途出力しているので不要 従来コード互換性のためにメソッドを用意している
        // mLogger.addHandler(handler);

    }

    /**
     * レベル設定.
     * 
     * @param level 設定レベル
     */
    public void setLevel(final Level level) {

        // Log.dなどで別途出力しているので不要 従来コード互換性のためにメソッドを用意している
        // mLogger.setLevel(level);

    }

    /**
     * クラス名を取得する.
     * 
     * @param sourceClass クラス
     * @return クラス名
     */
    protected String getClassName(final Object sourceClass) {

        String myName = sourceClass.getClass().getSimpleName();

        if (myName.getClass() == sourceClass.getClass()) {
            myName = sourceClass.toString();
        }
        return myName;

    }

    /**
     * メッセージ編集.
     * 
     * @param sourceClass ログクラスを行うクラス。呼び出し側でthisを渡す事
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     * @return 編集されたメッセージ
     */
    protected String getMsg(final Object sourceClass, final String sourceMethod, final Object param) {

        return getMsg(getClassName(sourceClass), sourceMethod, param);
    }

    /**
     * メッセージ編集.
     * 
     * @param sourceClassName ログクラスを行うクラス名。
     * @param sourceMethod ログクラスを行うメソッド
     * @param param パラメータ
     * @return 編集されたメッセージ
     */
    protected String getMsg(final String sourceClassName, final String sourceMethod, final Object param) {

        String paramString = "";
        if (param != null) {
            paramString = param.toString();
        }

        String msg = sourceClassName + "." + sourceMethod;

        if (paramString.length() != 0) {
            msg = msg + "\n" + "           " + paramString;
        }

        return msg;
    }

}
