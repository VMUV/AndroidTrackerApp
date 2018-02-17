package com.clokey.shasta.motusapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shasta on 2/17/2018.
 */

public class PairedDevicesAdapter extends ArrayAdapter
{
    public PairedDevicesAdapter(Activity context, ArrayList<PairedDevice> pairedDevices)
    {
        super(context, 0, pairedDevices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        PairedDevice currentPairedDevice = (PairedDevice) getItem(position);

        TextView deviceName = listItemView.findViewById(R.id.device_name);
        deviceName.setText(currentPairedDevice.getDeviceName());

        TextView deviceMacAddress = listItemView.findViewById(R.id.device_mac_address);
        deviceMacAddress.setText(currentPairedDevice.getMacAddress());

        return listItemView;
    }

}
