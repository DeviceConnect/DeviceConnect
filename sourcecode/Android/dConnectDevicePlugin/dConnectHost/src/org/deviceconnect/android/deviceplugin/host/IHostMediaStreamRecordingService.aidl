
package org.deviceconnect.android.deviceplugin.host;

interface IHostMediaStreamRecordingService { 
    void sendPreviewData(in byte[] data, in int format, int width, in int height);
}
