package la.iok.finnecho.auto.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import la.iok.finnecho.auto.device.listener.UnlockListener;
import la.iok.finnecho.auto.handler.WeChatHandler;

/**
 * Created by Finn on 2016/9/21 0021.
 */

public class MainService extends BaseService {
    private UnlockListener unlockListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startService() {
        Intent hookService = new Intent(this, HookService.class);
        startService(hookService);
        bindService(hookService, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BaseService.getService(HookService.class).addListener(new WeChatHandler());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);

        this.unlockListener = new UnlockListener(getApplicationContext());
    }

    public void addListener(UnlockListener.ScreenStateListener listener) {
        unlockListener.addListener(listener);
    }

    public void removeListener(UnlockListener.ScreenStateListener listener){
        unlockListener.removeListener(listener);
    }

    public void toast(CharSequence text, int duration) {
        Toast.makeText(this, text, duration).show();
    }
}
