/*
 BaseSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;

import android.provider.BaseColumns;

/**
 * 共通スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface BaseSchema extends BaseColumns {

    /** 
     * 作成日.
     */
    String CREATE_DATE = "create_date";
    
    /** 
     * 更新日.
     */
    String UPDATE_DATE = "update_date";
}
