/*
 FileProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import java.io.ByteArrayOutputStream;

import org.deviceconnect.android.deviceplugin.sw.R;
import org.deviceconnect.android.deviceplugin.sw.SWConstants;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.FileProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;

/**
 * SonySWデバイスプラグインの{@link FileProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWFileProfile extends FileProfile {

    /**
     * コンストラクタ.
     * 
     * @param fileMgr fileMgr {@link FileManager}のインスタンス
     */
    public SWFileProfile(final FileManager fileMgr) {
        super(fileMgr);
    }

    @Override
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, 
            final String path, final String mimeType, final byte[] data) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        if (data == null || path == null || path.equals("") || deviceId == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }
        DisplaySize size = determineDisplaySize(getContext(), SWUtil.toHostAppPackageName(device.getName()));

        showDisplay(data, size, deviceId, response);

        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    /**
     * SWの画面に画像を表示する.
     * 
     * @param data バイナリデータ
     * @param size 画面サイズ
     * @param deviceId デバイスID
     * @param response レスポンス
     */
    private void showDisplay(final byte[] data, final DisplaySize size, final String deviceId, final Intent response) {
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
        final int width = size.width;
        final int height = size.height;
        if (getSizeW > getSizeH) {
            scale = width / getSizeW;
        } else {
            scale = height / getSizeH;
        }
        // 目標の大きさ
        int targetW = (int) Math.ceil(scale * getSizeW);
        int targetH = (int) Math.ceil(scale * getSizeH);

        resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);

        // 画像描写開始位置の修正
        if (getSizeW > getSizeH) {
            startGridY = (height / 2 - targetH / 2);
        } else {
            startGridX = (width / 2 - targetW / 2);
        }

        // 最終的にSWに表示するBitmapの作成(大きさはSWの画面サイズ)
        viewBitmap = Bitmap.createBitmap(width, height, SWConstants.DEFAULT_BITMAP_CONFIG);

        //canvasに表示用Bitmapをセット
        Canvas canvas = new Canvas(viewBitmap);

        //リサイズした画像をセンタリングしてcanvasにセット
        canvas.drawBitmap(resizedBitmap, startGridX, startGridY, null);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(SWConstants.OUTPUTSTREAM_SIZE);
        viewBitmap.compress(CompressFormat.JPEG, SWConstants.BITMAP_DECODE_QUALITY, outputStream);

        Intent intent = new Intent(Control.Intents.CONTROL_DISPLAY_DATA_INTENT);
        intent.putExtra(Control.Intents.EXTRA_DATA, outputStream.toByteArray());
        sendToHostApp(intent, deviceId);
    }

    /**
     * 指定されたホストアプリケーションに対応するSWの画面サイズを返す.
     * 
     * @param context コンテキスト
     * @param hostAppPackageName ホストアプリケーション名(SW1orSW2)
     * @return 画面サイズ
     */
    private static DisplaySize determineDisplaySize(final Context context, final String hostAppPackageName) {
        boolean smartWatch2Supported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(context, hostAppPackageName);
        int width;
        int height;
        if (smartWatch2Supported) {
            width = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
        } else {
            width = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
        }
        return new DisplaySize(width, height);
    }

    /**
     * ホストアプリケーションに対してインテントを送信する.
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
