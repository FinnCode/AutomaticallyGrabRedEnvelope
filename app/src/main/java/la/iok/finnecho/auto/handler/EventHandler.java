package la.iok.finnecho.auto.handler;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import la.iok.finnecho.auto.service.HookService;
import la.iok.finnecho.auto.service.BaseService;

/**
 * Created by py on 2016/9/20 0020.
 */
public abstract class EventHandler extends BaseService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getService(HookService.class).addListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public abstract void onWindowChanged(AccessibilityEvent event);

    //检测到红包则打开界面
    public abstract void onNotification(AccessibilityEvent event);
}
