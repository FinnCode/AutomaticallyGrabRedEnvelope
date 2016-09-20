package la.iok.finnecho.auto.handler;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.service.HookService;

import static la.iok.finnecho.auto.host.Setting.TAG;

/**
 * Created by py on 2016/9/20 0020.
 */
public class WeChatHandler extends EventHandler {
    public void onWindowChanged(AccessibilityEvent event) {
        if (event.getPackageName().equals("com.tencent.mm")) {
            //这是聊天界面
            if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                AccessibilityNodeInfo rootNodeInfo = getService(HookService.class).getRootInActiveWindow();
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
                AccessibilityNodeInfo rootNodeInfo = getService(HookService.class).getRootInActiveWindow();
                AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(3);
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                Simulation.sendScreenClick((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
                Toast.makeText(this, "果断抢" + (rect.left + rect.right) / 2 + "," + (rect.top + rect.bottom) / 2, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //检测到红包则打开界面
    public void onNotification(AccessibilityEvent event) {
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
}
