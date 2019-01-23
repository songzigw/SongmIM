package cn.songm.songmq.core.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String html(String content) {
        return content.replace("\\r\\n", "<br />");
    }

    public static boolean find(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean matches(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static String replace(String str, String regex, String newStr) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.replaceAll(newStr);
    }

    public static String[] split(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        return p.split(str);
    }

    public static String[] split(String str, String regex, int count) {
        Pattern p = Pattern.compile(regex);
        return p.split(str, count);
    }

    public static boolean isEmptyOrNull(String param) {
        return (param == null) || (param.trim().equals(""));
    }
    
    public static boolean isBlank(String param) {
        return isEmptyOrNull(param);
    }
    
    public static boolean isNotBlank(String param) {
        return !isEmptyOrNull(param);
    }

    public static Long parseLong(String param) {
        if ((param == null) || (param.trim().equals(""))) {
            return null;
        }
        return Long.valueOf(param.trim());
    }

    public static Integer parseInt(String param) {
        if ((param == null) || (param.trim().equals(""))) {
            return null;
        }
        return Integer.valueOf(param.trim());
    }

    public static Float parseFloat(String param) {
        if ((param == null) || (param.trim().equals(""))) {
            return null;
        }
        return Float.valueOf(param.trim());
    }

    public static Boolean parseBoolean(String param) {
        if ((param == null) || (param.trim().equals(""))) {
            return null;
        }
        return Boolean.valueOf(param.trim());
    }

    public static String convertLongToStr(Long value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String convertDateToStr(Date value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 获得文件名的后缀名
     * 
     * @param fileName
     * @return
     */
    public static String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取去掉横线的长度为32的UUID串
     * 
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取带横线的长度为36的UUID串
     * 
     * @return
     */
    public static String get36UUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * 函数功能说明 ： 判断对象数组是否为空. 修改者名字： 修改日期： 修改内容：
     * 
     * @参数： @param obj
     * @参数： @return
     * @return boolean
     * @throws
     */
    public static boolean isEmpty(Object[] obj) {
        return null == obj || 0 == obj.length;
    }

    /**
     * 函数功能说明 ： 判断对象是否为空. 修改者名字： 修改日期： 修改内容：
     * 
     * @参数： @param obj
     * @参数： @return
     * @return boolean
     * @throws
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        return !(obj instanceof Number) ? false : false;
    }

    /**
     * 函数功能说明 ： 判断集合是否为空. 修改者名字： 修改日期： 修改内容：
     * 
     * @参数： @param obj
     * @参数： @return
     * @return boolean
     * @throws
     */
    public static boolean isEmpty(List<?> obj) {
        return null == obj || obj.isEmpty();
    }

    /**
     * 函数功能说明 ： 判断Map集合是否为空. 修改者名字： 修改日期： 修改内容：
     * 
     * @参数： @param obj
     * @参数： @return
     * @return boolean
     * @throws
     */
    public static boolean isEmpty(Map<?, ?> obj) {
        return null == obj || obj.isEmpty();
    }
}