/*
 RedirectRepresentation.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.temp;

import java.util.Map;

import org.restlet.ext.oauth.AuthorizationServerResource;
import org.restlet.representation.EmptyRepresentation;

/**
 * RedirectRepresentation(Restletの同名のクラスが複雑で切り離しにくいので、簡略化した別クラスを追加して置き換えた).
 * @author NTT DOCOMO, INC.
 */
public class RedirectRepresentation extends EmptyRepresentation{

	/**
	 * RedirectProc.
	 */
    public enum RedirectProc {
        /** 未設定. */
		nothing,
        /** AuthorizationServerResource.requestAuthorization() 実行. */   
		requestAuthorization,
        /** LoginPageActivity 表示. */
		loginPage,
		/** 認証ページ？. */
		authPage,
	}

    /** セッションID. */
	public static final String SESSION_ID = "session_id";
	
	/** リダイレクトProc. */
	private RedirectProc mRedirectProc = null;
	
	/** オプション. */
	private Map<String, Object> mOptions = null; 
	
	/**
	 * コンストラクタ.
	 * @param redirectProc redirectProc
	 * @param options options
	 */
	public RedirectRepresentation(final RedirectProc redirectProc, final Map<String, Object> options) {
		mRedirectProc = redirectProc;
		mOptions = options; 
	}
	
	/**
	 * RedirectProcを返す.
	 * @return RedirectProc
	 */
	public RedirectProc getRedirectProc() {
		return mRedirectProc;
	}
	
	/**
	 * Optionsを返す.
	 * @return Options
	 */
	public Map<String, Object> getOptions() {
		return mOptions;
	}
}
