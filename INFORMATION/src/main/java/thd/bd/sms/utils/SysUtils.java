package thd.bd.sms.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import thd.bd.sms.bean.BDCache;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;

public class SysUtils {

    /**
     * 判断网络连接是否已开
     * true 已打开  false 未打开
     */
    public static boolean isConn(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
//            searchNetwork(context);//弹出提示对话框
        }
        return false;
    }

    /**
     * 判断网络是否连接成功，连接成功不做任何操作
     * 未连接则弹出对话框提示用户设置网络连接
     */
    public static void searchNetwork(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 判断一个服务是否在运行
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityMananger = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityMananger
                .getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            Log.i("AppUtils", serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    /**
     * 数据处理，缓存数据库
     *
     * @param mBdCache
     */
    public static void dispatchData(Context mContext,BDCache mBdCache) {

        RDCacheOperation operation = new RDCacheOperation(mContext);

        int conutBefore = operation.getCount();
        if (conutBefore >= Config.CACHE_COUNT) {

            Toast.makeText(mContext, "抱歉,缓存溢出!", Toast.LENGTH_SHORT).show();

        } else {

            operation.insert(mBdCache);//插入数据
            int count = operation.getCount();//获取数据
            SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT,count);
            notifyData(mContext);
            // 唤醒线程
        }
    }

    /**
     * 通知数据变化
     */
    public static void notifyData(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        mContext.sendBroadcast(intent);
    }

    /**
     * 跳转百度地图
     */
    public static void goToBaiduMap(Activity activity, double mLat, double mLng, String mAddressStr) {
        if (!isInstalled(activity,"com.baidu.BaiduMap")) {
            Toast.makeText(activity,"请先安装百度地图客户端",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?destination=latlng:"
                + mLat + ","
                + mLng + "|name:" + mAddressStr + // 终点
                "&mode=driving" + // 导航路线方式
                "&src=andr.baidu.openAPIdemo"));
        activity.startActivity(intent); // 启动调用
    }

    /**
     * 检测程序是否安装
     *
     * @param packageName
     * @return
     */
    private static boolean isInstalled(Activity activity,String packageName) {
        PackageManager manager = activity.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }


}
