package connect;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import msg.MsgValue;

/**
 * 使用HttpURLConnection从网站get和post数据
 * Created by Administrator on 2017/8/6 0006.
 */

public class MyHttpURLConnect {
    private static String urlStr = "http://116.62.145.58/index/index/patrol";

    private Handler handler;

    public MyHttpURLConnect(Handler handler) {
        this.handler = handler;
    }

    //提交信息
    public void post(TableInfor tableInfor) {
        //传入表单信息
        new PostThread(tableInfor).start();//用post方法发送
    }

    //POST线程
    class PostThread extends Thread {
        TableInfor tableInfor;

        public PostThread(TableInfor tableInfor) {
            this.tableInfor = tableInfor;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;

            InputStream is = null;
            String resultData = "";
            try {
                URL url = new URL(urlStr); //URL对象
                conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接,下面设置这个连接
                conn.setRequestMethod("POST"); //使用POST请求
                // 不要用cache，用了也没有什么用，因为我们不会经常对一个链接频繁访问。（针对程序）
                conn.setUseCaches(false);
                conn.setConnectTimeout(6 * 1000);
                conn.setReadTimeout(6 * 1000);
                conn.setRequestProperty("Charset", "utf-8");

                conn.connect();
                //参数字符串 //服务器不识别汉字
//                String param = "name=" + URLEncoder.encode(name, "UTF-8")
//                        + "&password=" + URLEncoder.encode(password, "UTF-8");
                //test
//                String param = TableInfor.element[0] + URLEncoder.encode("瀚海", "UTF-8")
//                        + "&"+TableInfor.element[1] + URLEncoder.encode("hr001", "UTF-8")
//                        + "&"+TableInfor.element[2] + URLEncoder.encode("瀚海", "UTF-8")
//                        + "&"+TableInfor.element[3] + URLEncoder.encode("巡检1号位置", "UTF-8")
//                        + "&"+TableInfor.element[4] + URLEncoder.encode("正常", "UTF-8");

                //用输出流向服务器发出参数，要求字符，所以不能直接用getOutputStream
                String param = tableInfor.getTableParam();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(tableInfor.getTableParam());
                dos.flush();
                dos.close();

                if (conn.getResponseCode() == 200) {//返回200表示相应成功
                    is = conn.getInputStream();   //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine = "";
                    while ((inputLine = bufferReader.readLine()) != null) {
                        resultData += inputLine + "\n";
                    }
                    System.out.println("post方法取回内容：" + resultData);

                    //判断第一个字符是否为1
                    char ch = resultData.charAt(0);
                    if (ch == 49) {
                        //提交成功
                        SendMessage(MsgValue.SUBMIT_SUCCESS, 0, 0, tableInfor);
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //提交失败
            SendMessage(MsgValue.SUBMIT_FAIL, 0, 0, "提交失败");
        }
    }


    void SendMessage(int what, int arg1, int arg2, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
        }
    }
}
