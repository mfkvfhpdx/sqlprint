package org.mfk.tools.sqlprint.core.utils;

import org.mfk.tools.sqlprint.core.BasicFormatterImpl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlStringUtils {
    private static final String separator= File.separator;
	public static String replaceQm(String sql, String replaceValue) {

		return replaceQm(sql, replaceValue, "?");
	}

	/**
	 * 把在sql中注释和引号字符串中包起来的问号替换成特定值
	 * @param sql
	 * @param replaceValue
	 * @return
	 */
	public static String replaceQm(String sql, String replaceValue, String brv) {

		String[] fds = sql.split(separator + brv);
		// 1 单引号开始 2 /**/ 这种注释开始
		int zt = 0;
		StringBuffer pjsql = new StringBuffer();
		for (int i = 0; i < fds.length; i++) {
			String fd = fds[i];
			pjsql.append(fd);
			// 最后一个段落不需要判断，直接跳过
			if (i == fds.length - 1) {
				break;
			}
			if (fd != null) {
				int index1 = -1;
				int index2 = -1;
				int qtindex = -1;
				String fd2 = fd;
				boolean th = false;
				while (true) {
					// 初始阶段，先找到单引号或注释开始符
					if (zt == 0) {

						index1 = fd2.indexOf("'");
						index2 = fd2.indexOf("/*");
						if (index1 == -1 && index2 == -1) {
							qtindex = -1;
							break;
						}
						index1 = ((index1 == -1) ? 999 : index1);
						index2 = ((index2 == -1) ? 999 : index2);
						if (index1 < index2) {
							zt = 1;
							qtindex = index1;
						} else {
							qtindex = index2;
							zt = 2;
						}
					} else {
						// 找到结束符号
						qtindex = fd2.indexOf(zt == 1 ? "'" : "*/");
						if (qtindex == -1) {
							break;
						}
						zt = 0;
					}
					fd2 = fd2.substring(qtindex + 1);

				}
			}

			if (zt == 0) {
				pjsql.append(brv);
			} else {
				// 没有找到结束符代表?在注释或单引号中间，替换成特定值
				pjsql.append(replaceValue);
			}

		}
		return pjsql.toString();
	}
	public static String formateSql(String sqlStr){

		return new BasicFormatterImpl().format(sqlStr);
	}
	public static String setSqlParameters(String sql, List<String> parameters) {
		SimpleDateFormat sp1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sp2 = new SimpleDateFormat("yyyy-MM-dd");
		String replaceQmValue = "#replace_wenhao#";
		boolean hasdyWh = false;
		if (sql != null && parameters != null && parameters.size() > 0) {
			int c = sql.split("\\?").length;
			// 问号大于参数个数，需检查
			if (c - 1 > parameters.size()) {
				hasdyWh = true;
			}

		}
		if (hasdyWh) {
			sql = replaceQm(sql, replaceQmValue);
		}

		int i = 0;
		while (i < parameters.size()) {
			String val = parameters.get(i);
			Object object = parameters.get(i);
			try {
				if (val == null){
					val = "null";
				}
				sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(val));
			} catch (Exception e) {
				e.printStackTrace();
			}


			++i;
		}
		// 把替换的字符串恢复成问号
		if (hasdyWh) {
			sql = sql.replaceAll(replaceQmValue, "?");
		}
		if (sql != null && parameters != null && parameters.size() > 0 && sql.indexOf(":1")>0){
			boolean check=true;
			//如果存在 : 占位符并且和能对应参数
			for (int j= parameters.size(); j >= 1; j--) {
				if (sql.indexOf(":"+j)==-1){
					check=false;
					break;
				}
			}
			if (check){
				for (int j= parameters.size(); j >= 1; j--) {
					String oj=parameters.get(j-1);
					if (oj == null){
						oj = "null";
					}
					sql=sql.replaceAll(Pattern.quote(":"+j),Matcher.quoteReplacement(oj));
				}
			}
		}
		return sql;
	}
}
