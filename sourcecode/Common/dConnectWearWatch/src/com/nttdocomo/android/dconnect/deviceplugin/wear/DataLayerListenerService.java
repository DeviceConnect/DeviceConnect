
package com.nttdocomo.android.dconnect.deviceplugin.wear;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.util.List;

/**
 * プラグインとのやり取りをおこなうService.
 */
public class DataLayerListenerService extends WearableListenerService implements SensorEventListener {

	/** Tag for Command. */
	private static final String CMD = "COMMAND";

	/** Tag. */
    private static final String TAG = "WEAR";

    /** Tag for Data. */
    private static final String DATA = "DATA";

    /** Google API Client. */
    private static GoogleApiClient mGoogleApiClient;

    /** SensorManager. */
    private static SensorManager mSensorManager;

    /** 加速度 x. */
    private static float mAccellX;

    /** 加速度 y. */
    private static float mAccellY;

    /** 加速度 z. */
    private static float mAccellZ;

    /** Gyro x. */
    private static float mGyroX;

    /** Gyro y. */
    private static float mGyroY;

    /** Gyro z. */
    private static float mGyroZ;

    /** DeviceのNodeID .*/
    private String mId;

    /** GyroSensor. */
    private Sensor myGyroSensor;

    /** AcceleratorSensor. */
    private Sensor mAccelerometer;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate:");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy:");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
    	//Toast.makeText(this, "onMessageReceived:", Toast.LENGTH_LONG).show();
    	mId = messageEvent.getSourceNodeId();
        if (messageEvent.getPath().equals(WearConst.DEVICE_TO_WEAR_VIBRATION_RUN)) {
            Log.i(CMD, "+++ DEVICE_TO_WEAR_VIBRATION_RUN");
        	// Patternの取得
        	String mPattern = new String(messageEvent.getData());

        	// PatternのLong配列の生成
        	String[] mPatternArray = mPattern.split(",", 0);
        	long[] mPatternLong = new long[mPatternArray.length + 1];
        	mPatternLong[0] = 0;
        	for (int i = 1; i < mPatternLong.length; i++) {
        		mPatternLong[i] = Integer.parseInt(mPatternArray[i - 1]);
        	}

        	// Vibrationの実行
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);			
            vibrator.vibrate(mPatternLong, -1);
        } else if (messageEvent.getPath().equals(WearConst.DEVICE_TO_WEAR_VIBRATION_DEL)) {
            Log.i(CMD, "--- DEVICE_TO_WEAR_VIBRATION_DEL");
         	// Vibrationの停止
             Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
             vibrator.cancel();
             
        } else if (messageEvent.getPath().equals(WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_REGISTER)) {
        	Log.i(CMD, "+++ REGISTER");
        	
        	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
        	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

             if (sensors.size() > 0) {
            	 mAccelerometer = sensors.get(0);
                 mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
             }
  
             sensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

             if (sensors.size() > 0) {
            	 myGyroSensor = sensors.get(0);
                 mSensorManager.registerListener(this, myGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
 
             }
        } else if (messageEvent.getPath().equals(WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_UNREGISTER)) {
        	Log.i(CMD, "--- UNREGISTER");

        	if (mSensorManager != null) {
        		mSensorManager.unregisterListener(this, mAccelerometer);
        		mSensorManager.unregisterListener(this, myGyroSensor);
        		mSensorManager.unregisterListener(this);
        		mSensorManager = null;
        		
        	}
        	
        	mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPeerConnected(final Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(final Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }

	@Override
	public void onSensorChanged(final SensorEvent sensorEvent) {
		// TODO Auto-generated method stub

		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mAccellX = sensorEvent.values[0];
			mAccellY = sensorEvent.values[1];
			mAccellZ = sensorEvent.values[2];
			final String data = mAccellX + "," + mAccellY + "," + mAccellZ
					+ "," + mGyroX + "," + mGyroY + "," + mGyroZ;

			// メッセージの送信
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(final Void... params) {
					if (!mGoogleApiClient.isConnected()) {
						mGoogleApiClient.connect();
					} else {
						MessageApi.SendMessageResult result = Wearable.MessageApi
								.sendMessage(
										mGoogleApiClient,
										mId,
										WearConst.WERA_TO_DEVICE_DEIVCEORIENTATION_DATA,
										data.getBytes()).await();
						if (!result.getStatus().isSuccess()) {
							Log.d(TAG, "SEND ERROR: failed to send Message: "
									+ result.getStatus());
						} else {
							Log.d(TAG, "SEND SUCCESS: result.getStatus():"
									+ result.getStatus());
						}
					}
					return null;
				}
			}
			.execute();

			Log.i(DATA, "data:" + data);
		} else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			mGyroX = sensorEvent.values[0];
			mGyroY = sensorEvent.values[1];
			mGyroZ = sensorEvent.values[2];
		}
	}

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
	}
}