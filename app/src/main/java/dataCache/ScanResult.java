package dataCache;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/3 0003.
 */
public class ScanResult implements Serializable {
    //一天中 一条信息
    private String time;   //时分秒
    private String scan_result;
    private String remark;

    public ScanResult(String time, String scan_result,String remark) {
        this.time = time;
        this.scan_result = scan_result;
        this.remark=remark;
    }

    public String getScan_result() {
        return scan_result;
    }

    public void setScan_result(String scan_result) {
        this.scan_result = scan_result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
