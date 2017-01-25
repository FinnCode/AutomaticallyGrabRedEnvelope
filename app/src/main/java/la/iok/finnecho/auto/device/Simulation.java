package la.iok.finnecho.auto.device;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import la.iok.finnecho.auto.device.catcher.EventCatcher;
import la.iok.finnecho.auto.device.event.DeviceEvent;
import la.iok.finnecho.auto.device.event.DeviceEvents;
import la.iok.finnecho.auto.device.listener.UnlockListener;
import la.iok.finnecho.auto.executor.PoolExecutor;
import la.iok.finnecho.auto.host.App;
import la.iok.finnecho.auto.service.BaseService;
import la.iok.finnecho.auto.service.HookService;
import la.iok.finnecho.auto.service.MainService;

/**
 * Created by Finn on 2016/9/18 0018.
 */
public class Simulation {
    private static boolean isRoot = false;
    private static Process process = null;
    private static BufferedOutputStream os = null;
    private static BufferedInputStream is = null;


    /**
     * 获取ROOT权限
     * 模拟驱动必须先获取ROOT权限，否则请用AccessibilityService的方式
     *
     * @return 是否成功
     */
    public static boolean getRoot() {
        if (isRoot) {
            return true;
        }
        try {
            process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if (0 == i) {
                process = Runtime.getRuntime().exec("su");
                isRoot = true;
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;

    }

    public static void sendPowerButtonClick(long delay) {
        PoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                sendPowerButtonClick();
            }
        }, delay);
    }

    /**
     * 发送电源键点击事件，通过模拟驱动事件的方式，几乎等同于用户直接按电源键
     */
    public static void sendPowerButtonClick() {
        if (!getRoot()) {
            try {
                os.write("chmod 777 /dev/input/event3\n".getBytes());
                os.write("sendevent /dev/input/event3 1 116 1\n".getBytes());
                os.write("sendevent /dev/input/event3 0 0 0\n".getBytes());
                os.write("sendevent /dev/input/event3 1 116 0\n".getBytes());
                os.write("sendevent /dev/input/event3 0 0 0\n".getBytes());
                os.flush();
            } catch (IOException e) {
                Log.e(App.TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 点击屏幕元素
     * 带有一定随机性的屏幕点击事件，可以通过外挂检测，对坐标轴和压力进行随机化，没有随机延时
     *
     * @param nodeInfo  元素
     * @param intensity 按压力度
     * @param level     随机力度
     * @return 是否发送事件成功
     */
    public static void clickNodeWithRandom(AccessibilityNodeInfo nodeInfo, int intensity, int level) {
        if (getRoot()) {
            Rect rect = new Rect();
            nodeInfo.getParent().getBoundsInScreen(rect);
            int x = (rect.left + rect.right) / 2;
            int y = (rect.top + rect.bottom) / 2;
            x = (int) (x + Math.round(Math.random() * 2 * level - level));
            y = (int) (y + Math.round(Math.random() * 2 * level - level));
            intensity = (int) (intensity + Math.round(Math.random() * 2 * level - level));
            sendScreenClick(x, y, intensity);
        } else {
            AccessibilityNodeInfo n = nodeInfo;
            while (n != null) {
                if (n.isClickable()) {
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                n = n.getParent();
            }
        }
    }

    /**
     * 发送屏幕点击事件，通过模拟驱动事件的方式，几乎等同于用户直接点击屏幕
     * 带有一定随机性的屏幕点击事件，可以通过外挂检测，对坐标轴和压力进行随机化，没有随机延时
     *
     * @param x         屏幕的x轴坐标
     * @param y         屏幕的y轴坐标
     * @param intensity 按压力度
     * @param level     随机力度
     * @return 是否发送事件成功
     */
    public static boolean sendScreenClickWithRandom(int x, int y, int intensity, int level) {
        x = (int) (x + Math.round(Math.random() * 2 * level - level));
        y = (int) (y + Math.round(Math.random() * 2 * level - level));
        intensity = (int) (intensity + Math.round(Math.random() * 2 * level - level));
        return sendScreenClick(x, y, intensity);
    }

    /**
     * 发送屏幕点击事件，通过模拟驱动事件的方式，几乎等同于用户直接点击屏幕
     *
     * @param x         屏幕的x轴坐标
     * @param y         屏幕的y轴坐标
     * @param intensity 按压力度
     * @return 是否发送事件成功
     */
    public static boolean sendScreenClick(int x, int y, int intensity) {
        if (getRoot()) {
            try {
                os.write("chmod 777 /dev/input/event1\n".getBytes());
                os.write("sendevent /dev/input/event1 0 4 84165\n".getBytes());
                os.write("sendevent /dev/input/event1 0 5 426793538\n".getBytes());
                os.write(("sendevent /dev/input/event1 3 57 " + intensity + "\n").getBytes());
                os.write(("sendevent /dev/input/event1 3 53 " + x + "\n").getBytes());
                os.write(("sendevent /dev/input/event1 3 54 " + y + "\n").getBytes());
                os.write("sendevent /dev/input/event1 0 0 0\n".getBytes());
                os.write("sendevent /dev/input/event1 0 4 84165\n".getBytes());
                os.write("sendevent /dev/input/event1 0 5 494475576\n".getBytes());
                os.write("sendevent /dev/input/event1 3 57 -1\n".getBytes());
                os.write("sendevent /dev/input/event1 0 0 0\n".getBytes());
                os.flush();
            } catch (IOException e) {
                Log.e(App.TAG, e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取屏幕方向
     *
     * @return 1:90° 2:180° 3:270° 4:360°
     */
    public static int getScreenChange() {
        Configuration mConfiguration = BaseService.getService(HookService.class).getResources().getConfiguration(); //获取设置的配置信息
        return mConfiguration.orientation; //获取屏幕方向
    }

    /**
     * 判断屏幕是否处于打开状态
     *
     * @return
     */
    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) BaseService.getService(HookService.class).getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (pm.isInteractive()) {
                return true;
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            if (pm.isScreenOn()) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * 录制并获取用户解锁屏幕所产生的驱动事件，获取到的事件直接重放即可解锁屏幕
     * 该方法执行之后会在屏幕锁定之后开始录制，直到屏幕解锁停止录制，并回调返回结果
     *
     * @param userUnlockEventList
     */
    public static void getUnlockEventList(final List<DeviceEvent> userUnlockEventList) {
        //todo 这个对象要从这个类中移除，降低耦合性
        userUnlockEventList.clear();
        final EventCatcher eventCatcher = new EventCatcher();
        BaseService.getService(MainService.class).addListener(new UnlockListener.ScreenStateListener() {
            @Override
            public void onScreenOff() {
                eventCatcher.startCatche();
            }

            @Override
            public void onUnlock() {
                BaseService.getService(MainService.class).removeListener(this);
                eventCatcher.stopCatche();
                userUnlockEventList.addAll(DeviceEvents.compactSlide(DeviceEvents.filter(eventCatcher.getEvents(), DeviceEvents.EVENT1, DeviceEvents.EVENT3), 10));
            }
        });
    }

    /**
     * 向系统发送驱动事件
     *
     * @param userUnlockEventList
     */
    public static void sendEvent(List<DeviceEvent> userUnlockEventList) {
        try {
            long totleSleep = 0;
            StringBuffer chmods = new StringBuffer();
            StringBuffer shells = new StringBuffer();
            for (DeviceEvent deviceEvent : userUnlockEventList) {
                shells.append("sleep " + (double) (deviceEvent.getTimeout() - totleSleep) / 1000 + "\n");
                totleSleep += deviceEvent.getTimeout();
                if (!deviceEvent.isChmod()) {
                    chmods.append(deviceEvent.getChmodShell() + "\n");
                    deviceEvent.setChmod();
                }
                shells.append("sendevent " + deviceEvent.getShell() + "\n");
            }
            os.write(chmods.toString().getBytes());
            os.write(shells.toString().getBytes());
        } catch (IOException e) {
            Log.e(App.TAG, e.getMessage(), e);
        }
    }
}
