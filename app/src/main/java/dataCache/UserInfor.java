package dataCache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.LocalInfor;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class UserInfor implements Serializable {
    //主属性
    private String userName;
    private int dateRecodeNum = 0;
    private List<DateRecord> dateRecordList = new ArrayList<DateRecord>();

    public UserInfor(String userName) {
        this.userName = userName;
    }

    //处理一个添加DataRecode事件
    public void solveAddEven(String scan_result, String remark) {
        String date = LocalInfor.getCurrentTime("yyyy-MM-dd");
        //查找date记录是否存在
        DateRecord dateRecord0 = null;
        for (DateRecord dateRecord : dateRecordList) {
            if (dateRecord.getDate().equals(date)) {
                dateRecord0 = dateRecord;
                break;
            }
        }
        DateRecord dateRecord = null;
        if (dateRecord0 == null) {
            dateRecord = new DateRecord(date);
        } else {
            dateRecord = dateRecord0;
            dateRecordList.remove(dateRecord0);
            --dateRecodeNum;
        }

        dateRecord.solveAddEvent(scan_result, remark);
        dateRecordList.add(dateRecord);
        ++dateRecodeNum;
        //写入缓存
        //在MainActivity该方法调用之后使用缓存
    }

    public int getDateRecodeNum() {
        return dateRecodeNum;
    }

    public void setDateRecodeNum(int dateRecodeNum) {
        this.dateRecodeNum = dateRecodeNum;
    }

    public List<DateRecord> getDateRecordList() {
        return dateRecordList;
    }

    public void setDateRecordList(List<DateRecord> dateRecordList) {
        this.dateRecordList = dateRecordList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

