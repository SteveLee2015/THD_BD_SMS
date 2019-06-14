package thd.bd.sms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.LocationSet;
import thd.bd.sms.database.LocSetDatabaseOperation;
import thd.bd.sms.service.CycleLocService;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CustomListView;
import thd.bd.sms.view.OnCustomListListener;


/**
 * 北斗RDSS定位设置
 *
 * @author llg
 */
public class RDLocationSetActivity extends BaseActivity implements
        OnClickListener {
    /**
     * 日志标识
     */
    private static final String TAG = "RDLocationSetActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;


    private Context mContext = this;

    /**
     * 定位频度
     */
    private EditText locationStep = null;

    /**
     * 高程数据
     */
    private EditText height = null;

    /**
     * 天线数据
     */
    private EditText antenna = null;

    /**
     * 测高方式
     */
    private CustomListView altimetryType = null;

    /**
     * 设置按钮
     */
    private Button setBtn = null;

    /**
     * 坐标类型
     */
    private CustomListView coodrinateType = null;


    /**
     * RDSS定位设置数据操作
     */
    private LocSetDatabaseOperation settingDatabaseOper = null;


    /**
     * 数据库数据的总数和
     */
    private int locationSettingTotal = 0;
    private LocationSet set; //使用的定位参数
    private LocationSet continueLoc;//连续定位参数
    private LocationSet onlyoneLoc;//单次定位参数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        WinUtils.setWinTitleColor(this);
        initUI();
        locationSettingTotal = settingDatabaseOper.getSize();
        //LocationSet set =settingDatabaseOper.getFirst();
        set = settingDatabaseOper.getByStatus(LocationSet.LOCATIONSET_STATUS_USING);

        continueLoc = settingDatabaseOper.getByType(LocationSet.LOCATIONSET_TYPE_CONTINUE);
        onlyoneLoc = settingDatabaseOper.getByType(LocationSet.LOCATIONSET_TYPE_ONLY_ONE);
        /* 如果数据库中有数据,则在界面上显示数据库存储数据 */
        setUI(set);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_location_set;
    }


    private void setUI(LocationSet set) {

        if (set != null) {
            int type = set.getType();
            locationStep.setEnabled(type > 0 ? true : false);
            if (LocationSet.LOCATIONSET_TYPE_ONLY_ONE == type) {
                locationStep.setText(0 + "");
            } else {
                locationStep.setText(set.getLocationFeq());
            }
            height.setText(set.getHeightValue());
            antenna.setText(set.getTianxianValue());
            altimetryType.setIndex(Integer.valueOf(set.getHeightType()));
            setComponeStatusByAltimetry(Integer.valueOf(set.getHeightType()));
            coodrinateType.setIndex(type > 0 ? 1 : 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        altimetryType.setOnCustomListener(new OnCustomListListener() {
            @Override
            public void onListIndex(int num) {
                setComponeStatusByAltimetry(num);
            }
        });

        coodrinateType.setOnCustomListener(new OnCustomListListener() {
            @Override
            public void onListIndex(int num) {
                locationStep.setEnabled(num == 0 ? false : true);

                switch (num) {
                    case 0: {
                        set = onlyoneLoc;
                        break;
                    }
                    case 1: {
                        set = continueLoc;
                        break;
                    }
                }
                setUI(set);
            }
        });

    }

    /**
     * 初始化UI
     */
    public void initUI() {
        titleName.setText("RD定位设置");

        locationStep = (EditText) this.findViewById(R.id.location_step);
        height = (EditText) this.findViewById(R.id.height_value);
        antenna = (EditText) this.findViewById(R.id.tianxian_height_value);
        altimetryType = (CustomListView) this.findViewById(R.id.bd_check_height_type);
        setBtn = (Button) this.findViewById(R.id.bdset_submit_btn);
        coodrinateType = (CustomListView) this.findViewById(R.id.bd_report_coodr_type);
        coodrinateType.setData(this.getResources().getStringArray(R.array.bdloc_type_array));
        altimetryType.setData(this.getResources().getStringArray(R.array.test_height_spinner));
        setBtn.setOnClickListener(this);
        settingDatabaseOper = new LocSetDatabaseOperation(this);

    }

    /**
     * 各种测高方式下高程和天线组件的状态
     *
     * @param num
     */
    public void setComponeStatusByAltimetry(int num) {
        switch (num) {
            case 0:
                height.setEnabled(true);
                antenna.setEnabled(false);
                break;
            case 1:
                height.setEnabled(false);
                antenna.setEnabled(true);
                break;
            case 2:
                height.setEnabled(false);
                antenna.setEnabled(true);
                break;
            case 3:
                height.setEnabled(true);
                antenna.setEnabled(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bdset_submit_btn: {

                String classNameLoc = CycleLocService.class.getName();
                boolean isStartLoc = SysUtils.isServiceRunning(mContext, classNameLoc);
                Intent intentLoc = new Intent(this, CycleLocService.class);
                if (isStartLoc) {
                    stopService(intentLoc);
                }
                if ("".equals(SharedPreferencesHelper.getCardAddress())) {
                    return;
                }
                if (coodrinateType.getCurrentIndex() != 0) {
                    //校验定位频率数据
                    if (locationStep == null || "".equals(locationStep.getText().toString())) {
                        Toast.makeText(mContext, "频度不能为空!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //校验高程数据
                if (height == null || "".equals(height.getText().toString())) {
                    Toast.makeText(mContext, "高程不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //校验天线数据
                if (antenna == null || "".equals(antenna.getText().toString())) {
                    Toast.makeText(mContext, "天线高不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int frequency = Integer.valueOf(locationStep.getText().toString());
                //校验定位频度数据是否大于卡频度
                if ((coodrinateType.getCurrentIndex() != 0) && (frequency <= SharedPreferencesHelper.getSericeFeq())) {
                    Toast.makeText(mContext, "报告频度必须大于" + SharedPreferencesHelper.getSericeFeq() + "秒", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (coodrinateType.getCurrentIndex() == 0) {
                    frequency = 0;
                }
                //保存至数据库
                boolean toDataBase = saveToDataBase(frequency);
                if (toDataBase) {
                    RDLocationSetActivity.this.finish();
                }
                break;
            }
            default:
                break;
        }
    }


    public boolean saveToDataBase(int frequency) {
        boolean istrue = false;
        //如果数据库中没有定位设置的数据
        if (locationSettingTotal == 0) {
            LocationSet set = new LocationSet();
            set.setStatus(LocationSet.LOCATIONSET_STATUS_USING);
            //set.setLocationFeq(coodrinateType.getCurrentIndex()== 0?"0":String.valueOf(frequency));
            if (frequency == 0) {
                set.setType(LocationSet.LOCATIONSET_TYPE_ONLY_ONE);
            } else {
                set.setType(LocationSet.LOCATIONSET_TYPE_CONTINUE);
            }
            set.setLocationFeq(String.valueOf(frequency));
            set.setHeightType(String.valueOf(altimetryType.getCurrentIndex()));
            set.setHeightValue(height.isEnabled() ? String.valueOf(height.getText().toString()) : "0");
            set.setTianxianValue(antenna.isEnabled() ? String.valueOf(antenna.getText().toString()) : "0");
            istrue = settingDatabaseOper.insert(set);
        } else {
            //LocationSet set = settingDatabaseOper.getFirst();
            boolean updateOther = false;
            if (set == null) return false;
            set.setStatus(LocationSet.LOCATIONSET_STATUS_USING);
            if (frequency == 0) {
                set.setType(LocationSet.LOCATIONSET_TYPE_ONLY_ONE);
                continueLoc.setStatus(LocationSet.LOCATIONSET_STATUS_NOT_USING);
                updateOther = settingDatabaseOper.update(continueLoc);
            } else {
                set.setType(LocationSet.LOCATIONSET_TYPE_CONTINUE);
                onlyoneLoc.setStatus(LocationSet.LOCATIONSET_STATUS_NOT_USING);
                updateOther = settingDatabaseOper.update(onlyoneLoc);
            }
            //set.setLocationFeq(coodrinateType.getCurrentIndex()==0?"0":String.valueOf(frequency));
            set.setLocationFeq(String.valueOf(frequency));
            set.setHeightType(String.valueOf(altimetryType.getCurrentIndex()));
            set.setHeightValue(height.isEnabled() ? String.valueOf(height.getText().toString()) : "0");
            set.setTianxianValue(antenna.isEnabled() ? String.valueOf(antenna.getText().toString()) : "0");
            istrue = settingDatabaseOper.update(set);

        }
        if (istrue) {
            Toast.makeText(mContext, "定位设置成功", Toast.LENGTH_SHORT).show();
            Log.w("LERRYTEST_RD定位", "=========RDLocationSetActivity301===========set.getLocationFeq()==" + set.getLocationFeq() +
                    "========set.getHeightType()==" + set.getHeightType() + "============set.getHeightValue()==" + set.getHeightValue());
        } else {
            Toast.makeText(mContext, "定位设置失败", Toast.LENGTH_SHORT).show();
        }

        return istrue;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (settingDatabaseOper != null) {
            settingDatabaseOper.close();
        }
    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.return_home_layout:
                finish();
                break;
        }
    }
}
