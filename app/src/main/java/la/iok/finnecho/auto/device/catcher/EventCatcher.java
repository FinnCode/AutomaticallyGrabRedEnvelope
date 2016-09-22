package la.iok.finnecho.auto.device.catcher;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import la.iok.finnecho.auto.device.event.DeviceEvent;
import la.iok.finnecho.auto.host.Setting;

import static android.content.ContentValues.TAG;

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
    private Object mutex = new Object();

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
                new Thread() {
                    @Override
                    public void run() {
                        Scanner in = new Scanner(is);
                        while (in.hasNextLine()) {
                            String line = in.nextLine();
                            if (line.startsWith("/dev/input/event")) {
                                if (startTime == null) {
                                    startTime = System.currentTimeMillis();
                                }
                                events.add(new DeviceEvent(System.currentTimeMillis() - startTime, line));
                                Log.i(TAG,"event " + line);
                            }
                        }
                    }
                }.start();
            } catch (Exception e) {
                Log.e(TAG, "the device is not rooted,  error message： " + e.getMessage(), e);
            }
        }
    }

    public void stopCatche() {
        if (isStoped == false) {
            isStoped = true;
            process.destroy();
        }
    }

    public void waitFor() {
        while (isStoped == false) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
