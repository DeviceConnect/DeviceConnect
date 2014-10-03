/*
 ConfirmAuthFragmentListener.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth.fragment;

import java.util.EventListener;

/**
 * ConfirmAuthFragmentからのイベントを通知するリスナー.
 * @author NTT DOCOMO, INC.
 */
public interface ConfirmAuthFragmentListener extends EventListener {

    /**
     * OKボタンが押されたイベントを通知.
     */
    void doPositiveClick();
    
    /**
     * Cancelボタンが押されたイベントを通知.
     */
    void doNegativeClick();
}
