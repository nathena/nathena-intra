package me.nathena.infra.base;

import java.util.Map;

public interface QueryFilter {

	public Map<String,Object> getNamedParams();

	public String getQueryNamedSql(final String tableName);
	public String getCountNamedSql(final String tableName);
}
