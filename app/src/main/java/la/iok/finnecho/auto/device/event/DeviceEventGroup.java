package la.iok.finnecho.auto.device.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Finn on 2016/9/27 0027.
 */
public class DeviceEventGroup {

    private List<DeviceEvent> events = new ArrayList<>();

    private GroupType type = null;

    /**
     * 返回非空则标识不属于同一动作
     *
     * @param deviceEvent
     * @return
     */
    public void addEvent(DeviceEvent deviceEvent) {
        events.add(deviceEvent);
    }

    public List<DeviceEvent> getEvents() {
        return events;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }
}
