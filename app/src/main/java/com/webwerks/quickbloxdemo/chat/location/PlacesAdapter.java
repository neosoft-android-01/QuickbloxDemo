package com.webwerks.quickbloxdemo.chat.location;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.webwerks.quickbloxdemo.model.ShareLocationModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by webwerks on 4/5/17.
 */

public class PlacesAdapter extends ArrayAdapter</*HashMap<String, String>*/ShareLocationModel> {

    Context mContext;

    public PlacesAdapter(@NonNull Context context,@NonNull List</*HashMap<String, String>*/ShareLocationModel> objects) {
        super(context, 0, objects);
        mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2,null);
        }

        TextView lblPlace= (TextView) convertView.findViewById(android.R.id.text1);
        TextView lblAddress=(TextView) convertView.findViewById(android.R.id.text2);

        /*HashMap<String, String> googlePlace = getItem(position);
        String placeName = googlePlace.get("place_name");*/
        lblPlace.setText(getItem(position).getLocationName());
        lblAddress.setText(getItem(position).getLocationDesc());

        return convertView;
    }
}
