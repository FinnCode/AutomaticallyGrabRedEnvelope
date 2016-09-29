package la.iok.finnecho.auto.device.catcher;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import la.iok.finnecho.auto.device.event.DeviceEvent;

import static android.content.ContentValues.TAG;
import static la.iok.finnecho.auto.device.event.DeviceEvents.EVENT1_SUBMIT;
import static la.iok.finnecho.auto.device.event.DeviceEvents.EVENT1_TOUCH_RELEASE;

/**
 * Created by Finn on 2016/9/21 0021.
 */

public class EventCatcher {
    private Process process = null;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean started = false;
    private boolean isStoped = false;
    private List<DeviceEvent> events = new ArrayList<>();
    private Long startTime;
    private Thread catcherThread;

    public List<DeviceEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void startCatche() {
        if (started == false) {
            started = true;
            try {
                process = Runtime.getRuntime().exec("su\n");
                os = process.getOutputStream();
                os.write("\n\n\n\n\n\ngetevent\n".getBytes());
                os.flush();
                is = process.getInputStream();
                catcherThread = new Thread() {
                    @Override
                    public void run() {
                        StringBuffer buff = new StringBuffer();
                        Scanner in = null;
                        boolean release = false;
                        try {
                            in = new Scanner(is);
                            while (in.hasNextLine()) {
                                String line = in.nextLine();
                                if (line.startsWith("/dev/input/event")) {
                                    if (startTime == null) {
                                        startTime = System.currentTimeMillis();
                                    }
                                    DeviceEvent deviceEvent = new DeviceEvent(System.currentTimeMillis() - startTime, line);
                                    events.add(deviceEvent);
                                    if (isStoped) {
                                        if (!release && deviceEvent.equals(EVENT1_TOUCH_RELEASE)) { //监听到了手指放开
                                            release = true;
                                        } else if(release && deviceEvent.equals(EVENT1_SUBMIT)) { //监听到手指放开之后的一次提交操作
                                            break;
                                        }
                                    }
                                }
                            }
                        } finally {
                            if (in != null)
                                in.close();
                        }
                    }
                };
                catcherThread.start();
            } catch (Exception e) {
                Log.e(TAG, "the device is not rooted,  error message： " + e.getMessage(), e);
            }
        }
    }

    public void stopCatche() {
        if (isStoped == false) {
            isStoped = true;
            try {
                while (catcherThread.isAlive()) {
                    Thread.sleep(1000);
                }
                process.destroy();
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
