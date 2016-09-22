package la.iok.finnecho.auto.device.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import la.iok.finnecho.auto.executor.PoolExecutor;

/**
 * Created by Finn on 2016/9/21 0021.
 */

public class UnlockListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private Collection<ScreenStateListener> listeners = Collections.synchronizedCollection(new ArrayList<ScreenStateListener>());

    public UnlockListener(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
        start();
    }

    public void addListener(ScreenStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ScreenStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            for (ScreenStateListener listener : listeners) {
                if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                    PoolExecutor.execute(new ScreenOnRunnable(listener));
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                    PoolExecutor.execute(new ScreenOffRunnable(listener));
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                    PoolExecutor.execute(new ScreenOnUnlockRunnable(listener));
                }
            }
        }
    }


    /**
     * 启动screen状态广播接收器
     */
    private void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public static abstract class ScreenStateListener {// 返回给调用者屏幕状态信息
        public void onScreenOn() {
        }
        public void onScreenOff() {
        }
        public void onUnlock() {
        }
    }

    private static class ScreenOnRunnable implements Runnable {
        private ScreenStateListener listener;

        ScreenOnRunnable(ScreenStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.onScreenOn();
        }
    }

    private static class ScreenOffRunnable implements Runnable {
        private ScreenStateListener listener;

        ScreenOffRunnable(ScreenStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.onScreenOff();
        }
    }

    private static class ScreenOnUnlockRunnable implements Runnable {
        private ScreenStateListener listener;

        ScreenOnUnlockRunnable(ScreenStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.onUnlock();
        }
    }
}