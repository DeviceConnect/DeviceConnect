package org.deviceconnect.android.localoauth.fragment;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.R;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.localoauth.ScopeUtil;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteClient;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteToken;
import org.restlet.ext.oauth.PackageInfoOAuth;
import org.restlet.ext.oauth.internal.Scope;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

/**
 * アクセストークン一覧を表示するFragment.
 * @author NTT DOCOMO, INC.
 */
public class AccessTokenListFragment extends Fragment {
    /** リストビュー用アダプタ. */
    private AccessTokenListAdapter mListAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        mListAdapter = new AccessTokenListAdapter(getActivity(), 0, loadTokens());

        View view = inflater.inflate(R.layout.access_token_list_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mListAdapter);

        View commentView = view.findViewById(R.id.noTokenView);
        if (mListAdapter.getCount() == 0) {
            commentView.setVisibility(View.VISIBLE);
        } else {
            commentView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        // ActionBarの全削除ボタンを追加
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
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(strTitle)
        .setMessage(strGuidance)
        .setPositiveButton(strPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                mListAdapter.deleteAll();
            }
        })
        .setNegativeButton(strNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
            }
        })
        .setCancelable(true);
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * アクセストークン読み込み.
     * @return アクセストークンのリスト
     */
    private List<SQLiteToken> loadTokens() {
        ArrayList<SQLiteToken> tokenList = new ArrayList<SQLiteToken>();
        SQLiteToken[] tokens = LocalOAuth2Main.getAccessTokens();
        if (tokens != null) {
            for (SQLiteToken token : tokens) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }

    /**
     * トークンデータがありませんのViewの表示・非表示を設定する.
     */
    private void setVisibleCommentView() {
        View commentView = getView().findViewById(R.id.noTokenView);
        if (mListAdapter.getCount() == 0) {
            commentView.setVisibility(View.VISIBLE);
        } else {
            commentView.setVisibility(View.GONE);
        }
    }

    /**
     * アクセストークンListView用Adapter.
     */
    private class AccessTokenListAdapter extends ArrayAdapter<SQLiteToken> {
        /** LayoutInflator. */
        private LayoutInflater mInflater;
        /** トークン配列. */
        private List<SQLiteToken> mTokenList;

        /**
         * コンストラクタ.
         * @param context コンストラクタ
         * @param textViewResourceId textViewResourceId
         * @param tokens トークン配列
         */
        public AccessTokenListAdapter(final Context context, final int textViewResourceId,
                final List<SQLiteToken> tokens) {
            super(context, textViewResourceId, tokens);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTokenList = tokens;
        }

        @Override
        public int getCount() {
            return mTokenList.size();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final SQLiteToken token = (SQLiteToken) getItem(position);

            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.access_token_list_item, (ViewGroup) null);
            }

            // アプリケーション名
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(getApplicationName(token));

            // スコープ名一覧を表示
            TextView textViewAccessToken = (TextView) view.findViewById(R.id.textViewAccessToken);
            textViewAccessToken.setText(getScopeList(token));

            // アプリのアイコン表示
            ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
            imageViewIcon.setImageDrawable(getApplicationIcon(token));

            // 行背景をクリックしたときの処理
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    openAccessTokenDescription(token);
                }
            });

            // 削除ボタン
            Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    openDeleteTokenDialog(token);
                }
            });

            return view;
        }

        /**
         * アクセストークンをすべて削除されたことを画面に反映する.
         */
        private void deleteAll() {
            LocalOAuth2Main.destroyAllAccessToken();
            mTokenList.clear();
            notifyDataSetChanged();
            setVisibleCommentView();
        }
        /**
         * アクセストークンを削除する.
         * @param token トークンデータ
         */
        private void deleteToken(final SQLiteToken token) {
            long tokenId = token.getId();
            LocalOAuth2Main.destroyAccessToken(tokenId);
            mTokenList.remove(token);
            notifyDataSetChanged();
            setVisibleCommentView();
        }
    }

    /**
     * トークンデータからアプリケーション名を取得する.
     * @param token トークンデータ
     * @return アプリケーション名
     */
    private String getApplicationName(final SQLiteToken token) {
        if (token != null) {
            String applicationName = token.getApplicationName();
            if (applicationName == null) {
                applicationName = getString(R.string.no_application_name);
            }
           return applicationName;
        } else {
            return getString(R.string.no_token);
        }
    }

    /**
     * トークンデータからアプリケーションアイコンを取得する.
     * <p>
     * アイコンが存在しない場合には、デフォルトの画像を返却する。
     * </p>
     * @param token トークンデータ
     * @return アイコン画像
     */
    private Drawable getApplicationIcon(final SQLiteToken token) {
        Drawable icon = null;
        if (token != null) {
            String clientId = token.getClientId();
            SQLiteClient client = LocalOAuth2Main.findClientByClientId(clientId);
            if (client != null) {
                PackageInfoOAuth p = client.getPackageInfo();
                icon = getPackageIcon(getActivity(), p.getPackageName());
            }
        }
        if (icon == null) {
            icon = getResources().getDrawable(R.drawable.ic_action_labels);
        }
        return icon;
    }

    /**
     * パッケージアイコンを返す.
     * @param context コンテキスト
     * @param packageName パッケージ名
     * @return not null: パッケージアイコン / null: パッケージが見つからない
     */
    private Drawable getPackageIcon(final Context context, final String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            return pm.getApplicationIcon(packageName);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * トークンデータからスコープ一覧の文字列を取得する.
     * @param token トークンデータ
     * @return スコープ一覧の文字列
     */
    private String getScopeList(final SQLiteToken token) {
        if (token != null) {
            Scope[] scopes = token.getScope(); 
            if (scopes != null && scopes.length > 0) {
                String firstScopeName = ScopeUtil.getDisplayScope(getActivity(),
                        scopes[0].getScope(), null, null);
                if (scopes.length == 1) {
                    return firstScopeName;
                } else {
                    String format = getString(R.string.access_token_list_item_format);
                    return String.format(format, firstScopeName, scopes.length - 1);
                }
            }
        }
        return "";
    }

    /**
     * トークンデータの詳細を表示する.
     * @param token トークンデータ
     */
    private void openAccessTokenDescription(final SQLiteToken token) {
        AccessTokenDescriptionFramgent f = new AccessTokenDescriptionFramgent();
        Bundle bundle = new Bundle();
        bundle.putString(AccessTokenDescriptionFramgent.EXTRA_CLIENT_ID, 
                token.getClientId());
        f.setArguments(bundle);
        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        t.add(android.R.id.content, f, "description");
        t.addToBackStack("tokenList");
        t.commitAllowingStateLoss();
    }

    /**
     * トークンデータの削除確認ダイアログを表示する.
     * @param token 削除するトークンデータ
     */
    private void openDeleteTokenDialog(final SQLiteToken token) {
        String applicationName = token.getApplicationName();

        String strTitle = applicationName;
        String strGuidance = getString(R.string.access_token_delete_guidance);
        String strPositive = getString(R.string.access_token_delete_positive);
        String strNegative = getString(R.string.access_token_delete_negative);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(strTitle).setMessage(strGuidance)
                .setPositiveButton(strPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mListAdapter.deleteToken(token);
                    }
                }).setNegativeButton(strNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                }).setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
