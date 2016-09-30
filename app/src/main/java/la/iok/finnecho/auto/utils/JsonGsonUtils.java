package la.iok.finnecho.auto.utils;

import android.nfc.Tag;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import la.iok.finnecho.auto.host.App;

/**
 * JSON转换工具类
 */
public final class JsonGsonUtils {

    private static final Gson gson = new Gson();

    /**
     * 如果json的内容是简单的javabean对象，使用该方法把json转换为java对象
     *
     * @param content
     * @param classOfT
     * @return
     */
    public static <T> T jsonToJava(String content, Class<T> classOfT) {
        return gson.fromJson(content, classOfT);
    }

    /**
     * 如果json的内容是简单的javabean对象，使用该方法把json转换为java对象
     *
     * @param content
     * @param type
     * @return
     */

    public static <T> T jsonToJava(String content, TypeToken<T> type) {
        return gson.fromJson(content,type.getType());
    }
    /**
     * 把java对象转换为json字符串
     *
     * @param value
     * @return
     */
    public static String toJsonString(Object value) {
        if (value != null) {
            return gson.toJson(value);
        }
        return "";
    }

    /**
     * 把java对象转换为json字符串的byte数组
     *
     * @param value
     * @return
     */
    public static byte[] toJsonBytes(Object value) {
        try {
            return toJsonString(value).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(App.TAG, "转换成Json字符串的时候出错", e);
        }
        return new byte[0];
    }

    public static String chineseEncoding(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (char c : string.toCharArray()) {
            if (c >= 0x2E80 && c <= 0xA4CF || c >= 0xF900 && c <= 0xFAFF ||
            c >= 0xFE30 && c <= 0xFE4F || c >= 0xFF00 && c <= 0xFFEF){
                stringBuffer.append("\\u");
                Integer unicode = (int) c;
                stringBuffer.append(Integer.toHexString(unicode));
            }
            else
                stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }
}
