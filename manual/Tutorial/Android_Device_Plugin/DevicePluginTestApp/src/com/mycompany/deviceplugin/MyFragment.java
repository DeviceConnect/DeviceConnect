package com.mycompany.deviceplugin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        int position = args.getInt("position", -1);
        View root = inflater.inflate(R.layout.fragment, container, false);
        TextView view = (TextView) root.findViewById(R.id.text);
        position++;
        view.setText("Fragment : " + position);

        return root;
    }
}