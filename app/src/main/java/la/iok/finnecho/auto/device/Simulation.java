package la.iok.finnecho.auto.device;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import la.iok.finnecho.auto.device.catcher.EventCatcher;
import la.iok.finnecho.auto.device.event.DeviceEvent;
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
    private static DataOutputStream os = null;
    private static DataInputStream is = null;

    public static boolean getRoot() {
        try {
            process = Runtime.getRuntime().exec("su\n");
            os = new DataOutputStream(process.getOutputStream());
            os.flush();
            is = new DataInputStream(process.getInputStream());
        } catch (Exception e) {
            Log.e(App.TAG, "ROOT权限获取失败, 错误信息： " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static void sendPowerButtonClick(long delay) {
        PoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                sendPowerButtonClick();
            }
        }, delay);
    }

    public static void sendPowerButtonClick() {
        if (!isRoot) {
            isRoot = getRoot();
        }
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

    public static boolean sendScreenClick(int x, int y) {
        if (!isRoot) {
            isRoot = getRoot();
        }

        try {
            os.write("chmod 777 /dev/input/event1\n".getBytes());
            os.write("sendevent /dev/input/event1 0 4 144\n".getBytes());
            os.write("sendevent /dev/input/event1 0 5 2\n".getBytes());
            os.write("sendevent /dev/input/event1 0 4 84165\n".getBytes());
            os.write("sendevent /dev/input/event1 0 5 426793538\n".getBytes());
            os.write("sendevent /dev/input/event1 3 57 13228\n".getBytes());
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

    public static int getScreenChange() {
        Configuration mConfiguration = BaseService.getService(HookService.class).getResources().getConfiguration(); //获取设置的配置信息
        return mConfiguration.orientation; //获取屏幕方向
    }

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


    public static void getUnlockEventList(final List<DeviceEvent> userUnlockEventList) {
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
                userUnlockEventList.addAll(eventCatcher.getEvents());
                sendEvent(userUnlockEventList);
            }
        });
    }

    public static void sendEvent(List<DeviceEvent> userUnlockEventList) {
        try {
            long startTime = System.currentTimeMillis();
            for (DeviceEvent deviceEvent : userUnlockEventList) {
                long sleepTime = deviceEvent.getTimeout() - System.currentTimeMillis() + startTime;
                if (sleepTime > 0)
                Thread.sleep(sleepTime);
                if (!deviceEvent.isChmod()){
                    os.write(deviceEvent.getChmodShell().getBytes());
                    deviceEvent.setChmod();
                }
                os.write(("sendevent " + deviceEvent.getShell()).getBytes());
            }
        } catch (InterruptedException e) {
            Log.e(App.TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(App.TAG, e.getMessage(), e);
        }
    }
}
