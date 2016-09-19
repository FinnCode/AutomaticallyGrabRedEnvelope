package la.iok.finnecho.auto.activity;

import android.content.Intent;
import android.os.Bundle;

import la.iok.finnecho.auto.R;
import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.service.BaseService;
import la.iok.finnecho.auto.service.MainService;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openPermissions();
        startServices();
    }

    //启动服务
    private void startServices() {
        Intent mainService = new Intent(MainActivity.this, MainService.class);
        startService(mainService);
        bindService(mainService, BaseService.getServiceConnection(), BIND_AUTO_CREATE);
    }

    //获取相关权限
    private void openPermissions() {
        //root权限
        Simulation.getRoot();

//        //监听通知栏权限
//        if (!isOpenNotificationReadPermission()) {
//            Toast.makeText(getApplicationContext(), "请允许本软件监听通知信息", Toast.LENGTH_LONG);
//            openNotificationReadPermission();
//        }
    }
}