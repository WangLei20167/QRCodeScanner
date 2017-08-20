package com.example.administrator.qrcodescanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
import com.jwsd.libzxing.OnQRCodeScanCallback;
import com.jwsd.libzxing.QRCodeManager;

import connect.MyHttpURLConnect;
import connect.TableInfor;
import dataCache.UserInfor;
import msg.MsgValue;
import runtimepermissions.PermissionsManager;
import runtimepermissions.PermissionsResultAction;
import utils.ACache;
import utils.LocalInfor;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText et_userName;
    private EditText et_remark;
    private Button bt_scan;

    private UserInfor mUserInfor;
    private MyHttpURLConnect myHttpURLConnect;

    //用于实现缓存
    private ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("收工回头看管理系统");
        //初始化控件
        et_userName = (EditText) findViewById(R.id.et_user_name);
        et_remark = (EditText) findViewById(R.id.et_remark);
        bt_scan = (Button) findViewById(R.id.button_scan);
        //请求清单文件中所有的权限
        requestPermission();

        //设置获取焦点   为了防止EditText首先获取焦点而自动弹出键盘
        imageView = (ImageView) findViewById(R.id.iv_logo);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();
        imageView.requestFocusFromTouch();

        //从网址获取异常
        myHttpURLConnect = new MyHttpURLConnect(handler);
        //缓存
        mCache = ACache.get(this);
        //获取用户名
        String userName = mCache.getAsString("userName");
        if (userName != null) {
            et_userName.setText(userName);
            mUserInfor = (UserInfor) mCache.getAsObject(userName);
        }
    }

    /**
     * 进入扫描二维码页面
     * 对扫描结果做处理
     *
     * @param view
     */
    public void onScanQR(View view) {
        final String userName = et_userName.getText().toString();
        if (userName.equals("")) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        //存入用户名
        mCache.put("userName", userName);
        final String remark = et_remark.getText().toString();
        if (remark.equals("")) {
            Toast.makeText(this, "请输入备注信息", Toast.LENGTH_SHORT).show();
            return;
        }
        QRCodeManager.getInstance()
                .with(this)
                .setReqeustType(1)
                .scanningQRCode(new OnQRCodeScanCallback() {
                    @Override
                    public void onCompleted(String result) {
                        //Toast.makeText(MainActivity.this, "(结果)" + result, Toast.LENGTH_SHORT).show();
                        //显示扫码结果
                        //tv_scanResult.setText(result);
                        onSubmitResult(userName, remark, result);
                    }

                    @Override
                    public void onError(Throwable errorMsg) {
                        //controlLog.append("\n\n(错误)" + errorMsg.toString());
                        Toast.makeText(MainActivity.this, "(错误)" + errorMsg.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        //controlLog.append("\n\n(取消)扫描任务取消了");
                        //Toast.makeText(MainActivity.this, "(取消)扫描任务取消了", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //提交扫码结果
    public void onSubmitResult(String userName, String remark, String scanResult) {
        //对scanResult进行格式检查
        //是否含有"_"
        int flag = scanResult.indexOf("_");
        if (flag == -1) {
            SendMessage(MsgValue.TELL_ME_SOME_INFOR, 0, 0,
                    "不可处理此二维码信息 " + scanResult
            );
            return;
        }
        //"_"后的子串是否可以转化为数字
        String strNo = scanResult.substring(flag + 1);
        try {
            int _no = Integer.parseInt(strNo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            SendMessage(MsgValue.TELL_ME_SOME_INFOR, 0, 0,
                    "不可处理此二维码信息 " + scanResult
            );
            return;
        }
        //处理对象
        if (mUserInfor != null && mUserInfor.getUserName().equals(userName)) {

        } else {
            //当前UserInfo对象不是输入的用户名对应的对象
            //从缓存中获取
            mUserInfor = (UserInfor) mCache.getAsObject(userName);
            //缓存中不存在此对象，则创建新对象实例
            if (mUserInfor == null) {
                mUserInfor = new UserInfor(userName);
            }
        }
        //对扫描按钮的处理
        bt_scan.setEnabled(false);
        bt_scan.setText("正在提交中...");
        String point = "";
        //设置point信息
        if (scanResult.indexOf("xungengdian") == 0) {
            point = "巡检" + strNo + "号位置";
        } else {
            point = scanResult;
        }

        TableInfor tableInfor = new TableInfor(mUserInfor.getUserName(), scanResult, 1, point, remark);
        //发送给web端服务器
        myHttpURLConnect.post(tableInfor);
    }


    /**
     * 处理各个类发来的UI请求消息
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MsgValue.TELL_ME_SOME_INFOR:
                    String infor = msg.obj.toString();
                    Toast.makeText(MainActivity.this, infor, Toast.LENGTH_SHORT).show();
                    break;
                case MsgValue.SUBMIT_SUCCESS:
                    //对扫描按钮的处理
                    bt_scan.setEnabled(true);
                    bt_scan.setTextColor(Color.parseColor("#000000"));
                    bt_scan.setText("(" + LocalInfor.getCurrentTime("HH:mm") + ")已提交成功，点击扫码");
                    Toast.makeText(MainActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    //加入本地记录
                    TableInfor tableInfor = (TableInfor) msg.obj;
                    mUserInfor.solveAddEven(tableInfor.getPoint(), tableInfor.getMemo());
                    //写入缓存  刷新缓存
                    mCache.put(mUserInfor.getUserName(), mUserInfor);
                    break;
                case MsgValue.SUBMIT_FAIL:
                    bt_scan.setEnabled(true);
                    bt_scan.setText("提交失败，点击重新扫码");
                    bt_scan.setTextColor(Color.parseColor("#FF0000"));

                    //查找失败原因，是否因为网络不可用
                    if (!LocalInfor.isNetworkAvailable(MainActivity.this)) {
                        //弹窗提示 网络不可用
                        String showMsg = "当前网络不可用，请检查网络连接";
                        DialogUIUtils.showAlert(MainActivity.this, "提示", showMsg, "", "", "确定", "", true, true, true, new DialogUIListener() {
                            @Override
                            public void onPositive() {
                            }

                            @Override
                            public void onNegative() {
                            }
                        }).show();
                    } else {
                        //未知原因提交失败
                        Toast.makeText(MainActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void SendMessage(int what, int arg1, int arg2, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册onActivityResult
        QRCodeManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data);
    }

    //改写back键
    private long mExitTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //退出前的处理操作
            //执行退出操作,并释放资源
            finish();
            //Dalvik VM的本地方法完全退出app
            Process.killProcess(Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //浏览历史记录
            case R.id.item_viewHistory:
                //从用户名输入框获取用户名
                String userName = et_userName.getText().toString();
                if (userName.equals("")) {
                    if (mUserInfor != null) {
                        userName = mUserInfor.getUserName();
                        et_userName.setText(userName);
                    } else {
                        Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    if (mUserInfor != null && mUserInfor.getUserName().equals(userName)) {
                        //查询的用户就是当前mUserInfor实例对象
                    } else {
                        //查询的不是当前对象，则先判断本地是否有此用户的数据
                        mUserInfor = (UserInfor) mCache.getAsObject(userName);
                        if (mUserInfor == null) {
                            Toast.makeText(this, "本地无" + userName + "用户信息", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                String date = LocalInfor.getCurrentTime("yyyy-MM-dd");
                Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("date", date);
                startActivity(intent);

                break;
            default:
        }
        return true;
    }

    /**
     * 适配android6.0以上权限                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         =
     */
    private void requestPermission() {
        /**
         * 请求所有必要的权限
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "权限 " + permission + " 被拒绝", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
