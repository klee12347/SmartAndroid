package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.RouteItem;

import static fpg.ftc.si.smart.util.LogUtils.*;

/**
 * 巡檢路線
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class RouteAdapter extends ArrayAdapter<RouteItem> {

    private static final String TAG = makeLogTag(RouteAdapter.class);

    /**
     * Used to cache the data
     */
	private List<RouteItem> mDataSource;
    private Activity mContext;


    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public RouteAdapter(final Activity context, ArrayList<RouteItem> dataSource) {
        super(context, 0);
        mDataSource = dataSource;
        mContext = context;
    }



    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public RouteItem getItem(int location) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_route, parent, false);
            holder = new ViewHolder();
            holder.txtRouteName = (TextView) convertView.findViewById(R.id.txtRouteName);
            holder.txtArrTime = (TextView) convertView.findViewById(R.id.txtArrTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final RouteItem dataHolder = mDataSource.get(position);


        holder.txtRouteName.setText(dataHolder.getNAME());
        holder.txtArrTime.setText(mContext.getResources().getString(R.string.lb_arrtime) + " " + dataHolder.getARRTIME());


        return convertView;
    }


    public static class ViewHolder {

        public TextView txtRouteName;
        public TextView txtArrTime;

    }


}
