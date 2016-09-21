package la.iok.finnecho.auto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import la.iok.finnecho.auto.R;
import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.handler.WeChatHandler;
import la.iok.finnecho.auto.service.HookService;

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
        Intent mainService = new Intent(this, HookService.class);
        startService(mainService);
        Intent weChatHandler = new Intent(this, WeChatHandler.class);
        startService(weChatHandler);
    }

    //获取相关权限
    private void openPermissions() {
        //root权限
        Simulation.getRoot();

        //监听辅助功能权限
        if (!isAccessibilitySettingsOn()) {
            Toast.makeText(getApplicationContext(), "请允许本软件的辅助功能", Toast.LENGTH_LONG).show();
            openAccessbilitySettings();
        }
    }
}