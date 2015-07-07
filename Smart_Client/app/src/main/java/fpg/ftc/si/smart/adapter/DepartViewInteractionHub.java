/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package fpg.ftc.si.smart.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import fpg.ftc.si.smart.DepartViewActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.DepartInfo;
import fpg.ftc.si.smart.model.DepartPathItem;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

public class DepartViewInteractionHub {

    private static final String TAG = makeLogTag(DepartViewInteractionHub.class);

    private IDepartInteractionListener mDepartViewListener;

    private View mNavigationBar;

    private TextView mNavigationBarText;

    private View mDropdownNavigation;

    private ImageView mNavigationBarUpDownArrow;

    private Context mContext;

    private String mCurrentPath;
    private String mCurrentPathName;//拿來顯示中文名稱
    private String mRoot;
    // File List view setup
    private ListView mFileListView;

    public DepartViewInteractionHub(IDepartInteractionListener fileViewListener) {
        assert (fileViewListener != null);
        mDepartViewListener = fileViewListener;
        setup();
        //mFileOperationHelper = new FileOperationHelper(this);
        //mFileSortHelper = new FileSortHelper();
        mContext = mDepartViewListener.getContext();
    }

    public DepartInfo getItem(int pos) {
        return mDepartViewListener.getItem(pos);
    }

    private void setup() {
        setupNaivgationBar();
        setupFileListView();
    }

    private void setupNaivgationBar() {
        mNavigationBar = mDepartViewListener.getViewById(R.id.navigation_bar);
        mNavigationBarText = (TextView) mDepartViewListener.getViewById(R.id.current_path_view);
        mNavigationBarUpDownArrow = (ImageView) mDepartViewListener.getViewById(R.id.path_pane_arrow);
        View clickable = mDepartViewListener.getViewById(R.id.current_path_pane);
        clickable.setOnClickListener(buttonClick);

        mDropdownNavigation = mDepartViewListener.getViewById(R.id.dropdown_navigation);

        setupClick(mNavigationBar, R.id.path_pane_up_level);
    }



    private void setupClick(View v, int id) {
        View button = (v != null ? v.findViewById(id) : mDepartViewListener.getViewById(id));
        if (button != null)
            button.setOnClickListener(buttonClick);
    }

    /**
     * 定義 View的按鈕事件
     */
    private OnClickListener buttonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.current_path_pane:
                    onNavigationBarClick();
                    break;

                case R.id.path_pane_up_level:
                    //返回上一層
                    onOperationUpLevel();
                    break;
            }
        }

    };


    /**
     * 導航列 展開出來的 ListView Row 點選的事件
     */
    private OnClickListener navigationClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            //mDepartViewListener.get
            String path = (String) v.getTag();
            assert (path != null);
            showDropdownNavigation(false);

            if(path.isEmpty()){
                mCurrentPath = mRoot;
            } else{
                mCurrentPath = path;
            }
            refreshFileList();
        }

    };

    /**
     * 導覽列 點選事件
     */
    protected void onNavigationBarClick() {
        if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
            showDropdownNavigation(false);
        } else {
            LinearLayout list = (LinearLayout) mDropdownNavigation.findViewById(R.id.dropdown_navigation_list);
            list.removeAllViews();
            int pos = 0;
            DepartPathItem displayPath = mDepartViewListener.getDisplayPath(mCurrentPath);
            String[] full_path_id_arr = displayPath.getFULL_PATH_ID().split("/");
            int idx = 0;
            boolean root = true;
            int left = 0;
            while (pos != -1 && !displayPath.FULL_PATH_DISPLAY.equals("/")) {

                //如果當前位置在根目錄則不顯示導航列
                int end = displayPath.getFULL_PATH_DISPLAY().indexOf("/", pos);
                if (end == -1)
                    break;



                View listItem = LayoutInflater.from(mContext).inflate(R.layout.dropdown_item,
                        null);

                View listContent = listItem.findViewById(R.id.list_item);
                listContent.setPadding(left, 0, 0, 0);
                left += 20;
                ImageView img = (ImageView) listItem.findViewById(R.id.item_icon);

                img.setImageResource(root ? R.drawable.dropdown_icon_root : R.drawable.dropdown_icon_folder);
                root = false;

                TextView text = (TextView) listItem.findViewById(R.id.path_name);
                String substring = displayPath.getFULL_PATH_DISPLAY().substring(pos, end);
                if(substring.isEmpty())substring = "/";
                text.setText(substring);

                listItem.setOnClickListener(navigationClick);
                listItem.setTag(full_path_id_arr[idx]);
                pos = end + 1;
                list.addView(listItem);
                idx++;
            }
            if (list.getChildCount() > 0)
                showDropdownNavigation(true);

        }
    }


    public boolean onOperationUpLevel() {
        showDropdownNavigation(false);
        if (!mRoot.equals(mCurrentPath)) {
            //取得目前節點的父親節點當目前路徑
            mCurrentPath = mDepartViewListener.getParentDepID(mCurrentPath);
            refreshFileList();
            return true;
        }

        return false;
    }

    public void refreshFileList() {

        updateNavigationPane();

        mDepartViewListener.onRefreshFileList(mCurrentPath);

    }

    private void updateNavigationPane() {
        View upLevel = mDepartViewListener.getViewById(R.id.path_pane_up_level);
        upLevel.setVisibility(mRoot.equals(mCurrentPath) ? View.INVISIBLE : View.VISIBLE);

        View arrow = mDepartViewListener.getViewById(R.id.path_pane_arrow);
        arrow.setVisibility(mRoot.equals(mCurrentPath) ? View.GONE : View.VISIBLE);

        mNavigationBarText.setText(mDepartViewListener.getDisplayPath(mCurrentPath).getFULL_PATH_DISPLAY());
    }

    private void setupFileListView() {
        mFileListView = (ListView) mDepartViewListener.getViewById(R.id.file_path_list);
        mFileListView.setLongClickable(true);
        mFileListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });
    }


    /**
     * 下面清單項目點選觸發
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        DepartInfo departInfo = mDepartViewListener.getItem(position);
        showDropdownNavigation(false);

        if (departInfo == null) {
            Log.e(TAG, "不存在這筆資料" + position);
            return;
        }

        if(!departInfo.isEnd())
        {
            mCurrentPath =  departInfo.DepartID;
            refreshFileList();
        }
        else
        {
            //已經到底 就直接幫忙切到人員頁面
            DepartViewActivity mDepartViewActivity =((DepartViewActivity) mContext);
            mDepartViewActivity.CurrentSelected = departInfo.DepartID;
            mDepartViewActivity.goToDepartUserView();

        }

    }

    public void setRootPath(String path) {
        mRoot = path;
        mCurrentPath = path;
    }

    public String getRootPath() {
        return mRoot;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(String path) {
        mCurrentPath = path;
    }

    /**
     * 是否顯示 導航列 上下箭頭
     * @param show
     */
    private void showDropdownNavigation(boolean show) {
        mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        mNavigationBarUpDownArrow
                .setImageResource(mDropdownNavigation.getVisibility() == View.VISIBLE ? R.drawable.arrow_up
                        : R.drawable.arrow_down);
    }

}
