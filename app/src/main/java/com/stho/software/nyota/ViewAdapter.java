package com.stho.software.nyota;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stho.software.nyota.utilities.IIconNameValue;
import com.stho.software.nyota.utilities.IIconValue;

import java.util.ArrayList;

/**
 * Created by shoedtke on 04.10.2016.
 */
abstract class ViewAdapter<H extends ViewAdapter.BasicsViewHolder> {

    public static class BasicsViewHolder {
        ImageView icon;
        TextView value;
    }

    public static class DetailsViewHolder extends BasicsViewHolder {
        TextView name;
    }

    protected LinearLayout container;
    protected LinearLayout.LayoutParams params;
    protected LayoutInflater inflater;
    protected SparseArray<H> views = new SparseArray<H>();

    protected ViewAdapter(Activity activity, int containerResId) {
        container = (LinearLayout) activity.findViewById(containerResId);
        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 0);
    }

    boolean prepare(int count) {
        if (views.size() != count) {
            clear();
            return true;
        } else {
            return false;
        }
    }

    void clear() {
        views.clear();
        container.removeAllViews();
    }
}

final class BasicsViewAdapter extends ViewAdapter<ViewAdapter.BasicsViewHolder> {

    BasicsViewAdapter(Activity activity, int containerResId) {
        super(activity, containerResId);
    }

    BasicsViewHolder getViewHolder(int index) {
        BasicsViewHolder holder = views.get(index);
        if (holder == null) {
            View view = inflater.inflate(R.layout.icon_value_list_item, container, false);
            container.addView(view, params);
            holder = new DetailsViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.listItemIcon);
            holder.value = (TextView) view.findViewById(R.id.listItemValue);
            views.put(index, holder);
        }
        return holder;
    }

    void update(ArrayList<IIconValue> items) {

        prepare(items.size());

        for (IIconValue item : items)
            update(items.indexOf(item), item);
    }

    void update(int index, IIconValue item) {
        update(getViewHolder(index), item.getImageId(), item.getValue());
    }

    void update(int index, int imageId, String value) {
        update(getViewHolder(index), imageId, value);
    }

    private static void update(BasicsViewHolder holder, int imageId, String value) {
        holder.icon.setImageResource(imageId);
        holder.value.setText(value);
    }
}

final class DetailsViewAdapter extends ViewAdapter<ViewAdapter.DetailsViewHolder> {

    DetailsViewAdapter(Activity activity, int containerResId) {
        super(activity, containerResId);
    }

    private DetailsViewHolder getViewHolder(int index) {
        DetailsViewHolder holder = views.get(index);
        if (holder == null) {
            View view = inflater.inflate(R.layout.icon_name_value_list_item, container, false);
            container.addView(view, params);
            holder = new DetailsViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.listItemIcon);
            holder.name = (TextView) view.findViewById(R.id.listItemName);
            holder.value = (TextView) view.findViewById(R.id.listItemValue);
            views.put(index, holder);
        }
        return holder;
    }

    void update(ArrayList<IIconNameValue> items) {

        prepare(items.size());

        for (IIconNameValue item : items)
            update(items.indexOf(item), item);
    }

    void update(int index, IIconNameValue item) {
        update(getViewHolder(index), item.getImageId(), item.getName(), item.getValue());
    }

    private static void update(DetailsViewHolder holder, int imageId, String name, String value) {
        holder.icon.setImageResource(imageId);
        holder.name.setText(name);
        holder.value.setText(value);
    }
}

