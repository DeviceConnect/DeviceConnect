package com.nttdocomo.android.dconnect.deviceplugin.chromecast.setting;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.nttdocomo.android.dconnect.deviceplugin.chromecast.ChromeCastService;
import com.nttdocomo.android.dconnect.deviceplugin.chromecast.R;
import com.nttdocomo.android.dconnect.deviceplugin.util.ChromeCastApplication;
import com.nttdocomo.android.dconnect.ui.activity.DConnectSettingPageFragmentActivity;

/**
 * チュートリアル画面（ステップ）
 * <p>
 * 画面を作成する
 * </p>
 * 
 */
public class ChromeCastSettingStepsActivity extends DConnectSettingPageFragmentActivity {
	
	private List<BaseFragment> fragments = new ArrayList<BaseFragment>();
    
    ChromeCastService chromeCastService = null;
    ChromeCastApplication application = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        ViewPager vp = getViewPager();
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(final int state) {}
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {}
            @Override
            public void onPageSelected(final int position) {}
        });
    }

    @Override
    public int getPageCount() {
        return 3;
    }

    @Override
    public Fragment createPage(final int position) {
        if (fragments.size() == 0) {
            fragments.add(new FragmentPage1());
            fragments.add(new FragmentPage2());
            fragments.add(new FragmentPage3());
        }
        return fragments.get(position);
    }
    
    /**
     * 指定されたページに遷移する
     * 
     * @param	position
     * @return	なし
     */
    public void setCurrentPage(final int position) {
        getViewPager().setCurrentItem(position, true);
    }
    
    /**
     * フラグメント（ベース）
     */
    public static class BaseFragment extends Fragment {
        
        ChromeCastSettingStepsActivity mActivity;
        
        public void setActivity(final ChromeCastSettingStepsActivity activity) {
            mActivity = activity;
        }
    }

    /**
     * フラグメント (Page1)
     */
    private class FragmentPage1 extends BaseFragment {
    	
    	@Override
        public void onStart() {
            super.onStart();
        }
     
        @Override
        public void onStop() {
            super.onStop();
        }
    	
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_1, container, false);
            return root;
        }
    }
    
    /**
     * フラグメント (Page2)
     */
    private class FragmentPage2 extends BaseFragment {
    	
    	@Override
        public void onStart() {
            super.onStart();
        }
     
        @Override
        public void onStop() {
            super.onStop();
        }
        
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) { 
            View root = inflater.inflate(R.layout.dconnect_settings_step_2, container, false);
            return root;
        }
    }
    
    /**
     * フラグメント (Page3)
     */
    private class FragmentPage3 extends BaseFragment {
    	
    	@Override
        public void onStart() {
            super.onStart();
        }
     
        @Override
        public void onStop() {
            super.onStop();
        }
    	
        @Override
        public View onCreateView(final LayoutInflater inflater, 
                final ViewGroup container, final Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dconnect_settings_step_3, container, false);
            
            Button button = (Button) root.findViewById(R.id.buttonChromecastSettingApp);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {  
                	String packageName = "com.google.android.apps.chromecast.app";
                	try{
                		Intent intent = container.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                		startActivity(intent);
                	}catch(Exception e){
                		Uri uri = Uri.parse("market://details?id=" + packageName);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                		e.printStackTrace();
                	}
                }
            });
            
            return root;
        }
    }
}
