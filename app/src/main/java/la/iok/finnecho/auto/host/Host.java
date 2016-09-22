package la.iok.finnecho.auto.host;

import java.util.ArrayList;
import java.util.List;

import la.iok.finnecho.auto.device.event.DeviceEvent;

/**
 * Created by Finn on 2016/9/18 0018.
 */
public class Host {

    public static String version = "0.0.1"; //版本号

    public static double totle = 0; //总共抢到的金额

    public static List<DeviceEvent> userUnlockEventList = new ArrayList<>();//用户解锁动作
}
