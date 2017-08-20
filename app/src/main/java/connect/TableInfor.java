package connect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class TableInfor {
    private String name;
    private String qr_id;
    private int groupid;
    private String point;
    private String memo;


    private String tableParam;
    public static String[] element = {
            "name=",
            "qr_id=",
            "groupid=",
            "point=",
            "memo="
    };

    public TableInfor(String name, String qr_id, int groupid, String point, String memo) {
        this.name = name;
        this.qr_id = qr_id;
        this.groupid = groupid;
        this.point = point;
        this.memo = memo;

        //test
        try {
            tableParam = element[0] + URLEncoder.encode(name, "UTF-8")
                    + "&"+element[1] + URLEncoder.encode(qr_id, "UTF-8")
                    + "&"+element[2] + URLEncoder.encode(groupid+"", "UTF-8")
                    + "&"+element[3] + URLEncoder.encode(point, "UTF-8")
                    + "&"+element[4] + URLEncoder.encode(memo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getMemo() {
        return memo;
    }

    public String getPoint() {
        return point;
    }

    public String getTableParam() {
        return tableParam;
    }
}
