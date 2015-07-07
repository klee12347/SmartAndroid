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
import android.content.Intent;
import android.view.View;

import java.util.List;

import fpg.ftc.si.smart.model.DepartInfo;
import fpg.ftc.si.smart.model.DepartPathItem;

public interface IDepartInteractionListener {

    public View getViewById(int id);

    public Context getContext();

    /**
     * 取得目前路徑 含自己的子節點集合
     * @param current_depid
     * @return
     */
    public List<DepartInfo> getCurrentPathChildren(String current_depid);

    public void startActivity(Intent intent);

    public void onDataChanged();

    public void onPick(DepartInfo f);

    public boolean shouldShowOperationPane();

    /**
     * Handle operation listener.
     * @param id
     * @return true: indicate have operated it; false: otherwise.
     */
    public boolean onOperation(int id);

    public DepartPathItem getDisplayPath(String path);

    public String getRealPath(String displayPath);

    public void runOnUiThread(Runnable r);

    /**
     * 取得目前節點的父節點
     * @param current_depid
     * @return
     */
    public String getParentDepID(String current_depid);

    public boolean shouldHideMenu(int menu);

//    public FileIconHelper getFileIconHelper();

    public DepartInfo getItem(int pos);

//    public void sortCurrentList(FileSortHelper sort);

//    public Collection<FileInfo> getAllFiles();

//    public void addSingleFile(FileInfo file);

    public boolean onRefreshFileList(String path);

    public int getItemCount();

    
}
