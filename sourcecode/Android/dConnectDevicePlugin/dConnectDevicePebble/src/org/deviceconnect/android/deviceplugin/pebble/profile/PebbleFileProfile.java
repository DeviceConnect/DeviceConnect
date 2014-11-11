/*
 PebbleFileProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.profile;

import org.deviceconnect.android.deviceplugin.pebble.PebbleDeviceService;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager.OnSendDataListener;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.FileProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;

/**
 * Pebble 用 Fileプロファイル.
 * @author NTT DOCOMO, INC.
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

        if (deviceId == null) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }

        if (!PebbleUtil.checkDeviceId(deviceId)) {
            MessageUtils.setNotFoundDeviceError(response);
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
