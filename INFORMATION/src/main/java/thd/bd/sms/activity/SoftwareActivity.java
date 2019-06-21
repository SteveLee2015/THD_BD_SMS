package thd.bd.sms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.utils.WinUtils;

/**
 * 软件说明界面
 *
 * @author llg
 */
public class SoftwareActivity extends BaseActivity {

    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    private TextView versionTextView;//版本
    private ImageView icon;//图标
    private Button updateBtn;//更新
    private TextView dataTextView;//日期
    //private TextView mapVersionTextView;
    private Context mContext = this;
    long[] mHits = new long[9];

    private static final String PACKAGE_TAG = "S510_APP_Message_";

    @Override
    protected int getContentView() {
        return R.layout.activity_software;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        WinUtils.setWinTitleColor(this);

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            WinUtils.setDialogPosition(this, 0.9f, 0.6f);
        } else {
            WinUtils.setDialogPosition(this, 0.9f, 0.9f);
        }
        String appVersion = getAppVersion(this);
        // 软件版本
        versionTextView = (TextView) this.findViewById(R.id.project_version_textview);
        icon = (ImageView) this.findViewById(R.id.iv_icon);
        dataTextView = (TextView) this.findViewById(R.id.project_date_textview);

        if (appVersion.contains(":")) {
            try {
                String[] versionInfos = appVersion.split(":");
                String versionName = versionInfos[0];
                String versionDate = versionInfos[1];
                versionTextView.setText("软件版本：" + versionName);
                dataTextView.setText("日期：" + versionDate);
            } catch (Exception e) {
                Toast.makeText(this, "获取软件版本信息错误!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            versionTextView.setText("软件版本：" + appVersion);
        }

        //图片 9连击

		/*icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//每点击一次 实现左移一格数据
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				//给数组的最后赋当前时钟值
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				//当0出的值大于当前时间-500时  证明在500秒内点击了2次
				if(mHits[0] > SystemClock.uptimeMillis() - 2000){
					Toast.makeText(mContext, "9连击,蓝牙设置界面已打开!", Toast.LENGTH_SHORT).show();
					SMSapp app = (SMSapp) getApplication();
					app.openBlueTooth = true;

				}
			}
		});*/

        // 地图版本
//		mapVersionTextView = (TextView) this
//				.findViewById(R.id.map_version_textview);
        //mapVersionTextView.setText("地图版本:" + getMapVersion(this));

        updateBtn = (Button) this.findViewById(R.id.update_version_btn);
        updateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 检查当前外置SD卡中是否有升级包，如果有则进行升级
                File extfile = new File("/mnt/sdcard");
                File[] list = extfile.listFiles();
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                File inetfile = new File(sdCardPath);
                File[] inetfileList = inetfile.listFiles();
                if (list != null && list.length > 0 && inetfileList != null
                        && inetfileList.length > 0) {
                    final List<String> names = new ArrayList<String>();
                    for (File file : list) {
                        if (!file.isDirectory()) {
                            if (file.getName().startsWith(PACKAGE_TAG)
                                    && file.getName().endsWith(".apk")) {
                                names.add(file.getName());
                            }
                        }
                    }
                    if (names.size() <= 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SoftwareActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("请在存储卡中放置升级包!");
                        builder.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else if (names.size() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SoftwareActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("是否把当前应用更新为" + names.get(0) + "?");
                        builder.setPositiveButton("升级",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        String apkPath = "/mnt/sdcard/"
                                                + names.get(0);
                                        // String
                                        // version1=getUninstallAPKInfo(BDSoftwareActivity.this,apkPath);
                                        // Toast.makeText(BDSoftwareActivity.this,
                                        // ""+version1,
                                        // Toast.LENGTH_SHORT).show();
                                        // 调用升级流程进行升级
                                        Intent intent = new Intent(
                                                Intent.ACTION_VIEW);
                                        intent.setDataAndType(
                                                Uri.fromFile(new File(apkPath)),
                                                "application/vnd.android.package-archive");
                                        startActivity(intent);
                                    }
                                });
                        builder.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SoftwareActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("存储卡中有多个升级包,请只保留一个!");
                        builder.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SoftwareActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请安装带有升级包的存储卡!");
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        });
    }


    public static String getAppVersion(Context mContext) {
        String versionName = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo pi = manager.getPackageInfo(mContext.getPackageName(),
                    0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获得未安装APK的版本
     *
     * @param ctx
     * @param archiveFilePath
     * @return
     */
    private String getUninstallAPKInfo(Context ctx, String archiveFilePath) {
        String versionName = null;
        String appName = null;
        String pakName = null;
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pakinfo = pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_ACTIVITIES);
        if (pakinfo != null) {
            ApplicationInfo appinfo = pakinfo.applicationInfo;
            versionName = pakinfo.versionName;
            Drawable icon = pm.getApplicationIcon(appinfo);
            appName = (String) pm.getApplicationLabel(appinfo);
            pakName = appinfo.packageName;
        }
        return versionName;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            WinUtils.setDialogPosition(this, 0.9f, 0.6f);
        } else {
            WinUtils.setDialogPosition(this, 0.9f, 0.9f);
        }
    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        SoftwareActivity.this.finish();
    }
}
