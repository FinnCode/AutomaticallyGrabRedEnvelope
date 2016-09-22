package la.iok.finnecho.auto.handler;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.host.Host;
import la.iok.finnecho.auto.host.Setting;

import static la.iok.finnecho.auto.host.App.TAG;

/**
 * Created by py on 2016/9/20 0020.
 */
public class WeChatHandler extends EventHandler {

    protected int hasDetected = 0;
    protected int hasGrabed = 0;
    protected int recorded = 0;

    public void onWindowChanged(Map event) {
        if (event != null) {
            if (event.get("packageName").equals("com.tencent.mm")) {
                //这是聊天界面
                String className = (String) event.get("className");
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
                    List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByText("微信红包");
                    Collections.reverse(nodeInfos);
                    for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                        if (nodeInfo.getClassName().equals("android.widget.TextView")) {
                            if (hasGrabed < hasDetected) {
                                hasGrabed++;
                                Rect rect = new Rect();
                                nodeInfo.getParent().getBoundsInScreen(rect);
                                Simulation.sendScreenClick((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
                                break;
                            }
                        }
                    }
                }
                //这是红包界面
                if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
                    AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(3);
                    Rect rect = new Rect();
                    nodeInfo.getBoundsInScreen(rect);
                    Simulation.sendScreenClick((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2);
                    Toast.makeText(service, "果断抢" + (rect.left + rect.right) / 2 + "," + (rect.top + rect.bottom) / 2, Toast.LENGTH_LONG).show();
                }
                //这是红包领取后的界面
                if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    if (recorded < hasDetected) {
                        recorded++;
                        AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
                        AccessibilityNodeInfo moneyDetail = rootNodeInfo.getChild(0).getChild(0).getChild(0);
                        if (moneyDetail.getChildCount() == 6) {
                            //抢到了
                            double money = Double.parseDouble(moneyDetail.getChild(2).getText().toString());
                            Host.totle += money;
                            Toast.makeText(service.getApplicationContext(), "抢到红包" + money + "元\n" + "通过本软件总共抢到了" + Host.totle + "元了哦", Toast.LENGTH_LONG).show();
                        } else {
                            //没抢到
                            Toast.makeText(service.getApplicationContext(), "很遗憾~~再接再厉~~", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    //检测到红包则打开界面
    public void onNotification(Map event) {
        if (event != null) {
            try {
                if (event.get("packageName").equals("com.tencent.mm")) {
                    Notification notifucation = (Notification) event.get("parcelableData");
                    if (notifucation.tickerText.toString().contains("[微信红包]")) {
                        //判断屏幕是否打开
                        if(!Simulation.isScreenOn() && Setting.autoUnlock) {
                            Simulation.sendEvent(Host.userUnlockEventList);
                        }

                        hasDetected++;
                        (notifucation).contentIntent.send();
                    }
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
