package fpg.ftc.si.smart.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.util.consts.SystemConstants;

/**
 * Created by MarlinJoe on 2014/8/15.
 */
public class Utils {
    private static final String TAG = "Config";

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    SystemConstants.PACKAGE_NAME, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    SystemConstants.PACKAGE_NAME, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;

    }

    public static String getAppName(Context context) {
        String verName = context.getResources()
                .getText(R.string.app_name).toString();
        return verName;
    }

    /**
     * 取得 android device 唯一編號
     * @param context
     * @return
     */
    public static String getDeviceId(Context context)
    {
        String result = "";

        result = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return result;
    }

    public static String combine (String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    public static boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    public static boolean setText(View view, int id, int text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }
}
