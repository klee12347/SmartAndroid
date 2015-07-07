/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.smart.dao.Shift;


import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 班別
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class ShiftAdapter extends ArrayAdapter<Shift> {

    private static final String TAG = makeLogTag(ShiftAdapter.class);

    /**
     * Used to cache the data
     */
	private List<Shift> mDataSource;
    private Activity mContext;
    private int mLayoutId;//View資源

    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public ShiftAdapter(final Activity context) {
        super(context, 0);
        mContext = context;
        mDataSource = new ArrayList<Shift>();
        mLayoutId = android.R.layout.simple_list_item_1;
    }



    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Shift getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mDataSource != null)
            return mDataSource.get(position).hashCode();
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Recycle ViewHolder's items
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final Shift dataHolder = mDataSource.get(position);

        holder.txtName.setText(dataHolder.getCLSNM());
        return convertView;
    }


    public void setItemList(List<Shift> itemList) {

        this.mDataSource = itemList;
        this.notifyDataSetChanged();

    }


    public static class ViewHolder {

        public TextView txtName;

    }


    /**
     * 需要提供此方法才能有下拉
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getDropDownView(final int position, View convertView, final ViewGroup parent) {

        return getView(position,convertView,parent);
    }

}
