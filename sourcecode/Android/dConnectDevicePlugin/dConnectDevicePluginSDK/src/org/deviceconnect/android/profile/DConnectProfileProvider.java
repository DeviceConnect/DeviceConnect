/*
 DConnectProfileProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.util.List;

/**
 * Device Connect プロファイルプロバイダー.
 * @author NTT DOCOMO, INC.
 */
public interface DConnectProfileProvider {

    /**
     * プロファイルリストを取得する.
     * 
     * @return プロファイルマップ
     */
    List<DConnectProfile> getProfileList();

    /**
     * プロファイルマップを取得する.
     * 
     * @param name プロファイル名
     * @return プロファイル
     */
    DConnectProfile getProfile(final String name);

    /**
     * プロファイルを追加する.
     * 
     * @param profile プロファイル
     */
    void addProfile(final DConnectProfile profile);

    /**
     * プロファイルを削除する.
     * 
     * @param profile プロファイル
     */
    void removeProfile(final DConnectProfile profile);

}
