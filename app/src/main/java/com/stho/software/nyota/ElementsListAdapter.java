package com.stho.software.nyota;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.utilities.Angle;
import com.stho.software.nyota.utilities.Topocentric;

import java.util.ArrayList;

/**
 * Created by shoedtke on 08.09.2016.
 */
public class ElementsListAdapter extends ArrayAdapter<IElement> {

    private final LayoutInflater inflater;

    private static class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView azimuth;
        private TextView altitude;
    }


    public ElementsListAdapter(Context context, ArrayList<IElement> elements) {
        super(context, -1, elements);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // ViewHolder pattern:
        // see http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance_problems
        // https://developer.android.com/training/improving-layouts/smooth-scrolling.html
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.element_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.listItemIcon);
            holder.name = (TextView) convertView.findViewById(R.id.listItemName);
            holder.azimuth = (TextView) convertView.findViewById(R.id.listItemAzimuth);
            holder.altitude = (TextView) convertView.findViewById(R.id.listItemAltitude);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        IElement element = super.getItem(position);
        if (element != null) {
            holder.icon.setImageResource(element.getImageId());
            holder.name.setText(element.getName());

            Topocentric topocentric = element.getPosition();
            if (topocentric != null) {
                holder.azimuth.setText(Angle.toString(topocentric.azimuth, Angle.AngleType.AZIMUTH));
                holder.altitude.setText(Angle.toString(topocentric.altitude, Angle.AngleType.ALTITUDE));
            }
        }
        return convertView;
    }
}
