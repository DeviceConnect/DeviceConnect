/*
 WearDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.wear.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * DeviceOrientationプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearDeviceOrientationProfile extends DeviceOrientationProfile
		implements ConnectionCallbacks, OnConnectionFailedListener {

	/** Google Play Service. */
	private GoogleApiClient mGoogleApiClient;

	/** Tag. */
	private static final String TAG = "WEAR";

	/** Tag. */
	private static final String TAG_DATA = "DATA";

	/** StaticなDevice名. */
	private static String mDeviceId;

	/** Status. */
	private static int statusEvent;

	/** EVENT_REGISTER . */
	private static final int EVENT_REGISTER = 1;

	/** EVENT_UNREGISTER . */
	private static final int EVENT_UNREGISTER = 2;
	
	/** Inerval Time. */
	private static final int INTERVAL_TIME = 200;

	/** 内部管理用iD. */
	private static String mId = "";

	/**
	 * デバイスIDをチェックする.
	 * 
	 * @param deviceId
	 *            デバイスID
	 * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
	 */
	private boolean checkDeviceId(final String deviceId) {
		String regex = WearNetworkServiceDiscoveryProfile.DEVICE_ID;
		Pattern mPattern = Pattern.compile(regex);
		Matcher match = mPattern.matcher(deviceId);

		return match.find();
	}

	/**
	 * デバイスIDが空の場合のエラーを作成する.
	 * 
	 * @param response
	 *            レスポンスを格納するIntent
	 */
	private void createEmptyDeviceId(final Intent response) {
		setResult(response, DConnectMessage.RESULT_ERROR);
	}

	/**
	 * デバイスが発見できなかった場合のエラーを作成する.
	 * 
	 * @param response
	 *            レスポンスを格納するIntent
	 */
	private void createNotFoundDevice(final Intent response) {
		setResult(response, DConnectMessage.RESULT_ERROR);
	}

	@Override
	protected boolean onPutOnDeviceOrientation(final Intent request,
			final Intent response, final String deviceId,
			final String sessionKey) {
		if (deviceId == null) {
			createEmptyDeviceId(response);
		} else if (!checkDeviceId(deviceId)) {
			createNotFoundDevice(response);
		} else if (sessionKey == null) {
			MessageUtils.setInvalidRequestParameterError(response);
		} else {
			mDeviceId = deviceId;
			mId = getNodeId(deviceId);
			statusEvent = EVENT_REGISTER;

			// イベントの登録
			EventError error = EventManager.INSTANCE.addEvent(request);

			if (error == EventError.NONE) {

				mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
						.addApi(Wearable.API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();

				if (!mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}

				setResult(response, DConnectMessage.RESULT_OK);

				return true;
			} else {
				setResult(response, DConnectMessage.RESULT_ERROR);
				return true;
			}

		}
		return true;
	}

	@Override
	protected boolean onDeleteOnDeviceOrientation(final Intent request,
			final Intent response, final String deviceId,
			final String sessionKey) {
		if (deviceId == null) {
			createEmptyDeviceId(response);
		} else if (!checkDeviceId(deviceId)) {
			createNotFoundDevice(response);
		} else if (sessionKey == null) {
			MessageUtils.setInvalidRequestParameterError(response);
		} else {
			mId = getNodeId(deviceId);
			statusEvent = EVENT_UNREGISTER;
			mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
					.addApi(Wearable.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();

			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.connect();
			} else {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(final Void... params) {
						Collection<String> nodes = getNodes();
						sendMessageToWear(
								mId,
								EVENT_UNREGISTER, 
								nodes, 
								WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_UNREGISTER, 
								"");
						return null;

					}
				}
				.execute();
			}

			// イベントの解除
			EventError error = EventManager.INSTANCE.removeEvent(request);
			if (error == EventError.NONE) {
				setResult(response, DConnectMessage.RESULT_OK);
				return true;
			} else {
				setResult(response, DConnectMessage.RESULT_ERROR);
				return true;
			}

		}
		return true;
	}

	@Override
	public void onConnected(final Bundle connectionHint) {

		Log.i(TAG, "onConnected");

		if (statusEvent == EVENT_REGISTER) {
			
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(final Void... params) {
					Collection<String> nodes = getNodes();
					sendMessageToWear(
							mId,
							EVENT_REGISTER,
							nodes,
							WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_REGISTER,
							"");
					return null;
				}
			}
			.execute();

		} else if (statusEvent == EVENT_UNREGISTER) {
			Log.i(TAG, "onConnected:EVENT_UNREGISTER");
			
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(final Void... params) {					
					Collection<String> nodes = getNodes();
					sendMessageToWear(
							mId,
							EVENT_UNREGISTER, 
							nodes, 
							WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_UNREGISTER, 
							"");
					return null;
				}
			}
			.execute();
		}

	}

	@Override
	public void onConnectionSuspended(final int cause) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onConnectionSuspended");
	}

	@Override
	public void onConnectionFailed(final ConnectionResult result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onConnectionFailed");
	}

	/**
	 * Wear nodeを取得.
	 * 
	 * @return WearNode
	 */
	private Collection<String> getNodes() {

		HashSet<String> results = new HashSet<String>();
		NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
				.getConnectedNodes(mGoogleApiClient).await();

		for (Node node : nodes.getNodes()) {
			Log.d(TAG, "node.getId():" + node.getId());
			results.add(node.getId());
		}

		return results;
	}

	/**
	 * Wearにメッセージを送信.
	 * 
	 * @param id データを送信するNode ID
	 * @param status ステータス
	 * @param nodes ノード一覧
	 * @param action アクション名
	 * @param message 送信する文字列
	 */
	public void sendMessageToWear(final String id, final int status, 
				final Collection<String> nodes, final String action, final String message) {

		if (status == EVENT_REGISTER) {
			for (String node : nodes) {
				
				// 指定デバイスのノードに送信
				if (node.indexOf(id) != -1) {
					Wearable.MessageApi.addListener(mGoogleApiClient,
							// データ受信用リスナーを登録
							new MessageApi.MessageListener() {
								@Override
								public void onMessageReceived(final MessageEvent messageEvent) {
									final String data = new String(messageEvent.getData());
		
									sendMessageToEvent(data);
								}
							});
		
					MessageApi.SendMessageResult result = 
								Wearable.MessageApi
								.sendMessage(mGoogleApiClient, node, action, message.getBytes())
								.await();
		
					if (!result.getStatus().isSuccess()) {
						
					} else {
						
					}
				}
			}
		} else if (status == EVENT_UNREGISTER) {
			for (String node : nodes) {
				
				// 指定デバイスのノードに送信
				if (node.indexOf(id) != -1) {
					MessageApi.SendMessageResult result = 
							Wearable.MessageApi
							.sendMessage(mGoogleApiClient, 
									node,
									WearConst.DEVICE_TO_WEAR_DEIVCEORIENTATION_UNREGISTER,
									"".getBytes())
							.await();
	
					if (!result.getStatus().isSuccess()) {
					
					} else {
						mGoogleApiClient.disconnect();
					}
				}
			}
		}
	}
	
	/**
	 * 登録イベントにメッセージ送信.
	 * 
	 * @param data 受信した文字列
	 */
	private void sendMessageToEvent(final String data) {
		
		String[] mDataArray = data.split(",", 0);

		Bundle orientation = new Bundle();
		Bundle a1 = new Bundle();
		a1.putDouble(DeviceOrientationProfile.PARAM_X, 0.0);
		a1.putDouble(DeviceOrientationProfile.PARAM_Y, 0.0);
		a1.putDouble(DeviceOrientationProfile.PARAM_Z, 0.0);
		Bundle a2 = new Bundle();
		a2.putDouble(DeviceOrientationProfile.PARAM_X, Double.parseDouble(mDataArray[0]));
		a2.putDouble(DeviceOrientationProfile.PARAM_Y, Double.parseDouble(mDataArray[1]));
		a2.putDouble(DeviceOrientationProfile.PARAM_Z, Double.parseDouble(mDataArray[2]));
		Bundle r = new Bundle();
		r.putDouble(DeviceOrientationProfile.PARAM_ALPHA, Double.parseDouble(mDataArray[3]));
		r.putDouble(DeviceOrientationProfile.PARAM_BETA, Double.parseDouble(mDataArray[4]));
		r.putDouble(DeviceOrientationProfile.PARAM_GAMMA, Double.parseDouble(mDataArray[5]));
		orientation.putBundle(DeviceOrientationProfile.PARAM_ACCELERATION, a1);
		orientation.putBundle(DeviceOrientationProfile.PARAM_ACCELERATION_INCLUDING_GRAVITY, a2);
		orientation.putBundle(DeviceOrientationProfile.PARAM_ROTATION_RATE, r);
		orientation.putLong(DeviceOrientationProfile.PARAM_INTERVAL, 0);
		setInterval(orientation, INTERVAL_TIME);
		
		List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId,
					DeviceOrientationProfile.PROFILE_NAME,
					null,
					DeviceOrientationProfile.ATTRIBUTE_ON_DEVICE_ORIENTATION);

		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			Intent intent = EventManager.createEventMessage(event);

			intent.putExtra(DeviceOrientationProfile.PARAM_ORIENTATION, orientation);
			getContext().sendBroadcast(intent);
		}
	}
	
	/**
	 * DeviceIDがらnodeを取得.
	 * 
	 * @param deviceId  デバイスID
	 * @return nodeId 内部管理用NodeID
	 */
	private String getNodeId(final String deviceId) {

		String[] mDeviceIdArray = deviceId.split("\\(", 0);
		String id = mDeviceIdArray[1].replace(")", "");

		return id;
	}
}
