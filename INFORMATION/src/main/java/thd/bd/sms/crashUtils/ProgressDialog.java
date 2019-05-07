package thd.bd.sms.crashUtils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author lerry
 * Description：圆形进度条Dialog
 */
public class ProgressDialog {

    private static volatile ProgressDialog instance;

    private ProgressDialog() {
    }

    public static ProgressDialog getInstance() {
        if (instance == null) {
            synchronized (ProgressDialog.class) {
                if (instance == null) {
                    instance = new ProgressDialog();
                }
            }
        }
        return instance;
    }

    private MaterialDialog materialDialog;

    public void show(Context mContext) {
        materialDialog = new MaterialDialog.Builder(mContext)
//                .title(R.string.progress_dialog_title)
                .content("请稍等...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();

    }

    public void dismiss() {
        materialDialog.dismiss();
    }
}
