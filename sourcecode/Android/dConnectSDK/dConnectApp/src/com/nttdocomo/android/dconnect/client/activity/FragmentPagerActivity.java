package com.nttdocomo.android.dconnect.client.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.nttdocomo.android.dconnect.client.fragment.DrawerListFragment;
import com.nttdocomo.android.dconnect.client.fragment.FragmentViewPager;
import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.activity.SettingsActivity;

/**
 * フラグメントページャーアクティビティ.
 */
public abstract class FragmentPagerActivity extends FragmentActivity {

    /**
     * ドロワーレイアウト.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * アクションバードロワートグル.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // initialize drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(final View view) {
                super.onDrawerClosed(view);
                FragmentPagerActivity.this.onDrawerClosed(view);
            }
            @Override
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
                FragmentPagerActivity.this.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // init drawer
        DrawerListFragment drawer = (DrawerListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_drawer_fragment);
        drawer.setDataListSelectedListener(new DrawerListFragment.OnDataListSelectedListener() {
            @Override
            public void onDataListSelected(final ListView parent,
                    final View view, final int position, final long id) {
                ((FragmentViewPager) findViewById(
                        R.id.activity_main_pager)).setCurrentItem(position);
                getSupportFragmentManager().popBackStack();
                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * ドロワークローズイベントハンドラ.
     * @param view ビュー
     */
    protected void onDrawerClosed(final View view) {
        invalidateOptionsMenu();

        Fragment fragment = ((FragmentViewPager) findViewById(
                R.id.activity_main_pager)).getCurrentFragment();
        if (fragment != null && ((Fragment) fragment).getArguments() != null) {
            getActionBar().setTitle(fragment.getArguments().getString(
                    Intent.EXTRA_TITLE, fragment.toString()));
        } else if (fragment != null) {
            getActionBar().setTitle(fragment.toString());
        } else {
            getActionBar().setTitle(R.string.app_name);
        }
    }

    /**
     * ドロワーオープンイベントハンドラ.
     * @param drawerView ビュー
     */
    protected void onDrawerOpened(final View drawerView) {
        getActionBar().setTitle(R.string.app_name);
        invalidateOptionsMenu();
    }

    /**
     * フラグメントリストをクリアする.
     */
    protected void clearFragmentList() {
        setFragmentList(null);
    }

    /**
     * フラグメントリストを設定する.
     * @param fragments フラグメントリスト
     */
    protected void setFragmentList(final List<Fragment> fragments) {
        // init view pager
        ((FragmentViewPager) findViewById(R.id.activity_main_pager)).setFragmentList(fragments);
        ((DrawerListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_drawer_fragment)).setDataList(fragments);
    }
}
