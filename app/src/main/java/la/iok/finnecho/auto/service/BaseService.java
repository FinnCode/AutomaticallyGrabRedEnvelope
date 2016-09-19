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
    protected static Map<Class, Service> services = new HashMap<>();

    protected static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder instanceof ServiceBinder) {
                ServiceBinder binder = (ServiceBinder) iBinder;
                services.put(binder.getService().getClass(), binder.getService());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public static Service getService(Class clazz) {
        return services.get(clazz);
    }

    public static ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder(this);
    }

    protected static class ServiceBinder extends android.os.Binder {
        private Service service;

        public ServiceBinder(Service baseService) {
            this.service = baseService;
        }

        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public Service getService() {
            return this.service;
        }
    }
}
