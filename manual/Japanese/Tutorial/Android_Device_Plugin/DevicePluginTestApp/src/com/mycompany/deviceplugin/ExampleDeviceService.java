package com.mycompany.deviceplugin;

import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;

/** 
* サンプルのdConnectメッセージサービス実装クラス. 
* @author docomo 
*/ 
public class ExampleDeviceService extends DConnectMessageService { 
    @Override 
    public void onCreate() { 
            super.onCreate(); 
            //ここで、DConnectMessageServiceProviderにプロファイルを追加することができる。
            addProfile(new ExampleNetworkServiceDiscoveryProfile());
            addProfile(new ExampleSystemProfile(this));
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new ExampleSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new ExampleNetworkServiceDiscoveryProfile();
    }
}
