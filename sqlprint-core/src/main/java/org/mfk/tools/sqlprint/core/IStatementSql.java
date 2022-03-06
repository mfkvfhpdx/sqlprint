package org.mfk.tools.sqlprint.core;

import java.util.List;

public interface IStatementSql {
	public String getSql();
	public List<String> getSplicingParameters();  
}
