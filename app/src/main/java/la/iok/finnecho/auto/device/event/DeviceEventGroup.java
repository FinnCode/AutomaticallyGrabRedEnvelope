package la.iok.finnecho.auto.device.event;

import java.util.ArrayList;
import java.util.List;

import static la.iok.finnecho.auto.device.event.DeviceEvents.EVENT1;
import static la.iok.finnecho.auto.device.event.DeviceEvents.EVENT3;

/**
 * Created by Finn on 2016/9/27 0027.
 */
public class DeviceEventGroup {

    private List<DeviceEvent> events = new ArrayList<>();
    private List<DeviceEvent> deviceEvents = new ArrayList<>();

    private GroupType type = null;

    /**
     * 返回非空则标识不属于同一动作
     * @param deviceEvent
     * @return
     */
    public List<DeviceEvent> addEvent(DeviceEvent deviceEvent){
        if (deviceEvent.getEventPath().equals(EVENT1)) {

        } else if(deviceEvent.getEventPath().equals(EVENT3)) {

        }
        return null;
    }

    public List<DeviceEvent> getEvents() {
        return events;
    }

    public GroupType getType() {
        return type;
    }
}
