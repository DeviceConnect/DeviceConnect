
package com.nttdocomo.android.dconnect.deviceplugin.wear;

import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearConst;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearDeviceOrientationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearNetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearNotificationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.wear.profile.WearVibrationProfile;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.event.cache.db.DBCacheController;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;

/**
 * Dice+デバイスプロバイダ.
 * 
 */
public class WearDeviceService extends DConnectMessageService {
 
	/**
	 * Tag.
	 */
	private static final String TAG = "WEAR";
	
    @Override
    public void onCreate() {
        super.onCreate();
        
        // initialize of the EventManager
        EventManager.INSTANCE.setController(new DBCacheController(this));
        LocalOAuth2Main.initialize(getApplicationContext());
        
        // add supported profiles
        addProfile(new WearNotificationProfile());
        addProfile(new WearVibrationProfile());
        addProfile(new WearDeviceOrientationProfile());
 
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if(action.equals(WearConst.DEVICE_TO_WEAR_NOTIFICATION_OPEN)){
            	String deviceId = intent.getStringExtra(WearConst.PARAM_DEVICEID);
            	int notificationId = intent.getIntExtra(WearConst.PARAM_NOTIFICATIONID, -1);
            	
            	
            	List<Event> events = EventManager.INSTANCE.getEventList(deviceId, WearNotificationProfile.PROFILE_NAME,
	                    null, WearNotificationProfile.ATTRIBUTE_ON_CLICK);

            	for (int i = 0; i < events.size(); i++) {
	                Event event = events.get(i);
	                Intent mIntent = EventManager.createEventMessage(event);
	                mIntent.putExtra(WearNotificationProfile.PARAM_NOTIFICATION_ID, notificationId);
	                getContext().sendBroadcast(mIntent);
            	}
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        
        super.onDestroy();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new WearSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new WearNetworkServiceDiscoveryProfile();
    }
}
