package org.mfk.tools.sqlprint.core;

import java.lang.reflect.Type;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractStatementSql implements IStatementSql {
	protected Statement statement;
	protected Object otherparams;
	protected SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected String sql = "";
	protected List<String> splicingParameters = new ArrayList<String>();

	public AbstractStatementSql(Statement statement, Object otherparams) {
		this.statement = statement;
		this.otherparams = otherparams;
	}

	protected String converNumberToSqlString(Object obj) {
		return String.valueOf(obj);
	}

	protected String converStringToSqlString(String obj) {
		return "'" + String.valueOf(obj) + "'";
	}

	protected String converDateToSqlString(Date date) {
		return "'" + defaultDateFormat.format(date) + "'";
	}

	protected String converOtherTypeToSqlString(Object obj) {
		return "'" + String.valueOf(obj) + "'";
	}

	protected String converObjectSplicingString(Object obj) {
		String val = null;
		if (obj == null) {
			return "null";
		}
		Type type = obj.getClass();

		if (obj instanceof String) {
			val = "'" + String.valueOf(obj).replaceAll("'", "''") + "'";
		} else if (type == Long.class || type == long.class || type == Integer.class || type == int.class
				|| type == Double.class || type == double.class || type == Short.class || type == short.class
				|| type == Float.class || type == float.class) {
			val = converNumberToSqlString(obj);

		} else if (obj instanceof Date) {
			val = converDateToSqlString((Date) obj);
		} else {
			val = converOtherTypeToSqlString(obj);
		}
		return val;
	}

	@Override
	public String getSql() {

		return sql;
	}

	@Override
	public List<String> getSplicingParameters() {

		return splicingParameters;
	}
}
