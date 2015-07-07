package fpg.ftc.si.smart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.DepartInfo;

/**
 * 組織樹
 * Created by MarlinJoe on 2014/8/8.
 */
public class DepartListAdapter extends ArrayAdapter<DepartInfo> {
    private LayoutInflater mInflater;


    private List<DepartInfo> mDataSource;
    /**
     * 切資料
     */
    private DepartViewInteractionHub mDepartViewInteractionHub;

    /**
     * 切圖示
     */
    //private FileIconHelper mFileIcon;

    private Context mContext;

    /**
     *  @param context
     * @param resource
     * @param departViewInteractionHub
     * @param objects
     */
    public DepartListAdapter(Context context, int resource, DepartViewInteractionHub departViewInteractionHub, List<DepartInfo> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
//        mFileViewInteractionHub = f;
//        mFileIcon = fileIcon;
        mContext = context;
        mDataSource = objects;
        mDepartViewInteractionHub = departViewInteractionHub;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public DepartInfo getItem(int location) {
        return mDataSource.get(location);
    }

    @Override
    public long getItemId(int position) {
        if (mDataSource != null)
            return mDataSource.get(position).hashCode();
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Recycle ViewHolder's items
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_depart, parent, false);
            holder = new ViewHolder();
            holder.txtDepartName = (TextView) convertView.findViewById(R.id.txt_depart_name);
            holder.imgArrar = (ImageView) convertView.findViewById(R.id.image_arror);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the data holder
        final DepartInfo dataHolder = mDataSource.get(position);

        holder.txtDepartName.setText(dataHolder.getDepartName());

        //當為最底層時,則顯示最末層的圖示
        if(dataHolder.isEnd())
        {
            holder.imgArrar.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imgArrar.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setDataSource(List<DepartInfo> dataSource)
    {
        mDataSource = dataSource;
    }

    public static class ViewHolder {

        public TextView txtDepartName;
        public ImageView imgArrar;

    }
}