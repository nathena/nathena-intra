package me.nathena.infra.base;

import java.util.Map;

public interface QueryFilter {

	public Map<String,Object> getQuqeryParams();

	public String getQuerySql();
	public String getCountSql();
}
