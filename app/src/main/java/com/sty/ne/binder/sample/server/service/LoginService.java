package com.sty.ne.binder.sample.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sty.ne.binder.sample.server.MainActivity;
import com.sty.ne.bindersample.ILoginInterface;

import androidx.annotation.Nullable;

/**
 * @Author: tian
 * @UpdateDate: 2020-08-19 21:55
 */
public class LoginService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginInterface.Stub() {
            @Override
            public void login() throws RemoteException {
                Log.e("sty --> ", "Binder_Server_Login_Service");
                //单向通信，真实的项目中跨进程都是双向通信，双向服务绑定的
                serviceStartActivity();
            }

            @Override
            public void loginCallback(boolean loginStatus, String loginUser) throws RemoteException {

            }
        };
    }

    //启动界面
    private void serviceStartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
