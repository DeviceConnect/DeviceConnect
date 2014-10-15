/*
 DrawerListFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.client.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ドロワーリストフラグメント.
 */
public class DrawerListFragment extends ListFragment {

    /**
     * データリスト.
     */
    private List<Object> mDataList = new ArrayList<Object>();

    /**
     * リストアダプター.
     */
    private ArrayAdapter<Object> mListAdapter = null;

    /**
     * リストアイテムクリックリスナー.
     */
    private OnDataListSelectedListener mListListener = null;

    /**
     * フラグメントを生成する.
     * @param devices デバイスリスト
     * @return フラグメント
     */
    public static DrawerListFragment newInstance(final List<Object> devices) {
        DrawerListFragment fragment = new DrawerListFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new ArrayAdapter<Object>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mDataList);
        setListAdapter(mListAdapter);
    }

    @Override
    public void onListItemClick(final ListView parent,
            final View view, final int position, final long id) {
        super.onListItemClick(parent, view, position, id);
        if (mListListener != null) {
            mListListener.onDataListSelected(parent, view, position, id);
        }
    }

    /**
     * データリストを設定する.
     * @param data データリスト
     */
    public void setDataList(final List<? extends Object> data) {

        mListAdapter.clear();

        if (data != null && data.size() > 0) {

            List<String> titleList = new ArrayList<String>();
            for (Object obj : data) {
                if (obj instanceof Fragment && ((Fragment) obj).getArguments() != null) {
                    titleList.add(((Fragment) obj).getArguments()
                            .getString(Intent.EXTRA_TITLE, obj.toString()));
                } else {
                    titleList.add(obj.toString());
                }
            }
            mListAdapter.addAll(titleList);
            mListAdapter.notifyDataSetChanged();

        }

    }

    /**
     * データリスト選択リスナーを設定する.
     * @param listener リスナー
     */
    public void setDataListSelectedListener(final OnDataListSelectedListener listener) {
        mListListener = listener;
    }

    /**
     * データリスト選択リスナー.
     */
    public interface OnDataListSelectedListener {
        /**
         * データリスト選択ハンドラ.
         * @param parent 親View
         * @param view 選択されたView
         * @param position 選択されたアイテムの位置
         * @param id 選択されたアイテムのID
         */
         void onDataListSelected(ListView parent, View view, int position, long id);
    }

}
