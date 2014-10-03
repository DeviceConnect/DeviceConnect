package com.nttdocomo.android.dconnect.deviceplugin.pebble.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.pebble.PebbleDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager.OnSendDataListener;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.FileProfile;
import com.nttdocomo.android.dconnect.provider.FileManager;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Pebble 用の pbi イメージを作成して、バイナリー送信をするサンプルプログラム.
 * 
 * @author terawaki
 */
public class PebbleFileProfile extends FileProfile {

    /**
     * コンストラクタ.
     * @param fileMgr
     */
    public PebbleFileProfile(final FileManager fileMgr) {
        super(fileMgr);
    }

    @Override
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, final String path,
            final String mimeType, final byte[] data) {
        
        if (path == null || path.equals("")) {
            MessageUtils.setInvalidRequestParameterError(response, "path is not specied to update a file.");
            return true;
        }

        if (data == null) {
            MessageUtils.setInvalidRequestParameterError(response, "data is not specied to update a file.");
            return true;
        }

        PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
        byte[] buf = PebbleManager.convertImage(data);
        mgr.sendDataToPebble(buf, new OnSendDataListener() {
            @Override
            public void onSend(final boolean successed) {
                if (successed) {
                    setResult(response, DConnectMessage.RESULT_OK);
                } else {
                    MessageUtils.setUnknownError(response);
                }
                getContext().sendBroadcast(response);
            }
        });
        return false;
    }
}
