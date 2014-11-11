package org.deviceconnect.android.localoauth.fragment;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.R;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.localoauth.ScopeUtil;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteToken;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.Scope;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * アクセストークンの詳細情報を表示するFragment.
 * @author NTT DOCOMO, INC.
 */
public class AccessTokenDescriptionFramgent extends Fragment {

    /** Extra: クライアントID. */
    static final String EXTRA_CLIENT_ID = "clientID";

    /** 表示するトークン. */
    private SQLiteToken mToken;

    /** スコープを表示するアダプタ. */
    private ScopeListAdapter mScopeListAdater;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        String clientId = getArguments().getString(EXTRA_CLIENT_ID);
        Client client = LocalOAuth2Main.findClientByClientId(clientId);
        mToken = LocalOAuth2Main.getAccessToken(client);

        mScopeListAdater = new ScopeListAdapter(getActivity(), 0, getScopeList());

        View view = inflater.inflate(R.layout.access_token_item_fragment, container, false);

        TextView textView = (TextView) view.findViewById(R.id.textViewApplicationName);
        textView.setText(mToken.getApplicationName());

        ListView listView = (ListView) view.findViewById(R.id.listViewScope);
        listView.setAdapter(mScopeListAdater);

        Button okBtn = (Button) view.findViewById(R.id.buttonOk);
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                back();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
    }

    /**
     * スコープリストを取得する.
     * @return スコープリスト
     */
    private List<Scope> getScopeList() {
        List<Scope> scopeList = new ArrayList<Scope>();
        Scope[] scopes = mToken.getScope();
        if (scopes != null) {
            for (Scope scope : scopes) {
                scopeList.add(scope);
            }
        }
        return scopeList;
    }

    /**
     * 一つ前のFragmentに戻る.
     */
    private void back() {
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack();
    }

    /**
     * スコープListView用Adapter.
     */
    private class ScopeListAdapter extends ArrayAdapter<Scope> {
        /** LayoutInflator. */
        private LayoutInflater mInflater;
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
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mScopes = scopes;
        }

        @Override
        public int getCount() {
            return mScopes.size();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            Scope scope = (Scope) getItem(position);

            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.access_token_item_scope, (ViewGroup) null);
            }

            // スコープ名表示(日本語表示できる場合は日本語表示する)
            TextView textView = (TextView) view.findViewById(R.id.textView);
            if (scope != null) {
                String strScope = scope.getScope();
                strScope = ScopeUtil.getDisplayScope(getActivity(), strScope, null, null);
                textView.setText(strScope);
            }

            /* 有効期限 */
            TextView textViewExpirePeriod = (TextView) view.findViewById(R.id.textViewExpirePeriod);
            if (scope != null) {
                String expirePeriod = scope.getStrExpirePeriod();
                String expirePeriodFormat = getString(R.string.expire_period_date_format);  
                String displayExpirePeriod = String.format(expirePeriodFormat, expirePeriod); 
                textViewExpirePeriod.setText(displayExpirePeriod);
            }

            return view;
        }
    }
}
