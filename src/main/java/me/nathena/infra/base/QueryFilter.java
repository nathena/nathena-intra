package me.nathena.infra.base;

import java.util.Map;

public interface QueryFilter {

	public Map<String,Object> getQuqeryParams();

	public String getQuerySql(final String tableName);
	public String getCountSql(final String tableName);
}
