package com.sty.ne.binder.sample.server;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sty.ne.bindersample.ILoginInterface;

public class MainActivity extends Activity {
    private static final String NAME = "tian";
    private static final String PWD = "123";

    private EditText etName;
    private EditText etPwd;
    private ILoginInterface iLoginInterface; //AIDL定义接口
    private boolean isStartRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();

        initBindService();
    }

    private void initBindService() {
        Intent intent = new Intent();
        intent.setAction("Binder_Client_Action");
        intent.setPackage("com.sty.ne.bindersample");
        bindService(intent, conn, BIND_AUTO_CREATE);
        isStartRemote = true;
    }

    private void initView() {
        etName = findViewById(R.id.nameEt);
        etPwd = findViewById(R.id.pwdEt);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLoginInterface = ILoginInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //点击事件
    public void startLogin(View view) {
        final String name = etName.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //模拟登陆的Dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("登录");
        dialog.setMessage("登录中...");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean loginStatus = false;
                            if(NAME.equals(name) && PWD.equals(pwd)) {
                                Toast.makeText(MainActivity.this, "QQ登录成功", Toast.LENGTH_SHORT).show();
                                loginStatus = true;
                                //登录成功，销毁界面
                                finish();
                            }else {
                                Toast.makeText(MainActivity.this, "QQ登录失败", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                            //告知Client，登录结果返回
                            iLoginInterface.loginCallback(loginStatus, name);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isStartRemote) {
            unbindService(conn);
        }
    }
}
