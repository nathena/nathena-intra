package me.nathena.infra.base;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultQueryFilter implements QueryFilter {

	protected StringBuilder countSql = new StringBuilder("select count(1) from ");
	protected StringBuilder querySql = new StringBuilder("select * from ");
	protected Map<String,Object> params = new HashMap<String, Object>();
	
	@Override
	public final Map<String, Object> getQuqeryParams() {
		return params;
	}

	@Override
	public final String getQuerySql(final String tableName) {
		
		querySql.append(" `").append(tableName).append("` ").append(" where 1 ").append(buildSqlNamed())
			    .append(buildSqlOrderBy()).append(buildSqlOffset());
		
		return querySql.toString();
	}

	@Override
	public final String getCountSql(final String tableName) {
		
		countSql.append(" `").append(tableName).append("` ").append(" where 1 ").append(buildSqlNamed());
		
		return countSql.toString();
	}

	public abstract StringBuilder buildSqlNamed();
	public abstract StringBuilder buildSqlOffset();
	public abstract StringBuilder buildSqlOrderBy();
}
