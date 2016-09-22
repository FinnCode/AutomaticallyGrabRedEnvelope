package la.iok.finnecho.auto.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * JSON转换工具类
 */
public final class JsonGsonUtils {

    private static final Gson gson = new Gson();
//    private static final Log LOG = LogFactory.getLog(GsonUtil.class);

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

    public static void main(String[] args) {
        String str = "{\"Code\":2,\"Mes\":{\"UserID\":\"960\",\"UserName\":\"\",\"Status\":\"1\",\"Sex\":\"0\",\"Serial\":\"1020153000000001\",\"School\":\"浙江省镇海中学\",\"Roles\":\"2\",\"RealName\":\"赵老师\",\"QQ\":\"\",\"Handphone\":\"17098018090\",\"Email\":\"\",\"HeaderPic\":\"http://www.ikuko.test/Scripts/images/img8.jpg\",\"LoginCount\":\"156\"}}";
        Map map = jsonToJava(str, Map.class);
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
//            LOG.error("error in toJsonBytes", e);
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
