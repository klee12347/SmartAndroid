/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.util;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by MarlinJoe on 2014/10/16.
 */
public class FileUtils {

    public static boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    // WARNING ! Inefficient if source and dest are on the same filesystem !
    public static boolean moveFile(File source, File dest) {
        return copyFile(source, dest) && source.delete();
    }

    // Returns true if the sdcard is mounted rw
    public static boolean isSDMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void deleteFile(File file){
        if(file.isFile()){//表示该文件不是文件夹
            file.delete();
        }else{
            //首先得到当前的路径
            String[] childFilePaths = file.list();
            for(String childFilePath : childFilePaths){
                File childFile=new File(file.getAbsolutePath()+ File.separator+childFilePath);
                deleteFile(childFile);
            }
            file.delete();
        }
    }
}
