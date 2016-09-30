package la.iok.finnecho.auto.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Finn on 2016/9/18 0018.
 */
public class BaseService extends Service {
    /**
     * 所有服务都收集在这里，方便调用
     */
    protected static Map<Class, Service> services = new HashMap<>();

    /**
     * 通过这个方法可以获取系统中已经启动的服务
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Service> T getService(Class<T> clazz) {
        return (T) services.get(clazz);
    }

    /**
     * 重写服务开始方法，将自身（服务）放进services集合，方便获取
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        services.put(this.getClass(), this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
