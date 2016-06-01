/**
 * 
 */
package me.nathena.infra.base;

import java.util.HashMap;
import java.util.Map;

import me.nathena.infra.utils.StringUtil;

/**
 * @author GaoWx
 *
 */
public class SqlQueryAdaptor extends RepositoryFilter {
	private StringBuilder querySql;
	private StringBuilder orderSql;
	private Map<String, Object> namedParams;
	
	public void addSqlQuery(String queryStr, Object... values) {
		if(StringUtil.isEmpty(queryStr))
			return;
		
		if(querySql == null) {
			querySql = new StringBuilder();
			namedParams = new HashMap<>();
		}
		
		int placeholderCount = getPlaceholderCount(queryStr);
		
		if(placeholderCount != values.length)
			throw new RuntimeException("命名参数和占位符个数不一样");
		
		int existPlaceholderCount = namedParams.size();
		for(int i = 0 ; i < placeholderCount; i++) {
			queryStr = queryStr.replaceFirst("\\?", ":placeHolder_" + (existPlaceholderCount + i));
			namedParams.put("placeHolder_" + (existPlaceholderCount + i), values[i]);
		}
		
		if(querySql.length() <= 0)
			querySql.append("(").append(queryStr).append(")");
		else
			querySql.append(" AND (").append(queryStr).append(")");
	}

	public void setSqlOrder(String orderStr) {
		if(StringUtil.isEmpty(orderStr))
			return;
		
		if(orderSql == null)
			orderSql = new StringBuilder();
		else
			throw new RuntimeException("已经设置了order语句了");
		
		orderSql.append(" ORDER BY ").append(orderStr);
	}
	
	public String getQuerySql() {
		return querySql != null ? querySql.toString() : "";
	}
	
	public String getOrderSql() {
		return orderSql != null ? orderSql.toString() : "";
	}

	public Map<String, Object> getNamedParams() {
		return namedParams;
	}
	
	private int getPlaceholderCount(String str) {
		int start = str.indexOf("?");
		int last = str.lastIndexOf("?");
		
		if(start >= last)
			return 1;
		
		int count = 2;
		for(int i = start + 1; i < last; i++) {
			if(str.charAt(i) == '?')
				count++;
		}
		
		return count;
	}
	
	public static void main(String[] args) {
		SqlQueryAdaptor SqlQuery = new SqlQueryAdaptor();
		SqlQuery.addSqlQuery("dsdsd = ? and sas IN (?)", "dsds", "DSDS");
		SqlQuery.addSqlQuery("dsdsd = ? and sas IN (?) or sasa = ?", "dsds", "DSDS", "DSDS");
		
		System.out.println(SqlQuery.getQuerySql().toString());
	}
}