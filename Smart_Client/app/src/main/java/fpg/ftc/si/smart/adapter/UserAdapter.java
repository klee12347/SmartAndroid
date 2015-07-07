/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.Json_User;
import fpg.ftc.si.smart.util.StringUtils;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 使用者
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class UserAdapter extends ArrayAdapter<Json_User> {

    private static final String TAG = makeLogTag(UserAdapter.class);

    /**
     * Used to cache the data
     */
	private ArrayList<Json_User> mDataSource;
    private ArrayList<Json_User> mMatchArrayList;
    private Activity mContext;

    private static HashMap<String,Boolean> mSelectedMap;//選取項目 account
    private int mSelectedCount;//選取數

    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public UserAdapter(final Activity context) {
        super(context, 0);
        mContext = context;
        mDataSource = new ArrayList<Json_User>();
        mSelectedMap = new HashMap<String, Boolean>();
        mMatchArrayList = new ArrayList<Json_User>();
    }



    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Json_User getItem(int position) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_user, parent, false);
            holder = new ViewHolder();
            holder.txtAccount = (TextView) convertView.findViewById(R.id.txt_account);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.cbUser = (CheckBox) convertView.findViewById(R.id.cb_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final Json_User dataHolder = mDataSource.get(position);
        String account = dataHolder.getACCOUNT();
        holder.txtAccount.setText(account );
        holder.txtName.setText(dataHolder.getNAME());
        holder.cbUser.setChecked(getmSelectedMap().get(account));
        return convertView;
    }

    /**
     * 過濾資料
     * @param search_text
     */
    public void filter(String search_text) {
        search_text = search_text.toLowerCase(Locale.getDefault());
        mDataSource.clear();
        if (search_text.length() == 0) {
            mDataSource.addAll(mMatchArrayList);
        }
        else
        {
            for (Json_User item : mMatchArrayList)
            {
                if (item.getACCOUNT().toLowerCase(Locale.getDefault()).contains(search_text) || item.getNAME().toLowerCase(Locale.getDefault()).contains(search_text))
                {
                    mDataSource.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 初始化 isSelected
     */
    private void initDate(){
        getmSelectedMap().clear();
        for(Json_User item : mDataSource)
        {
            getmSelectedMap().put(item.getACCOUNT(),false);
        }
        mSelectedCount = 0;
    }

    /**
     * 灌資料近來
     * @param itemList
     */
    public void setItemList(ArrayList<Json_User> itemList) {

        this.mDataSource = itemList;
        this.mMatchArrayList.clear();
        this.mMatchArrayList.addAll(itemList);
        initDate();
        this.notifyDataSetChanged();

    }

    public static HashMap<String,Boolean> getmSelectedMap() {
        return mSelectedMap;
    }

    public void setIsSelected(HashMap<String,Boolean> isSelected) {
        this.mSelectedMap = isSelected;
    }

    public void setItemSelected(String account, boolean isChecked) {

        this.getmSelectedMap().put(account, isChecked);
        if (isChecked) {
            mSelectedCount++;
        } else {
            mSelectedCount--;
        }
    }

    /**
     * 取得目前選取的數量
     * @return
     */
    public int getmSelectedCount() {
        return mSelectedCount;
    }

    /**
     * 串成下載的參數
     * TODO 可能會慢
     * @return urid=xxxx&urid=xxx
     */
    public String getSelectedParam(){
        String result = "";
        List<String> tempArray = new ArrayList<String>();
        HashMap<String,Boolean> getSelectedMap = getmSelectedMap();
        for(Json_User user : mDataSource)
        {
            String account = user.getACCOUNT();
            if(getSelectedMap.containsKey(account))
            {
                if(getSelectedMap.get(account))
                {
                    tempArray.add("urid=" + user.getURID());
                }
            }
        }
        result = StringUtils.join(tempArray, "&");
        return result;
    };

    public static class ViewHolder {

        public TextView txtAccount;
        public TextView txtName;
        public CheckBox cbUser;
    }


}
