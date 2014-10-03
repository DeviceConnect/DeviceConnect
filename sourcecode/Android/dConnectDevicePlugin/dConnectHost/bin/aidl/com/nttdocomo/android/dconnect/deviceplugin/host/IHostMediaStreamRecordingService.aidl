
package com.nttdocomo.android.dconnect.deviceplugin.host;

interface IHostMediaStreamRecordingService { 
    void sendPreviewData(in byte[] data, in int format, int width, in int height);
}
