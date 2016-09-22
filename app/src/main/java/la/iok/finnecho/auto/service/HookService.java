package la.iok.finnecho.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import la.iok.finnecho.auto.executor.PoolExecutor;
import la.iok.finnecho.auto.handler.EventHandler;
import la.iok.finnecho.auto.utils.CorventUtils;

/**
 * Created by Finn on 2016/9/18 0018.
 */
@SuppressLint("NewApi")
public class HookService extends AccessibilityService {

    private Collection<EventHandler> listener = Collections.synchronizedCollection(new ArrayList<EventHandler>());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                if (event.getClassName().equals("android.app.Notification")) {
                    notifyNotification(event);
                } else if (event.getClassName().equals("android.widget.Toast$TN")) {

                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //界面状态改变
                notifyWindowChanged(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:   //点击事件
                break;
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT: //文本改变
                break;
            //省略其他的一堆可以监听的事件
        }
    }

    private void notifyWindowChanged(final AccessibilityEvent event) {
        for (final EventHandler handler : listener) {
            PoolExecutor.execute(new NotifyWindowChangedRunable(handler, CorventUtils.PO2Map(event)));
        }
    }

    private void notifyNotification(final AccessibilityEvent event) {
        for (final EventHandler handler : listener) {
            PoolExecutor.execute(new NotifyNotificationRunable(handler, CorventUtils.PO2Map(event)));
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(HookService.this, "onInterrupt", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BaseService.services.put(this.getClass(), this);
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    public void addListener(EventHandler eventHandler) {
        this.listener.add(eventHandler);
    }

    private static abstract class EventNotifyRunnable implements Runnable {
        protected final Map event;
        protected final EventHandler handler;

        public EventNotifyRunnable(EventHandler handler, Map event) {
            this.handler = handler;
            this.event = event;
        }
    }

    private static class NotifyWindowChangedRunable extends EventNotifyRunnable {
        public NotifyWindowChangedRunable(EventHandler handler, Map<String, Object> event) {
            super(handler, event);
        }

        @Override
        public void run() {
            handler.onWindowChanged(event);
        }
    }

    private class NotifyNotificationRunable extends EventNotifyRunnable {

        public NotifyNotificationRunable(EventHandler handler, Map<String, Object> event) {
            super(handler, event);
        }
        @Override
        public void run() {
            handler.onNotification(event);
        }
    }
}
