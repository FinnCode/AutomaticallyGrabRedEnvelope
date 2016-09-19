package la.iok.finnecho.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Created by Finn on 2016/9/18 0018.
 */
public class MainService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //界面状态改变
                break;
            case  AccessibilityEvent.TYPE_VIEW_CLICKED:   //点击事件
                break;
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT: //文本改变
                break;
            //省略其他的一堆可以监听的事件
        }
        Toast.makeText(MainService.this, event.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(MainService.this, "onInterrupt", Toast.LENGTH_SHORT).show();
    }
}
