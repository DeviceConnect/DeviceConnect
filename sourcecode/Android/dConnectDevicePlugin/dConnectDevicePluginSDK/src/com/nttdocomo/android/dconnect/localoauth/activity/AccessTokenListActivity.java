/*
 AccessTokenListActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth.activity;

import java.util.ArrayList;
import java.util.List;

import org.restlet.ext.oauth.PackageInfoOAuth;
import org.restlet.ext.oauth.internal.Scope;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.R;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.localoauth.ScopeUtil;
import com.nttdocomo.android.dconnect.localoauth.oauthserver.db.SQLiteClient;
import com.nttdocomo.android.dconnect.localoauth.oauthserver.db.SQLiteToken;


/**
 * アクセストークン一覧Activity.
 * @author NTT DOCOMO, INC.
 */
public class AccessTokenListActivity extends Activity {
    
    /** デフォルトのアイコン. */
    private static final int LIST_ITEM_DEFAULT_ICON_ID = R.drawable.ic_action_labels;
    
    /** 読み込んだトークン配列. */
    private List<SQLiteToken> mTokens = new ArrayList<SQLiteToken>();
    
    /** リストビュー用アダプタ. */
    private AccessTokenListAdapter mListAdapter = null; 
    
    /** リストビュー. */
    private ListView mListView = null;
    
    /** アクセストークン詳細画面. */
    private View mAccessTokenItemLayout = null;
    
    /** アクセストークン詳細画面内のスコープリストビュー. */
    private ListView mScopeListView = null;
    
    /** アクセストークン詳細画面内のスコープリストビュー用アダプタ. */
    private ScopeListAdapter mScopeListAdater = null;
    
    /** アクセストークン詳細画面に表示するスコープ配列. */
    private List<Scope> mScopes = new ArrayList<Scope>();
    
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_token_list_activity);
        
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        setTitle(R.string.access_token);
        
        mListView = (ListView) findViewById(R.id.listView);
        mAccessTokenItemLayout = findViewById(R.id.accessTokenItemLayout);
        
        
        /* アクセストークン読み込み */
        loadTokens();
        
        /* アクセストークンListView設定 */
        mListAdapter = new AccessTokenListAdapter(this, 0, mTokens);
        mListView.setAdapter(mListAdapter);
        
        /* アクセストークン詳細画面のスコープリストビュー */
        mScopeListView = (ListView) findViewById(R.id.listViewScope);
        mScopeListAdater = new ScopeListAdapter(this, 0, mScopes);
        mScopeListView.setAdapter(mScopeListAdater);
        
        
        /* アクセストークン詳細画面のOKボタン押下時の処理 */
        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mAccessTokenItemLayout.getVisibility() == View.VISIBLE) {
                    mAccessTokenItemLayout.setVisibility(View.GONE);
                }
            }
        });
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        
        /* ActionBarの全削除ボタンを追加 */
        final MenuItem menuItem = menu.add(R.string.delete_all);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                if (item.getTitle().equals(menuItem.getTitle())) {
                    deleteAllTokenProc();
                }
                return true;
            }
        });
        
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    /**
     * アクセストークン読み込み.
     */
    private void loadTokens() {
        
        /* トークンデータ読み込み */
        SQLiteToken[] tokens = LocalOAuth2Main.getAccessTokens();
        
        /* リスト表示用配列にデータを移す */
        mTokens.clear();
        if (tokens != null) {
            for (SQLiteToken token : tokens) {
                mTokens.add(token);
            }
        } else {
            /* 「トークンはありません」と表示するためのデータ */
            mTokens.add(null);
        }
    }
    
    /**
     * アクセストークンを全て削除する処理.
     */
    private void deleteAllTokenProc() {
        
        String strTitle = getString(R.string.access_token_delete_all_title);
        String strGuidance = getString(R.string.access_token_delete_guidance);
        String strPositive = getString(R.string.access_token_delete_positive);
        String strNegative = getString(R.string.access_token_delete_negative);
        
        /* 削除確認ダイアログ */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccessTokenListActivity.this);
        alertDialogBuilder
        .setTitle(strTitle)
        .setMessage(strGuidance)
        .setPositiveButton(strPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                
                /* アクセストークンを全て削除 */
                deleteAllToken();
            }
        })
        .setNegativeButton(strNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                /* ダイアログを閉じる */
            }
        })
        .setCancelable(true);
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        
        
    }
    
    
    /**
     * アクセストークンを全て削除.
     */
    private void deleteAllToken() {
        
        /* DBのトークンデータを削除 */
        LocalOAuth2Main.destroyAllAccessToken();
        
        /* 配列から全件削除 */
        mTokens.clear();
        
        /* 「トークンはありません」と表示するためのデータを追加する */
        mTokens.add(null);
        
        /* 表示更新 */
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * アクセストークンListView用Adapter.
     */
    private class AccessTokenListAdapter extends ArrayAdapter<SQLiteToken> {
        
        /** LayoutInflator. */
        private LayoutInflater mLayoutInflater;
        
        /** トークン配列. */
        private List<SQLiteToken> mTokens;
        
        /**
         * コンストラクタ.
         * @param context コンストラクタ
         * @param textViewResourceId textViewResourceId
         * @param tokens トークン配列
         */
        public AccessTokenListAdapter(final Context context, final int textViewResourceId,
                final List<SQLiteToken> tokens) {
            super(context, textViewResourceId, tokens);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTokens = tokens;
        }
        
        @Override
        public int getCount() {
            return mTokens.size();
        }
        
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // 特定の行(position)のデータを得る
            final SQLiteToken token = (SQLiteToken) getItem(position);
            
            // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.access_token_list_item, null);
            }
            
            SQLiteClient client = null;
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            if (token != null) {
                
                String clientId = token.getClientId();
                client = LocalOAuth2Main.findClientByClientId(clientId);
                String applicationName = token.getApplicationName();
                if (applicationName == null) {
                    applicationName = getString(R.string.no_application_name);
                }
                textView.setText(applicationName);
            } else {
                textView.setText(getString(R.string.no_token));
            }
            
            /* スコープ名一覧を表示する */
            String displayScopes = "";
            if (token != null) {
                Scope[] scopes = token.getScope(); 
                if (scopes != null && scopes.length > 0) {
                    String firstScopeName = ScopeUtil.getDisplayScope(AccessTokenListActivity.this,
                            scopes[0].getScope(), null, null);
                    if (scopes.length == 1) {
                        /* スコープが1件だけの場合は1件分のスコープ名を表示する */
                        displayScopes = firstScopeName;
                    } else {
                        /* 2件以上ある場合は「ファイル 他２件」のようにスコープ名1件とその他の件数を表示する */
                        String format = getString(R.string.access_token_list_item_format);
                        displayScopes = String.format(format, firstScopeName, scopes.length - 1);
                    }
                }
            }
            TextView textViewAccessToken = (TextView) convertView.findViewById(R.id.textViewAccessToken);
            textViewAccessToken.setText(displayScopes);
            
            /* アプリのアイコン表示 */
            ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            Drawable icon = null;
            if (client != null) {
                PackageInfoOAuth p = client.getPackageInfo();
                icon = getPackageIcon(AccessTokenListActivity.this, p.getPackageName());
            }
            if (icon != null) {
                imageViewIcon.setImageDrawable(icon);
            } else {
                imageViewIcon.setImageResource(LIST_ITEM_DEFAULT_ICON_ID);
            }
            
            /* 行背景をクリックしたときの処理 */
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    
                    /* 「トークンはありません」行をタップしたときは何もしないで終了する */
                    if (token == null) {
                        return;
                    }
                    
                    /* 非表示中ならポップアップ画面を表示する */
                    if (mAccessTokenItemLayout.getVisibility() == View.GONE) {
                        
                        /* アプリケーション名設定 */
                        TextView textViewApplicationName = (TextView) mAccessTokenItemLayout
                                .findViewById(R.id.textViewApplicationName);
                        textViewApplicationName.setText(token.getApplicationName());
                        
                        /* スコープ名一覧表示 */
                        mScopes.clear();
                        Scope[] scopes = token.getScope();
                        if (scopes != null) {
                            for (Scope scope : scopes) {
                                mScopes.add(scope);
                            }
                        }
                        mScopeListAdater.notifyDataSetChanged();
                        
                        /* ポップアップ画面を表示する */
                        mAccessTokenItemLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            
            
            Button buttonDelete = (Button) convertView.findViewById(R.id.buttonDelete);
            if (token != null) {
                buttonDelete.setVisibility(View.VISIBLE);
            } else {
                buttonDelete.setVisibility(View.GONE);
            }
            buttonDelete.setTag(position);
            buttonDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (0 <= position && position < mTokens.size()) {
                        String applicationName = mTokens.get(position).getApplicationName();
                    
                        String strTitle = applicationName;
                        String strGuidance = getString(R.string.access_token_delete_guidance);
                        String strPositive = getString(R.string.access_token_delete_positive);
                        String strNegative = getString(R.string.access_token_delete_negative);
                        
                        /* 削除確認ダイアログ */
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccessTokenListActivity.this);
                        alertDialogBuilder
                        .setTitle(strTitle)
                        .setMessage(strGuidance)
                        .setPositiveButton(strPositive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                
                                /* トークン削除 */
                                deleteToken(position);
                            }
                        })
                        .setNegativeButton(strNegative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                /* ダイアログを閉じる */
                            }
                        })
                        .setCancelable(true);
                        
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                
                /**
                 * アクセストークン削除.
                 * @param position ポジション
                 */
                private void deleteToken(final int position) {
                    
                    if (0 <= position && position < mTokens.size()) {
                        long tokenId = mTokens.get(position).getId();
                        
                        /* DBのトークンデータを削除 */
                        LocalOAuth2Main.destroyAccessToken(tokenId);
                        
                        /* 配列から1件削除 */
                        mTokens.remove(position);
                        
                        /* 0件になったら「トークンはありません」と表示するためのデータを追加する */
                        if (mTokens.size() <= 0) {
                            mTokens.add(null);
                        }
                        
                        /* 表示更新 */
                        notifyDataSetChanged();
                    }
                }
            });
            
            return convertView;
        }
    }

    /**
     * パッケージアイコンを返す.
     * @param context コンテキスト
     * @param packageName パッケージ名
     * @return not null: パッケージアイコン / null: パッケージが見つからない
     */
    private Drawable getPackageIcon(final Context context, final String packageName) {
        PackageManager pm = getPackageManager();
        Drawable icon = null;
        try {
            icon = pm.getApplicationIcon(packageName);
        } catch (NameNotFoundException e) {
//         e.printStackTrace();
        }
        return icon;
    }


    /**
     * スコープListView用Adapter.
     */
    private class ScopeListAdapter extends ArrayAdapter<Scope> {
        
        /** LayoutInflator. */
        private LayoutInflater mLayoutInflater;
        
        /** スコープ配列. */
        private List<Scope> mScopes;
        
        /**
         * コンストラクタ.
         * @param context コンストラクタ
         * @param textViewResourceId textViewResourceId
         * @param scopes スコープ配列
         */
        public ScopeListAdapter(final Context context, final int textViewResourceId,
                final List<Scope> scopes) {
            super(context, textViewResourceId, scopes);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mScopes = scopes;
        }
        
        @Override
        public int getCount() {
            return mScopes.size();
        }
        
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // 特定の行(position)のデータを得る
            final Scope scope = (Scope) getItem(position);
            
            // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.access_token_item_scope, null);
            }
            
            /* スコープ名表示(日本語表示できる場合は日本語表示する) */
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            if (scope != null) {
                String strScope = scope.getScope();
                strScope = ScopeUtil.getDisplayScope(AccessTokenListActivity.this, strScope, null, null);
                textView.setText(strScope);
            }
            
            /* 有効期限 */
            TextView textViewExpirePeriod = (TextView) convertView.findViewById(R.id.textViewExpirePeriod);
            if (scope != null) {
                String expirePeriod = scope.getStrExpirePeriod();
                String expirePeriodFormat = getString(R.string.expire_period_date_format);  
                String displayExpirePeriod = String.format(expirePeriodFormat, expirePeriod); 
                textViewExpirePeriod.setText(displayExpirePeriod);
            }
            
            
            return convertView;
        }
    }
}
