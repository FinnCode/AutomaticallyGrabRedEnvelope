package la.iok.finnecho.auto.device.event;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Finn on 2016/9/21 0021.
 */
public class DeviceEvent {
    private static Set<String> chmod = new HashSet<>();

    private String eventPath;
    private long[] param;

    protected long timeout;

    public DeviceEvent(long timeout, String consoleOutEvent) {
        String[] split = consoleOutEvent.split(" ");
        eventPath = split[0].replace(":", "");
        param = new long[split.length - 1];
        for (int i = 0; i < split.length - 1; i++) {
            String value = split[i + 1];
            if (!value.equals("ffffffff")) {
                param[i] = Long.parseLong(value, 16);
            } else {
                param[i] = -1;
            }
        }
        this.timeout = timeout;
    }

    public DeviceEvent(String event, long... param) {
        this.eventPath = event;
        this.param = param;
    }

    public DeviceEvent(long timeout, String eventPath, long[] param) {
        this.timeout = timeout;
        this.eventPath = eventPath;
        this.param = param;
    }

    public boolean isChmod() {
        return chmod.contains(eventPath);
    }

    public String getChmodShell() {
        return "chmod 777 " + eventPath;
    }

    public String getShell() {
        String result = eventPath + " ";
        for (long l : param) {
            result += l + " ";
        }
        return result;
    }

    public void setParam(long[] param) {
        this.param = param;
    }

    public void setEventPath(String eventPath) {
        this.eventPath = eventPath;
    }

    public String getEventPath() {
        return eventPath;
    }

    public long[] getParam() {
        return param;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setChmod() {
        chmod.add(eventPath);
    }

    @Override
    public String toString() {
        return getShell();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DeviceEvent)) {
            return false;
        }
        if (!getShell().equals(((DeviceEvent) obj).getShell())) {
            return false;
        }
        return true;
    }
}
