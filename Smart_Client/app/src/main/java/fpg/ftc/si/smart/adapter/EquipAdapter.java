package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.EquipmentItem;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 巡檢路線
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class EquipAdapter extends ArrayAdapter<EquipmentItem> {

    private static final String TAG = makeLogTag(EquipAdapter.class);

    /**
     * Used to cache the data
     */
	private List<EquipmentItem> mDataSource;
    private Activity mContext;


    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public EquipAdapter(final Activity context, ArrayList<EquipmentItem> dataSource) {
        super(context, 0);
        mDataSource = dataSource;
        mContext = context;
    }



    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public EquipmentItem getItem(int location) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_equip, parent, false);
            holder = new ViewHolder();
            holder.txtEquipName = (TextView) convertView.findViewById(R.id.txtEquipName);
            holder.txtTotalCount = (TextView) convertView.findViewById(R.id.txt_total_count);
            holder.txtFinishCount = (TextView) convertView.findViewById(R.id.txt_finish_count);
            holder.imageStatus = (ImageView) convertView.findViewById(R.id.image_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final EquipmentItem dataHolder = mDataSource.get(position);

        int total_count = dataHolder.getTotal_Count();
        int finish_count = dataHolder.getFinish_Count();
        holder.txtEquipName.setText(dataHolder.getNAME());
        holder.txtTotalCount.setText(String.valueOf(total_count));
        holder.txtFinishCount.setText(String.valueOf(finish_count));

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
     * 取得目前已做完的設備
     * @return
     */
    public int getCheckCount() {

        int result = 0;
        for(EquipmentItem item : mDataSource)
        {
            int total_count = item.getTotal_Count();
            int finish_count = item.getFinish_Count();
            if(total_count == finish_count)
            {
                result ++;
            }
        }
        return result;
    }


    /**
     * 取得百分比
     * @return
     */
    public int getProgress()
    {
        return (int)((getCheckCount()*100)/getCount());
    }

    public static class ViewHolder {

        public TextView txtEquipName;
        public TextView txtTotalCount;
        public TextView txtFinishCount;
        public ImageView imageStatus;

    }


}
