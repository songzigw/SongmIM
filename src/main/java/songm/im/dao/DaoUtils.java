package songm.im.dao;

/**
 * 
 * @author zhangsong
 *
 */
public class DaoUtils {
	public static enum Order {
		/** 升序排列 */
		ASC,
		/** 降序排列 */
		DESC
	}

	/**
	 * 1升序 2降序
	 * 
	 * @param type
	 * @return
	 */
	public static Order orderType(int type) {
		switch (type) {
		case 1:
			return Order.ASC;
		case 2:
			return Order.DESC;
		default:
			return Order.ASC;
		}
	}

	public static String insertSql(Tables table, String fields,
			String paramMarks) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(table).append("(");
		sql.append(fields).append(")");
		sql.append(" values(").append(paramMarks).append(")");
		return sql.toString();
	}
}
