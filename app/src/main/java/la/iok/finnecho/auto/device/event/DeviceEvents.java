package la.iok.finnecho.auto.device.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Finn on 2016/9/21 0021.
 */
public class DeviceEvents {
    public static final String EVENT1 = "/dev/input/event1";
    public static final DeviceEvent EVENT1_TOUCH_RELEASE = new DeviceEvent(EVENT1, 3l, 57l, -1l);
    public static final DeviceEvent EVENT1_SUBMIT = new DeviceEvent(EVENT1, 0, 0, 0);

    public static final String EVENT3 = "/dev/input/event3";

    /**
     * 获取点击事件
     *
     * @param x
     * @param y
     * @return
     */
    public static List<DeviceEvent> getScreenClickEvent(int x, int y) {
        return null;
    }

    /**
     * 过滤掉不必要的事件
     *
     * @param deviceEvents
     * @param include
     * @return
     */
    public static List<DeviceEvent> filter(List<DeviceEvent> deviceEvents, String... include) {
        List<String> includeList = Arrays.asList(include);
        List<DeviceEvent> result = new ArrayList<>();
        for (DeviceEvent deviceEvent : deviceEvents) {
            if (includeList.contains(deviceEvent.getEventPath())) {
                result.add(deviceEvent);
            }
        }
        return result;
    }

    /**
     * 精简滑动事件
     *
     * @param deviceEvents
     * @return
     */
    public static List<DeviceEvent> compactSlide(List<DeviceEvent> deviceEvents, int level) {
        List<DeviceEvent> result = new ArrayList<>();
        Map<Integer, DeviceEventGroup> groups = new HashMap<>();

        for (DeviceEvent deviceEvent : deviceEvents) {

        }

        return result;
    }

}
