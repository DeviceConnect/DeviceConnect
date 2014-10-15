/*
 BaseCacheController.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache;

import org.deviceconnect.android.event.Event;


/**
 * キャッシュコントローラーのベースクラス. 
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public abstract class BaseCacheController implements EventCacheController {

    /**
     * イベントのパラメータが正しいかチェックする.
     * 
     * @param event イベントデータ
     * @return 正常ならtrue、それ以外はfalseを返す
     */
    protected boolean checkParameter(final Event event) {
        
        if (event == null
                || event.getProfile() == null 
                || event.getAttribute() == null 
                || event.getSessionKey() == null) {
            return false;
        }
        
        return true;
    }

}
