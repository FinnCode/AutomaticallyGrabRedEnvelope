package la.iok.finnecho.auto.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import la.iok.finnecho.auto.host.App;
import la.iok.finnecho.auto.host.Setting;

/**
 * Created by Finn on 2016/9/21 0021.
 */

public class CorventUtils {

    /**
     * 将对象转换为Map的方法，由于安卓没有，所以自己写了一个，不造有没有BUG
     * @param o
     * @return
     */
    public static Map<String, Object> corventToMap(Object o) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Field[] fields = o.getClass().getFields();
            List<Method> methods = new ArrayList<>();
            methods.addAll(Arrays.asList(o.getClass().getMethods()));
            Class parent = o.getClass();
            while (parent.getGenericSuperclass() != Object.class) {
                parent = (Class) parent.getGenericSuperclass();
                methods.addAll(Arrays.asList(parent.getMethods()));
            }
            for (Field field : fields) {
                field.setAccessible(true);
                String proName = field.getName();
                Object proValue = field.get(o);
                map.put(proName.toUpperCase(), proValue);
            }
            for (Method method : methods) {
                String name = method.getName();
                if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                    try {
                        method.setAccessible(true);
                        Object value = method.invoke(o);
                        map.put(getFieldNameForGetter(name), value);
                    } catch (Exception e) {
                        throw new Exception("name:" + name, e);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            Log.e(App.TAG, "转化为MAP异常", e);
        }
        return null;
    }

    /**
     * 根据Getter方法获取属性的名称
     * @param name
     * @return
     */
    private static String getFieldNameForGetter(String name) {
        name = name.substring(3);
        String first = name.substring(0, 1).toLowerCase();
        return first + name.substring(1);
    }
}
