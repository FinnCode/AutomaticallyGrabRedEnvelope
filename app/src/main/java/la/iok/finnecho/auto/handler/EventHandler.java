package la.iok.finnecho.auto.handler;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import java.util.Map;

import la.iok.finnecho.auto.service.HookService;
import la.iok.finnecho.auto.service.BaseService;

import static la.iok.finnecho.auto.service.BaseService.getService;

/**
 * Created by py on 2016/9/20 0020.
 */
public abstract class EventHandler {
    protected HookService service;

    public EventHandler(){
        service = getService(HookService.class);
    }

    public abstract void onWindowChanged(Map event);

    //检测到红包则打开界面
    public abstract void onNotification(Map event);
}
