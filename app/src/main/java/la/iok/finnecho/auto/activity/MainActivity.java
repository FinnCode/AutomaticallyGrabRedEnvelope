package la.iok.finnecho.auto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import la.iok.finnecho.auto.R;
import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.executor.PoolExecutor;
import la.iok.finnecho.auto.host.Host;
import la.iok.finnecho.auto.host.Setting;
import la.iok.finnecho.auto.service.MainService;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openPermissions();
        startServices();
        initSetting();
    }

    //界面上的绑定
    private void initSetting() {
        findViewById(R.id.autoUnlockSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked()) {
                    Toast.makeText(MainActivity.this, "5秒钟之后会自动锁屏，然后请用最快速度解锁一次，程序会记录解锁动作", Toast.LENGTH_LONG);
                    Simulation.getUnlockEventList(Host.userUnlockEventList);
                    Simulation.sendPowerButtonClick(5000l);
                    Setting.autoUnlock = true;
                    //todo Toast
                } else {
                    Setting.autoUnlock = false;
                }
            }
        });
    }

    //启动服务
    private void startServices() {
        startService(new Intent(this, MainService.class));
    }

    //获取相关权限
    private void openPermissions() {
        //root权限
        Simulation.getRoot();

        //监听辅助功能权限
        if (!isAccessibilitySettingsOn()) {
            Toast.makeText(getApplicationContext(), "请将抢红包服务设置为打开状态", Toast.LENGTH_LONG).show();
            openAccessbilitySettings();
        }
    }
}