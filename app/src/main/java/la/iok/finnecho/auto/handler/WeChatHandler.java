package la.iok.finnecho.auto.handler;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import la.iok.finnecho.auto.device.Simulation;
import la.iok.finnecho.auto.host.Host;
import la.iok.finnecho.auto.host.Setting;

import static la.iok.finnecho.auto.host.App.TAG;

/**
 * Created by py on 2016/9/20 0020.
 */
public class WeChatHandler extends EventHandler {

    private List<Object> mSourceNodeIds = new ArrayList<>();

    private static Method getSourceNodeIdMethod = null;

    private Thread monitorChatInterfaceThread = null;

    private boolean monitorChatInterfaceThreadEnable = false;

    public WeChatHandler() {
        try {
            getSourceNodeIdMethod = AccessibilityNodeInfo.class.getMethod("getSourceNodeId");
        } catch (NoSuchMethodException e) {
            Log.e("finnecho.ERROR", e.getMessage(), e);
        }
    }

    /**
     * 界面变化的回调方法，检测微信界面，并发送屏幕点击事件模拟用户点击红包
     *
     * @param event
     */
    public void onWindowChanged(Map event) {
        if (event != null) {
            if (event.get("packageName").equals("com.tencent.mm")) {
                String className = (String) event.get("className");
                //这是聊天界面
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    //启动刷红包线程
                    start();
                } else {
                    //停止刷红包线程
                    stop();
                }
                //这是红包界面
                if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
                    AccessibilityNodeInfo nodeInfo = rootNodeInfo.getChild(3);
                    Simulation.clickNodeWithRandom(nodeInfo, 10000, 20);
                    rootNodeInfo.recycle();
                    nodeInfo.recycle();
                    Toast.makeText(service, "果断抢", Toast.LENGTH_LONG).show();
                }
                //这是红包领取后的界面
                if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
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
                    rootNodeInfo.recycle();
                    moneyDetail.recycle();
                }
            }
        }
    }

    private void stop() {
        monitorChatInterfaceThreadEnable = false;
    }

    private void start() {
        monitorChatInterfaceThreadEnable = true;
        if (monitorChatInterfaceThread == null) {
            monitorChatInterfaceThread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.e("finnecho.ERROR", "中断");
                        }

                        if (monitorChatInterfaceThreadEnable) {
                            AccessibilityNodeInfo rootNodeInfo = service.getRootInActiveWindow();
                            List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByText("微信红包");
                            Collections.reverse(nodeInfos);
                            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                                Object nodeId = null;
                                if (getSourceNodeIdMethod != null) {
                                    try {
                                        nodeId = getSourceNodeIdMethod.invoke(nodeInfo);
                                    } catch (Exception e) {
                                        Log.e("finnecho.ERROR", "获取nodeId失败", e);
                                    }
                                }
                                if (nodeInfo.getClassName().equals("android.widget.TextView") && (nodeId == null || !mSourceNodeIds.contains(nodeId))) {
                                    Simulation.clickNodeWithRandom(nodeInfo, 10000, 20);
                                    if (nodeId != null) {
                                        mSourceNodeIds.add(nodeId);
                                    }
                                    break;
                                }
                            }

                            rootNodeInfo.recycle();
                            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                                nodeInfo.recycle();
                            }
                        }
                    }
                }
            };
            monitorChatInterfaceThread.setDaemon(true);
            monitorChatInterfaceThread.start();
        }
    }


    /**
     * 通知事件回调方法，筛选出带有"[微信红包]"字样的通知，并点击进入界面
     *
     * @param event
     */
    public void onNotification(Map event) {
        if (event != null) {
            try {
                if (event.get("packageName").equals("com.tencent.mm")) {
                    Notification notifucation = (Notification) event.get("parcelableData");
                    if (notifucation.tickerText.toString().contains("[微信红包]")) {
                        //判断屏幕是否打开
                        if (!Simulation.isScreenOn() && Setting.autoUnlock) {
                            Simulation.sendEvent(Host.userUnlockEventList);
                        }

                        (notifucation).contentIntent.send();
                    }
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
