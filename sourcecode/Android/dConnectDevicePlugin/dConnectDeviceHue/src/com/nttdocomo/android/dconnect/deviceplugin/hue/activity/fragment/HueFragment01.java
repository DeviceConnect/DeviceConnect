package com.nttdocomo.android.dconnect.deviceplugin.hue.activity.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

/**
HueFargment01
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * Hue設定画面(1)フラグメント.
 */
public class HueFragment01 extends Fragment implements OnClickListener {
    
    /** listViewに設定するItem. */
    private static Set<String> items = new HashSet<String>();
    
    /** ListViewのAdapter. */
    private static ArrayAdapter<String> adapter;
    
    /** AccessPointのリスト. */
    private static ListView accessPointList;
   
    /** Activity. */
    private static  Activity mActivity;
    
    /** ListViewに設定するItemのリスト. */
    private static List<String> myList;
    
    /** 再検索ボタン. */
    private static Button mButton;
    
    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * HueSDKオブジェクト.
     */
    private PHHueSDK mPhHueSDK;
    
    /**
     * ProgressZone.
     */
    private static View progressZone;
    
    /**
     * リストアダプタオブジェクト.
     */
    //private AccessPointListAdapter adapter;
    View mRootView = null;
    
    /**
     * プログレスダイアログ.
     */
    private ProgressDialog mProgressDialog;
    
    
    @Override
    public View onCreateView(final LayoutInflater inflater, 
            final ViewGroup container, final Bundle savedInstanceState) {
        
        mLogger.entering(this, "onCreateView");
        mActivity = this.getActivity();
        
        try {
            mRootView = inflater.inflate(R.layout.hue_fragment_01, container, false);

            mPhHueSDK = HueControl.getPHHueSDK();
            mPhHueSDK.getNotificationManager().registerSDKListener(mListener);

            searchList();
        } catch (Exception e) {
            mLogger.warning(this, "onCreateView", "", e);
        }

        mButton = (Button)mRootView.findViewById(R.id.btnRefresh);
        mButton.setOnClickListener(this);
        
        progressZone = mRootView.findViewById(R.id.progress_zone);
        progressZone.setVisibility(View.VISIBLE);
        
        mLogger.exiting(this, "onCreateView");
        
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            dispBridgeIp();

        } catch (Exception e) {
            mLogger.warning(this, "onResume", "", e);
        }

    }

    /**
     * ブリッジIPの表示.
     */
    private void dispBridgeIp() {
        //dispBridgeIp(R.id.txtBridgeNo2);
    }

    /**
     * ブリッジIP一覧のセット.
     */
    private void searchList() {
        
        mLogger.entering(this, "searchList");
        
        myList = new ArrayList<String>(items);
        adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.row, R.id.row_textview1, myList);
        
        
        // Create the HueSDK singleton
        mPhHueSDK = HueControl.getPHHueSDK();
        mPhHueSDK.getNotificationManager().registerSDKListener(mListener);

        mPhHueSDK.getAccessPointsFound();
        
        // ListViewに紐付け
        accessPointList = (ListView) mRootView.findViewById(R.id.bridge_list2);
        accessPointList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                try {
                   
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    
                   
                    transaction.setCustomAnimations(
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_right_exit);
                    
                    String adr = myList.get((int) id);
                    
                    String mac = adr.substring( 0, 17 );
                    String ip = adr.substring( 20, adr.length() - 2 );
                    
                    transaction.replace(R.id.fragment_frame, HueFragment02.newInstance(mac, ip));
                    
                    transaction.commit();   

                } catch (Exception e) {
                    mLogger.warning(this, "onItemClick", "", e);
                }
            }
        });
        accessPointList.setAdapter(adapter);

        // ブリッジ検索
        doBridgeSearch();

        mLogger.exiting(this, "searchList");

    }



    // 画面終了
    @Override
    public void onDestroy() {

        mLogger.entering("HueFargment01", "onDestroy");
        try {
            if (mListener != null) {
                //画面非表示で消したほうがいいかも？
                mPhHueSDK.getNotificationManager().unregisterSDKListener(mListener);
                
            }
            mPhHueSDK.disableAllHeartbeat();

        } catch (Exception e) {
            mLogger.warning("HueFargment01", "onDestroy", "", e);

        }

        mLogger.exiting("HueFargment01", "onDestroy");

        super.onDestroy();

    }

    /**
     * ブリッジ検索処理.
     */
    private void doBridgeSearch() {
        mLogger.entering(this, "doBridgeSearch");

        // 検索中ダイアログ表示
        //showProgressDialog("", "ブリッジ検索中");
        // 検索
        PHBridgeSearchManager sm = (PHBridgeSearchManager) mPhHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);

        mLogger.exiting(this, "doBridgeSearch");

    }
    
    
 // Create a Listener to receive bridge notifications.
    public PHSDKListener mListener = new PHSDKListener() {

        @Override
        public void onBridgeConnected(final PHBridge b) {
            mPhHueSDK.setSelectedBridge(b);
            mPhHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            mPhHueSDK.getLastHeartbeat().put(
                    b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
          
        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
//           Log.i("TEST", "onAuthenticationRequred:" + accessPoint.getIpAddress());
        }

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {
//            Log.w("TEST", "Access Points Found. " + accessPoint.size());
           
            
           
            if (accessPoint != null && accessPoint.size() > 0) {
                    mPhHueSDK.getAccessPointsFound().clear();
                    mPhHueSDK.getAccessPointsFound().addAll(accessPoint);
                    ArrayList<PHAccessPoint> myPoints = (ArrayList<PHAccessPoint>) mPhHueSDK.getAccessPointsFound();
                    
                    String item = "";
                    
                    for (int i = 0; i < accessPoint.size(); i++) {
                        boolean same = false;
                        for (int d = 0; d < myList.size(); d++) {
                            
                            item = myPoints.get(i).getMacAddress() + " ( " 
                                    + myPoints.get(i).getIpAddress() + " )";
                            
                            if (myList.get(d).equals(item)) {
                                same = true;
                            }
                           
                        }  
                        if (!same) {

                            item = myPoints.get(i).getMacAddress() + " ( " 
                                    + myPoints.get(i).getIpAddress() + " )";
                            
                            myList.add(item); 

                        }
                    }
                    
                   
                    
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accessPointList.invalidateViews();
                            progressZone.setVisibility(View.GONE);
                       }
                   });
                    
                    
            } else {
                
            }

        }

        @Override
        public void onConnectionLost(final PHAccessPoint arg0) {
            
        }

        @Override
        public void onConnectionResumed(final PHBridge arg0) {
            
        }

        @Override
        public void onError(final int arg0, final String arg1) {
            
        }

        @Override
        public void onCacheUpdated(final List<Integer> arg0, final PHBridge arg1) {
            
        }

        @Override
        public void onParsingErrors(final List<PHHueParsingError> arg0) {
            
        }
    };
    
    @Override
    public void onClick(final View v) {
        
        progressZone.setVisibility(View.VISIBLE);
        searchList();
    }
}
