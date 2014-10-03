package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.SonyCameraDeviceService;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.MediaStreamRecordingProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
SonyCameraMediaStreamRecordingProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraMediaStreamRecordingProfile extends MediaStreamRecordingProfile {

    @Override
    protected boolean onGetMediaRecorder(final Intent request, final Intent response, final String deviceId) {
        return ((SonyCameraDeviceService) getContext()).getMediaRecorder(request, response, deviceId);
    }

    @Override
    protected boolean onPutOnPhoto(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found deviceID:" + deviceId);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "Not found sessionKey:" + sessionKey);
        } else {
            EventError error = EventManager.INSTANCE.addEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                MessageUtils.setUnknownError(response);
            }
        }

        mLogger.exiting(this.getClass().getName(), "onPutOnPhoto");
        return true;
    }

    @Override
    protected boolean onPostRecord(final Intent request, final Intent response, final String deviceId,
            final String target, final Long timeslice) {
        return ((SonyCameraDeviceService) getContext()).onPostRecord(request, response, deviceId, target, timeslice);
    }

    @Override
    protected boolean onPutStop(final Intent request, final Intent response,
            final String deviceId, final String mediaId) {
        return ((SonyCameraDeviceService) getContext()).onPutStop(request, response, deviceId, mediaId);
    }

    @Override
    protected boolean onPutOnDataAvailable(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {

        if (deviceId == null) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
        } else {
            EventError error = EventManager.INSTANCE.addEvent(request);
            if (error == EventError.NONE) {
                if (((SonyCameraDeviceService) getContext()).startPreview()) {
                    setResult(response, DConnectMessage.RESULT_OK);
                } else {
                    //エラー追加
                    MessageUtils.setUnknownError(response, "Failed to start preview.");
                }
            } else if (error == EventError.INVALID_PARAMETER) {
                MessageUtils.setInvalidRequestParameterError(response);
            } else if (error == EventError.FAILED) {
                MessageUtils.setUnknownError(response, "Failed to insert event for db.");
            } else if (error == EventError.NOT_FOUND) {
                MessageUtils.setUnknownError(response, "Not found event.");
            } else {
                MessageUtils.setUnknownError(response);
            }
        }

        mLogger.exiting(this.getClass().getName(), "onPutOnDataAvailable");
        return true;
    }

    @Override
    protected boolean onDeleteOnPhoto(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
        } else {
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (error == EventError.INVALID_PARAMETER) {
                MessageUtils.setInvalidRequestParameterError(response);
            } else if (error == EventError.FAILED) {
                MessageUtils.setUnknownError(response, "Failed to uninsert event for db.");
            } else if (error == EventError.NOT_FOUND) {
                MessageUtils.setUnknownError(response, "Not found event.");
            } else {
                MessageUtils.setUnknownError(response);
            }
        }

        mLogger.exiting(this.getClass().getName(), "onDeleteOnPhoto");
        return true;
    }

    @Override
    protected boolean onDeleteOnDataAvailable(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
        } else {
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                if (((SonyCameraDeviceService) getContext()).stopPreview()) {
                    setResult(response, DConnectMessage.RESULT_OK);
                } else {
                    //エラー追加
                    MessageUtils.setUnknownError(response, "Failed to stop preview.");
                }
            } else if (error == EventError.INVALID_PARAMETER) {
                MessageUtils.setInvalidRequestParameterError(response);
            } else if (error == EventError.FAILED) {
                MessageUtils.setUnknownError(response, "Failed to uninsert event for db.");
            } else if (error == EventError.NOT_FOUND) {
                MessageUtils.setUnknownError(response, "Not found event.");
            } else {
                MessageUtils.setUnknownError(response);
            }
        }

        return true;
    }

    @Override
    protected boolean onPostTakePhoto(final Intent request, final Intent response, final String deviceId,
            final String target) {
        return ((SonyCameraDeviceService) getContext()).onPostTakePhoto(request, response, deviceId, target);
    }
}
