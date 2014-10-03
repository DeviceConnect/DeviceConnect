package com.nttdocomo.android.dconnect.deviceplugin.sw.profile;

import java.io.ByteArrayOutputStream;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import com.nttdocomo.android.dconnect.deviceplugin.sw.R;
import com.nttdocomo.android.dconnect.deviceplugin.sw.SWConstants;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerSW;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.FileProfile;
import com.nttdocomo.android.dconnect.provider.FileManager;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;

/**
FileProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

public class SWFileProfile extends FileProfile {

    /** ロガー. */
    private DcLoggerSW mLogger = new DcLoggerSW();
    /**
     * 画面サイズ(横).
     */
    private int mWidth = SWConstants.DEFAULT_DISPLAY_WIDTH;
    /**
     * 画面サイズ(縦).
     */
    private int mHeight = SWConstants.DEFAULT_DISPLAY_HEIGHT;

    /**
     * 
     * @param fileMgr fileMgr
     */
    public SWFileProfile(final FileManager fileMgr) {
        super(fileMgr);
    }

    @Override
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, 
            final String path, final String mimeType, final byte[] data) {

        if (!checkSendFile(response, deviceId, data, path)) {
            return SWConstants.SYNC_RESPONSE;
        }

        mLogger.info(this, "onPostSend", path);
        mLogger.info(this, "onPostSend", data);
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        determineSize(getContext(), SWUtil.toHostAppPackageName(device.getName()));

        showDisplay(data, deviceId, response);

        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    /**
     * エラーチェック.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param data バイナリデータ
     * @param path バイナリ名
     * @return boolean
     */
    private boolean checkSendFile(final Intent response, final String deviceId, final byte[] data, final String path) {

        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return false;
        }

        if (data == null || path == null || path.equals("") || deviceId == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            mLogger.info(this, "onPostSend:path", path);
            mLogger.info(this, "onPostSend:data", data);
            mLogger.info(this, "onPostSend:data", deviceId);
            return false;
        }

        return true;

    }

    /**
     * SonyWatchDisplayへの表示.
     * 
     * @param data バイナリデータ
     * @param deviceId デバイスID
     * @param response 
     */
    private void showDisplay(final byte[] data, final String deviceId, final Intent response) {

        mLogger.entering(this, "showDisplay");

        Bitmap bitmap;
        Bitmap viewBitmap;
        Bitmap resizedBitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // 画像サイズ取得
        float getSizeW = bitmap.getWidth();
        float getSizeH = bitmap.getHeight();

        // 描画開始地点
        float startGridX = 0;
        float startGridY = 0;

        // 拡大率:縦横で長い方が画面ピッタリになるように
        float scale;
        if (getSizeW > getSizeH) {
            scale = mWidth / getSizeW;
        } else {
            scale = mHeight / getSizeH;
        }
        // 目標の大きさ
        int targetW = (int) Math.ceil(scale * getSizeW);
        int targetH = (int) Math.ceil(scale * getSizeH);

        resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);

        // 画像描写開始位置の修正
        if (getSizeW > getSizeH) {
            startGridY = (mHeight / 2 - targetH / 2);
        } else {
            startGridX = (mWidth / 2 - targetW / 2);
        }
        mLogger.info(this, "showDisplay:startGridX", startGridX);
        mLogger.info(this, "showDisplay:startGridY", startGridY);
        mLogger.info(this, "showDisplay:targetW", targetW);
        mLogger.info(this, "showDisplay:targetH", targetH);

        // 最終的にSWに表示するBitmapの作成(大きさはSWの画面サイズ)
        viewBitmap = Bitmap.createBitmap(mWidth, mHeight, SWConstants.DEFAULT_BITMAP_CONFIG);

        //canvasに表示用Bitmapをセット
        Canvas canvas = new Canvas(viewBitmap);

        //リサイズした画像をセンタリングしてcanvasにセット
        canvas.drawBitmap(resizedBitmap, startGridX, startGridY, null);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(SWConstants.OUTPUTSTREAM_SIZE);
        viewBitmap.compress(CompressFormat.JPEG, SWConstants.BITMAP_DECODE_QUALITY, outputStream);

        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, outputStream.toByteArray());
        sendToHostApp(intent, deviceId);
        mLogger.exiting(this, "showDisplay");
    }

    /**
     * DiplaySize返却.
     * 
     * @param context 
     * @param hostAppPackageName ホストアプリケーション名(SW1orSW2)
     */
    private void determineSize(final Context context, final String hostAppPackageName) {
        boolean smartWatch2Supported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(context, hostAppPackageName);
        if (smartWatch2Supported) {
            mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
            mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
        } else {
            mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
            mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
        }
    }

    // ホストアプリケーションに命令送信
    /**
     * 
     * @param intent インテント
     * @param deviceId デバイスID
     */
    protected void sendToHostApp(final Intent intent, final String deviceId) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        String deviceName = device.getName();
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME, getContext().getPackageName());
        intent.setPackage(SWUtil.toHostAppPackageName(deviceName));
        getContext().sendBroadcast(intent, Registration.HOSTAPP_PERMISSION);
    }

}
