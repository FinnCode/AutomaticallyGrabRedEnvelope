package la.iok.finnecho.auto.device.event;

/**
 * Created by Finn on 2016/9/27 0027.
 */
public enum GroupType {
    //基本动作
    ScreenTouchDown, ScreenTouchUp, PowerButtonDown, PowerButtonUp,
    //复合动作
    ScreenClick, ScreenDoubleClick, ScreenLongClick, PowerButtonClick, PowerButtonDoubleClick
}
