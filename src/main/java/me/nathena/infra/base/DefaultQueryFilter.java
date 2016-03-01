package me.nathena.infra.base;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultQueryFilter implements QueryFilter {

	protected StringBuilder countSql = new StringBuilder("select count(1) from ");
	protected StringBuilder querySql = new StringBuilder("select * from ");
	
	protected StringBuilder namedSql = new StringBuilder();
	protected StringBuilder offsetSql = new StringBuilder();
	protected StringBuilder orderBySql = new StringBuilder();
	
	protected Map<String,Object> namedParams = new HashMap<String, Object>();
	
	@Override
	public final Map<String, Object> getNamedParams() {
		return namedParams;
	}
	
	protected DefaultQueryFilter()
	{
		buildNamedParams();
		buildOffset();
		buildOrderBy();
	}

	@Override
	public final String getQueryNamedSql(final String tableName) {
		
		querySql.append(" `").append(tableName).append("` ").append(" where 1 ")
			.append(namedSql)
			.append(orderBySql)
			.append(offsetSql);
		
		return querySql.toString();
	}

	@Override
	public final String getCountNamedSql(final String tableName) {
		
		countSql.append(" `").append(tableName).append("` ").append(" where 1 ").append(namedSql);
		
		return countSql.toString();
	}

	public void buildNamedParams()
	{
		
	}
	
	public void buildOffset()
	{
		
	}
	
	public void buildOrderBy()
	{
		
	}
}
