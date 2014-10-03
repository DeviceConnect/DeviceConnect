/*
 com.nttdocomo.android.dconnect.profile.restful.test
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
/**
 * d-Connect RESTful APIのテストケース.
 * <p>
 * 正常系および異常系テストを提供する.
 * </p>
 * <p>
 * 異常系テストでは、以下のようなリクエストに対してテスト用デバイスプラグインからエラー応答が返されることを確認する.
 * <p>
 * <ul>
 * <li>必須パラメータが指定されていない</li>
 * <li>規定されている範囲に含まれない値が指定されている</li>
 * <li>存在しないデバイスIDが指定されている</li>
 * </ul>
 * 本パッケージの各テストケースのリクエストに対するレスポンスは、下記のパッケージで定義する.
 * com.nttdocomo.android.dconnect.test.plugin.profile
 * </p>
 */
package com.nttdocomo.android.dconnect.profile.restful.test;
