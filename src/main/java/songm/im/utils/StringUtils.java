package songm.im.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static String html(String content) {
		return content.replace("\\r\\n", "<br />");
	}

	public static boolean find(String str, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		boolean b = m.find();
		return b;
	}

	public static boolean match(String str, String regex) {
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		boolean b = m.matches();
		return b;
	}

	public static String replace(String str, String regex, String newStr) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);

		String s = m.replaceAll(newStr);
		return s;
	}

	public static String[] split(String str, String regex, int count) {
		Pattern p = Pattern.compile(regex);

		return p.split(str, count);
	}

	public static Long parseLong(String param) {
		if ((param == null) || (param.trim().equals(""))) {
			return null;
		}
		return Long.valueOf(Long.parseLong(param));
	}

	public static Integer parseInt(String param) {
		if ((param == null) || (param.trim().equals(""))) {
			return null;
		}
		return Integer.valueOf(Integer.parseInt(param));
	}

	public static boolean isEmptyOrNull(String param) {
		return (param == null) || (param.trim().equals(""));
	}

	public static Float parseFloat(String param) {
		if ((param == null) || (param.trim().equals(""))) {
			return null;
		}
		return Float.valueOf(Float.parseFloat(param));
	}

	public static Boolean parseBoolean(String param) {
		if ((param == null) || (param.trim().equals(""))) {
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(Boolean.parseBoolean(param));
	}

	public static void main(String[] args) {
		System.out.println(parseBoolean("false"));
	}
}