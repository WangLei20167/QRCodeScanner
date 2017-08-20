package com.example.administrator.qrcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIDateTimeSaveListener;
import com.dou361.dialogui.widget.DateSelectorWheelView;

import dataCache.DateRecord;
import dataCache.ScanResult;
import dataCache.UserInfor;
import utils.ACache;

public class ViewHistoryActivity extends AppCompatActivity {

    private TextView tv_history;

    private UserInfor mUserInfor;
    private ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        //带滑动条的TextView
        tv_history = (TextView) findViewById(R.id.tv_history);
        tv_history.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String date = intent.getStringExtra("date");

        setTitle(userName);
        //从本地获取对象
        mCache = ACache.get(this);
        mUserInfor = (UserInfor) mCache.getAsObject(userName);
        //显示date当天历史记录
        showHistory(date);
    }

    public void showHistory(String date) {
        tv_history.setText("历史记录\n");
        //若是本地无此用户信息
        if (mUserInfor == null) {
            tv_history.append("error:本地数据有误");
            return;
        }
        for (DateRecord dateRecord : mUserInfor.getDateRecordList()) {
            if (dateRecord.getDate().equals(date)) {
                String msg = "";
                msg += (date + "当天共" + dateRecord.getScanResultNum() + "条巡检记录");
                String[] pointList = dateRecord.getPointList();
                msg += ("\n其中有" + pointList.length + "个不同位置,分别为\n");
                for (String point : pointList) {
                    msg += (point + "     ");
                }
                msg += "\n";
                int count = 0;
                for (ScanResult scanResult : dateRecord.getScanResultList()) {
                    ++count;
                    msg += ("\n" + count + "、 " + scanResult.getTime());
                    msg += ("\n巡更位置： " + scanResult.getScan_result());
                    msg += ("\n备注： " + scanResult.getRemark());
                    msg += "\n\n";
                }
                tv_history.append(msg);
                return;
            }
        }
        tv_history.append(date + "当天无巡检记录");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //更改日期
            case R.id.item_changeDate:
                DialogUIUtils.showDatePick(this, Gravity.CENTER, "选择日期", System.currentTimeMillis() + 60000, DateSelectorWheelView.TYPE_YYYYMMDD, 0, new DialogUIDateTimeSaveListener() {
                    @Override
                    public void onSaveSelectedDate(int tag, String selectedDate) {
                        showHistory(selectedDate);
                    }
                }).show();
                break;
            default:
        }
        return true;
    }
}
