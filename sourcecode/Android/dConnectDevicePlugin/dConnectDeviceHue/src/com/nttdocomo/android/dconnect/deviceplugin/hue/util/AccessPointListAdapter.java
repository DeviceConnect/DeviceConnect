package com.nttdocomo.android.dconnect.deviceplugin.hue.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.philips.lighting.hue.sdk.PHAccessPoint;

/**
AccessPointListAdapter
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * This class provides adapter view for a list of Found Bridges.
 * 
 */
public class AccessPointListAdapter extends BaseAdapter {
    /**
     * .
     */
    private LayoutInflater mInflater;
    /**
     * .
     */
    private ArrayList<PHAccessPoint> accessPoints;

    /**
     * View holder class for access point list.
     * 
     */
    class BridgeListItem {
        /**
         * .
         */
        private TextView bridgeIp;
        /**
         * .
         */
        private TextView bridgeMac;
    }

    /**
     * creates instance of {@link AccessPointListAdapter} class.
     * 
     * @param context the Context object.
     * @param accessPointsList an array list of {@link PHAccessPoint} object to
     *            display.
     */
    public AccessPointListAdapter(final Context context, final ArrayList<PHAccessPoint> accessPointsList) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.accessPoints = accessPointsList;
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     * 
     * @param position The row index.
     * @param convertView The row view.
     * @param parent The view group.
     * @return A View corresponding to the data at the specified position.
     */
    @SuppressWarnings("null")
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        BridgeListItem item;
        View convView = convertView;

        if (convertView == null) {
            convView = mInflater.inflate(R.layout.selectbridge_item, null);

            item = new BridgeListItem();
            item.bridgeMac = (TextView) convView.findViewById(R.id.bridge_mac);
            item.bridgeIp = (TextView) convView.findViewById(R.id.bridge_ip);

            convView.setTag(item);
        } else {
            item = (BridgeListItem) convView.getTag();
        }
        PHAccessPoint accessPoint = accessPoints.get(position);
        item.bridgeIp.setTextColor(Color.BLACK);
        item.bridgeIp.setText(accessPoint.getIpAddress());
        item.bridgeMac.setTextColor(Color.DKGRAY);
        item.bridgeMac.setText(accessPoint.getMacAddress());

        return convView;
    }

    /**
     * Get the row id associated with the specified position in the list.
     * 
     * @param position The row index.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(final int position) {
        return 0;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     * 
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return accessPoints.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * 
     * @param position Position of the item whose data we want within the
     *            adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(final int position) {
        return accessPoints.get(position);
    }

    /**
     * Update date of the list view and refresh listview.
     * 
     * @param accessPointsList An array list of {@link PHAccessPoint} objects.
     */
    public void updateData(final ArrayList<PHAccessPoint> accessPointsList) {
        this.accessPoints = accessPointsList;
        notifyDataSetChanged();
    }

}
