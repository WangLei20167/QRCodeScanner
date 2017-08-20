package dataCache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.LocalInfor;
import utils.MyArray;

/**
 * Created by Administrator on 2017/8/3 0003.
 */
//一个用户下
public class DateRecord implements Serializable {
    private String date;   //格式年月日
    private int scanResultNum = 0;
    private List<ScanResult> scanResultList = new ArrayList<ScanResult>();
    //记录扫描到的不同位置数据
    private String[] pointList = null;

    public DateRecord(String date) {
        this.date = date;
    }

    //处理添加一个ItemMsg事件
    public void solveAddEvent(String scan_result, String remark) {
        boolean haveThisPoint = false;
        if (pointList == null) {
            String[] newPointList = new String[1];
            newPointList[0] = scan_result;
            pointList = newPointList;
        } else {
            for (String scan : pointList) {
                if (scan.equals(scan_result)) {
                    haveThisPoint = true;
                    break;
                }
            }
            if (!haveThisPoint) {
                pointList = (String[]) MyArray.arrayGrow(pointList, 1);
                int size = pointList.length;
                pointList[size-1] = scan_result;
            }
        }

        String time = LocalInfor.getCurrentTime("HH:mm:ss");
        ScanResult scanResult = new ScanResult(time, scan_result, remark);
        scanResultList.add(scanResult);
        ++scanResultNum;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ScanResult> getScanResultList() {
        return scanResultList;
    }

    public void setScanResultList(List<ScanResult> scanResultList) {
        this.scanResultList = scanResultList;
    }

    public int getScanResultNum() {
        return scanResultNum;
    }

    public void setScanResultNum(int scanResultNum) {
        this.scanResultNum = scanResultNum;
    }

    public String[] getPointList() {
        return pointList;
    }

    public void setPointList(String[] pointList) {
        this.pointList = pointList;
    }
}

