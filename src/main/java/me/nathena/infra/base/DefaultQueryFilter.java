package me.nathena.infra.base;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultQueryFilter implements QueryFilter {

	protected StringBuilder countSql = new StringBuilder("select count(1) from ");
	protected StringBuilder querySql = new StringBuilder("select * from ");
	
	protected Map<String,Object> namedParams = new HashMap<String, Object>();
	
	@Override
	public final Map<String, Object> getNamedParams() {
		return namedParams;
	}
	
	@Override
	public final String getQueryNamedSql(final String tableName) {
		
		querySql.append(" `").append(tableName).append("` ").append(" where 1 ")
			.append(buildNamedSql())
			.append(buildOrderBySql())
			.append(buildOffsetSql());
		
		return querySql.toString();
	}

	@Override
	public final String getCountNamedSql(final String tableName) {
		
		countSql.append(" `").append(tableName).append("` ").append(" where 1 ")
			.append(buildNamedSql());
		
		return countSql.toString();
	}

	public StringBuilder buildNamedSql()
	{
		return new StringBuilder();
	}
	
	public StringBuilder buildOffsetSql()
	{
		return new StringBuilder();
	}
	
	public StringBuilder buildOrderBySql()
	{
		return new StringBuilder();
	}
}
