package la.iok.finnecho.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import la.iok.finnecho.auto.device.Simulation;

import static la.iok.finnecho.auto.host.Host.TAG;

/**
 * Created by Finn on 2016/9/18 0018.
 */
@SuppressLint("NewApi")
public class HookService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                if (event.getClassName().equals("android.app.Notification")) {
                    handlerNotification(event);
                } else if (event.getClassName().equals("android.widget.Toast$TN")) {

                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //界面状态改变
                handlerWindowChanged(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:   //点击事件
                break;
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT: //文本改变
                break;
            //省略其他的一堆可以监听的事件
        }
    }

    private void handlerWindowChanged(AccessibilityEvent event) {
        if (event.getPackageName().equals("com.tencent.mm")) {
            //这是聊天界面
            if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
                List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByText("微信红包");
                Collections.reverse(nodeInfos);
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    if (nodeInfo.getClassName().equals("android.widget.TextView")) {
                        Rect rect = new Rect();
                        nodeInfo.getParent().getBoundsInScreen(rect);
                        Simulation.sendScreenClick((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
                        break;
                    }
                }
            }
            //这是红包界面
            if (event.getClassName().equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
                AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(3);
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                Simulation.sendScreenClick((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
                Toast.makeText(this, "果断抢" + (rect.left + rect.right) / 2 + "," + (rect.top + rect.bottom) / 2, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //检测到红包则打开界面
    private void handlerNotification(AccessibilityEvent event) {
        try {
            if (event.getPackageName().equals("com.tencent.mm")) {
                Toast.makeText(this, event.getBeforeText(), Toast.LENGTH_SHORT).show();
                Notification notifucation = (Notification) event.getParcelableData();
                if (notifucation.tickerText.toString().contains("[微信红包]")) {
                    (notifucation).contentIntent.send();
                }
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(HookService.this, "onInterrupt", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

}
