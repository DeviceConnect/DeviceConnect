package com.example.androidappsample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.util.EntityUtils; 

import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.DConnectMessage.ErrorCode;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.BatteryProfileConstants;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.profile.MediaPlayerProfileConstants;
import com.nttdocomo.dconnect.profile.MediaStreamRecordingProfileConstants;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;
import com.nttdocomo.dconnect.profile.ProximityProfileConstants;
import com.nttdocomo.dconnect.profile.SettingsProfileConstants;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;
import com.nttdocomo.dconnect.profile.VibrationProfileConstants;
import com.nttdocomo.dconnect.utils.AuthProcesser;
import com.nttdocomo.dconnect.utils.URIBuilder;
import com.nttdocomo.dconnect.utils.AuthProcesser.AuthorizationHandler;
 
import android.app.Activity; 
import android.content.Context; 
import android.os.Bundle; 
import android.os.Looper; 
import android.view.View; 
import android.view.View.OnClickListener; 
import android.widget.Button; 
import android.widget.EditText;
import android.widget.Toast; 

public class MainActivity extends Activity implements OnClickListener {
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Context mContext;
    private EditText mEditText1;
    
    private String mClientId;
    private String mClientSecret;
    private String mAccessToken;
    private ErrorCode mError;
    
    private List<SmartDevice> devices = null;

    /**
     * Local OAuthに使用するスコープ一覧.
     */
    private String[] scopes = {
        AuthorizationProfileConstants.PROFILE_NAME,
        BatteryProfileConstants.PROFILE_NAME,
        ConnectProfileConstants.PROFILE_NAME,
        DeviceOrientationProfileConstants.PROFILE_NAME,
        FileDescriptorProfileConstants.PROFILE_NAME,
        FileProfileConstants.PROFILE_NAME,
        MediaPlayerProfileConstants.PROFILE_NAME,
        MediaStreamRecordingProfileConstants.PROFILE_NAME,
        NetworkServiceDiscoveryProfileConstants.PROFILE_NAME,
        NotificationProfileConstants.PROFILE_NAME,
        PhoneProfileConstants.PROFILE_NAME,
        ProximityProfileConstants.PROFILE_NAME,
        SettingsProfileConstants.PROFILE_NAME,
        SystemProfileConstants.PROFILE_NAME,
        VibrationProfileConstants.PROFILE_NAME,

        // 独自プロファイル
        "light",
        "camera",
        "temperature",
        "dice",
        "sphero",
        "drive_controller",
        "remote_controller",
        "mhealth",

        // テスト用
        "*"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this.getBaseContext();
        mEditText1 = (EditText) findViewById(R.id.editText1);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
    }
    
    public void setTextField(String text) {
        mEditText1.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.button1:
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "http://localhost:4035/gotapi/system";
                        HttpGet mMethod = new HttpGet(url);
                        DefaultHttpClient mClient = new DefaultHttpClient();
                        HttpResponse response = mClient.execute(mMethod);

                        final String mResult = EntityUtils.toString(response.getEntity(), "UTF-8");

                        Looper.prepare();
                        Toast.makeText(mContext, mResult, Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mEditText1.setText(mResult);
                            }
                        });
                        Looper.loop();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })).start();
            break;            
        case R.id.button2:
            String host = "localhost";
            int port = 4035;
            String appName = getResources().getString(R.string.app_name);
            AuthProcesser.asyncAuthorize(host, port, false, getPackageName(), appName, scopes, mAuthHandler);
            break;
        case R.id.button3:
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                    if (devices != null) {
                        devices = null;
                    }
                    devices = new ArrayList<SmartDevice>();

                    try {
                        URIBuilder builder = new URIBuilder();
                        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
                        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
                        builder.setScheme("http");
                        builder.setHost("localhost");
                        builder.setPort(4035);
                        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, mAccessToken);

                        HttpUriRequest request = new HttpGet(builder.build());
                        DefaultHttpClient mClient = new DefaultHttpClient();
                        HttpResponse response = mClient.execute(request);
                        message = (new HttpMessageFactory()).newDConnectMessage(response);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    
                    if (message == null) {
                        return;
                    }

                    int result = message.getInt(DConnectMessage.EXTRA_RESULT);
                    if (result == DConnectMessage.RESULT_ERROR) {
                        return;
                    }

                    List<Object> services = message.getList(
                            NetworkServiceDiscoveryProfileConstants.PARAM_SERVICES);
                   final StringBuffer sb = new StringBuffer();
                    if (services != null) {
                        for (Object object: services) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> service = (Map<String, Object>) object;
                            SmartDevice device = new SmartDevice(
                                service.get(NetworkServiceDiscoveryProfileConstants.PARAM_ID).toString(),
                                service.get(NetworkServiceDiscoveryProfileConstants.PARAM_NAME).toString());
                            devices.add(device);
                            sb.append("id:" + service.get(NetworkServiceDiscoveryProfileConstants.PARAM_ID).toString() + ", ");
                            sb.append("name:" + service.get(NetworkServiceDiscoveryProfileConstants.PARAM_NAME).toString() + ", ");
                        }
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mEditText1.setText(sb);
                        }
                    });
                }
            })).start();
            break;
        case R.id.button4:
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    DConnectMessage message = new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);

                    URIBuilder uriBuilder = new URIBuilder();
                    uriBuilder.setProfile(SystemProfileConstants.PROFILE_NAME);
                    uriBuilder.setAttribute(SystemProfileConstants.ATTRIBUTE_DEVICE);
                    uriBuilder.setScheme("http");
                    uriBuilder.setHost("localhost");
                    uriBuilder.setPort(4035);
                    uriBuilder.addParameter(DConnectMessage.EXTRA_DEVICE_ID, devices.get(0).getId());
                    uriBuilder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, mAccessToken);

                    try {
                        HttpUriRequest req = new HttpGet(uriBuilder.build());
                        DefaultHttpClient mCli = new DefaultHttpClient();
                        HttpResponse res = mCli.execute(req);
                        final String mResult = EntityUtils.toString(res.getEntity(), "UTF-8");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mEditText1.setText(mResult);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    
                    int result = message.getInt(DConnectMessage.EXTRA_RESULT);
                    if (result == DConnectMessage.RESULT_ERROR) {
                        return;
                    }                    
                }
            })).start();
            break;
        }
    }

    /**
     * Local OAuthのリスナー.
     */
    private AuthorizationHandler mAuthHandler =  new AuthorizationHandler() {
        @Override
        public void onAuthorized(final String clientId, final String clientSecret, final String accessToken) {
            mClientId = clientId;
            mClientSecret = clientSecret;
            mAccessToken = accessToken;
            mError = null;
            
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Success. AccessToken=" + mAccessToken + " ClientId=" + clientId + " ClientSecret=" + clientSecret, Toast.LENGTH_LONG).show();
                    mEditText1.setText("Success. AccessToken=" + mAccessToken + " ClientId=" + clientId + " ClientSecret=" + clientSecret);
                    
                }
            });
        }
        @Override
        public void onAuthFailed(final ErrorCode error) {
            mError = error;

            mClientId = null;
            mClientSecret = null;
            mAccessToken = null;

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };    

}
