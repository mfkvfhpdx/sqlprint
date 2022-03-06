package org.mfk.tools.sqlprint.core.print;

import java.sql.Statement;

public interface ISqlPrinter {
	public void printSql(Statement statement, Object otherParams) throws Exception;
}
