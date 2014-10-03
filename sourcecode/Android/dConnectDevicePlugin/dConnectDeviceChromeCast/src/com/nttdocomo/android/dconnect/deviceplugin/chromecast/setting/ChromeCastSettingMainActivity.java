package com.nttdocomo.android.dconnect.deviceplugin.chromecast.setting;

import android.os.Bundle;
import android.view.View;
import com.nttdocomo.android.dconnect.ui.activity.DConnectSettingPageActivity;

/**
 * チュートリアル画面（メイン）
 * <p>
 * 画面を作成する
 * </p>
 * 
 */
public class ChromeCastSettingMainActivity extends DConnectSettingPageActivity {
	
	private View mView = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }

    @Override
    public int getPageCount() {
        return 1;
    }
    
    @Override
    public void onStart() {
        super.onStart();
    }
 
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View createPage(final int position) {
        return mView;
    }
}
