package fpg.ftc.si.smart.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.smart.DepartViewActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.DepartListAdapter;
import fpg.ftc.si.smart.adapter.DepartViewInteractionHub;
import fpg.ftc.si.smart.adapter.IDepartInteractionListener;
import fpg.ftc.si.smart.dao.Depart;
import fpg.ftc.si.smart.model.DepartInfo;
import fpg.ftc.si.smart.model.DepartPathItem;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 檔案管理工具的組織樹
 * 1.開啟組織資料庫
 *
 *
 */
public class DepartViewFragment extends Fragment implements IDepartInteractionListener {

    private static final String TAG = makeLogTag(DepartViewFragment.class);



    //檔案總管類
    private View mRootView;
    private ListView mFileListView;
    private DepartViewActivity mActivity;
    private DepartListAdapter mAdapter;

    private IDepartInteractionListener mFileViewListener;

    private View mNavigationBar;
    private TextView mNavigationBarText;
    private View mDropdownNavigation;
    private ImageView mNavigationBarUpDownArrow;

    public DepartViewFragment() {
        // Required empty public constructor
    }
    private DepartViewInteractionHub mDepartViewInteractionHub;

    private ArrayList<DepartInfo> mFileNameList = new ArrayList<DepartInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        LOGI(TAG,"onCreateView");
        mActivity = (DepartViewActivity) getActivity();
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.depart_explorer_list, container, false);
        mFileListView = (ListView) mRootView.findViewById(R.id.file_path_list);

        mAdapter = new DepartListAdapter(mActivity, R.layout.list_row_depart, mDepartViewInteractionHub, mFileNameList);

        //取得根節點
        Depart root = mActivity.BasicDBHelper.getDepart("");
        mDepartViewInteractionHub = new DepartViewInteractionHub(this);
        //設定根節點路徑
        mDepartViewInteractionHub.setRootPath(root.getDEPID());

        // Declare
        String currentPath = root.getDEPID();//先拿根節點的路徑當預設
        mDepartViewInteractionHub.setCurrentPath(currentPath);
        LOGI(TAG, "currentPath = " + currentPath);

        mFileListView.setAdapter(mAdapter);
        mDepartViewInteractionHub.refreshFileList();


        return mRootView;
    }

    @Override
    public View getViewById(int id) {
        return mRootView.findViewById(id);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public List<DepartInfo> getCurrentPathChildren(String current_depid) {

        return null;
    }

    @Override
    public void onDataChanged() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onPick(DepartInfo f) {

    }

    @Override
    public boolean shouldShowOperationPane() {
        return false;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    /**
     * 取得 顯示路徑
     * EX:中纖/氣電課
     * @param depid DEPID
     * @return
     */
    @Override
    public DepartPathItem getDisplayPath(String depid) {
        DepartPathItem result = null;
        Depart depart_item = mActivity.BasicDBHelper.getDepart(depid);
        if (depart_item != null) {
            result = new DepartPathItem(depart_item.getFULLPATH_ID(), depart_item.getFULLPATH());
        }
        return result;
    }

    @Override
    public String getRealPath(String displayPath) {
        return "";
    }

    @Override
    public void runOnUiThread(Runnable r) {

    }

    /**
     * 拿DEPID
     * @param current_depid
     * @return
     */
    @Override
    public String getParentDepID(String current_depid) {
        Depart current_depart = mActivity.BasicDBHelper.getDepart(current_depid);
        return current_depart.getPARENT_DEPID();
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }

    @Override
    public DepartInfo getItem(int pos) {

        return mAdapter.getItem(pos);
    }

    /**
     * 刷新資料 取路徑底下的子節點
     * @param path
     * @return true 有資料
     */
    @Override
    public boolean onRefreshFileList(String path) {
        //將目前路徑 記錄到activity中
        mActivity.CurrentSelected = path;
        boolean result = false;

        ArrayList<DepartInfo> fileList = mFileNameList;
        fileList.clear();

        String temp_depid= path;
        //空的話抓取根目錄下面的節點
        if(path.isEmpty())
        {
            Depart root_depart = mActivity.BasicDBHelper.getDepart(path);
            temp_depid = root_depart.getDEPID();
        }

        List<Depart> departList = mActivity.BasicDBHelper.getDeparts(temp_depid);
        for (Depart item : departList)
        {
            fileList.add(new DepartInfo(item.getDEPID(),item.getNAME(),item.getISEND()));
        }

        int depart_child = departList.size();


        //串資料
        //排序
        onDataChanged();
        mAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public int getItemCount() {
        return 0;
    }




}
