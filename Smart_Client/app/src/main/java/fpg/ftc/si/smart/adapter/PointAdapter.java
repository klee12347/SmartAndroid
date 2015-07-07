package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.PointItem;
import fpg.ftc.si.smart.util.DateUtils;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 巡檢路線
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class PointAdapter extends ArrayAdapter<PointItem> {

    private static final String TAG = makeLogTag(PointAdapter.class);

    public List<PointItem> getmDataSource() {
        return mDataSource;
    }

    /**
     * Used to cache the data
     */
	private List<PointItem> mDataSource;
    //儲存目前 管制點與TAG的集合
    private HashMap<String,String> mNFCTagsMap;
    private Activity mContext;


    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public PointAdapter(final Activity context, ArrayList<PointItem> dataSource,HashMap<String,String> nfcTagsMap) {
        super(context, 0);
        mDataSource = dataSource;
        mContext = context;
        mNFCTagsMap = nfcTagsMap;
    }



    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public PointItem getItem(int location) {
        return mDataSource.get(location);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_point, parent, false);
            holder = new ViewHolder();
            holder.txtPointName = (TextView) convertView.findViewById(R.id.txtPointName);
            holder.txtRangeTime = (TextView) convertView.findViewById(R.id.txtRangeTime);
            holder.txtTotalCount = (TextView) convertView.findViewById(R.id.txt_total_count);
            holder.txtFinishCount = (TextView) convertView.findViewById(R.id.txt_finish_count);
            holder.imageStatus = (ImageView) convertView.findViewById(R.id.image_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final PointItem dataHolder = mDataSource.get(position);


        holder.txtPointName.setText(dataHolder.getNAME());
        holder.txtTotalCount.setText(String.valueOf(dataHolder.getTotal_Count()));
        holder.txtFinishCount.setText(String.valueOf(dataHolder.getFinish_Count()));

        if(dataHolder.getTIME_START()!=null && dataHolder.getTIME_END()!=null)
        {
            String timeRange = DateUtils.converHourMinToString(dataHolder.getTIME_START()) + "~" + DateUtils.converHourMinToString(dataHolder.getTIME_END());
            holder.txtRangeTime.setText(timeRange);
        }

        int total_count = dataHolder.getTotal_Count();
        int finish_count = dataHolder.getFinish_Count();

        if(total_count == finish_count)
        {
            holder.imageStatus.setImageResource(R.drawable.ic_ok_green);
        }
        else
        {
            holder.imageStatus.setImageResource(R.drawable.ic_ok_gray);
        }

        return convertView;
    }

    /**
     * 取得RFID的TAG
     * @param uid
     * @return
     */
    public PointItem getPointItemByNFCTag(String uid) {
        //TODO 可以再優化 or uid 有大小寫嗎
        PointItem result = null;
        if(mNFCTagsMap.size()>0)
        {
            if(mNFCTagsMap.containsKey(uid))
            {
                String ctlptid = mNFCTagsMap.get(uid);

                for(PointItem cp :mDataSource)
                {
                    if(cp.getCTLPTID().equals(ctlptid))
                    {
                        result = cp;
                        break;
                    }
                }
            }
        }

        return result;
    }



    public static class ViewHolder {

        public TextView txtPointName;
        public TextView txtRangeTime;
        public TextView txtTotalCount;
        public TextView txtFinishCount;
        public ImageView imageStatus;
    }


}
