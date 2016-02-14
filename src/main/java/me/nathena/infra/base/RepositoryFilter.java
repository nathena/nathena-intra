/**
 * 
 */
package me.nathena.infra.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nathena.infra.utils.CollectionUtil;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.RandomHelper;
import me.nathena.infra.utils.StringUtil;


/**
 * 2015-09-08
 * reponsitory层的查询接口类,
 * 理论需要支持多种数据存储方式的查询
 * 只适用于简单查询,因为简单查询多数是等值查询,模糊匹配等具有相似性可抽象
 * 复杂的数据查询应根据使用的数据持久化技术写查询代码,因为复杂查询一般只适用于某些具体业务不具备复用性
 * @author GaoWx
 *
 */
public class RepositoryFilter {
	public static enum queryMethod {
		EQ("="),NEQ("!="),EGT(">="),GT(">"),ELT("<="),LT("<"),IN("IN"),BETWEEN_AND("BT"),LIKE("LIKE");
		
		private String value;
		private queryMethod(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public static enum queryConnect {
		AND, OR
	}
	
	public static enum orderby {
		ASC, DESC
	}
	
	protected List<repositoryQuery> querys = new ArrayList<repositoryQuery>();
	protected List<repositoryOrder> orders = new ArrayList<repositoryOrder>();
	
	private boolean defaultQuery = true;
	
	public RepositoryFilter() {}
	public void clearQuery() {
		querys.clear();
	}
	public void clearOrder() {
		orders.clear();
	}
	public void clear() {
		querys.clear();
		orders.clear();
	}
	
	public boolean isDefaultQuery() {
		return defaultQuery;
	}
	public void setDefaultQuery(boolean userDefault) {
		this.defaultQuery = userDefault;
	}
	
	public void addQuery(String field, Object value) {
		if(value == null) {
			return;
		}
		querys.add(new repositoryQuery(field, value, queryMethod.EQ, queryConnect.AND));
	}
	
	public void addQuery(String field, Object value, queryMethod method) {
		if(value == null) {
			return;
		}
		querys.add(new repositoryQuery(field, value, method, queryConnect.AND));
	}
	
	public void addQuery(String field, Object value, queryConnect connect) {
		if(value == null) {
			return;
		}
		querys.add(new repositoryQuery(field, value, queryMethod.EQ, connect));
	}
	
	public void addQuery(String field, Object value, queryMethod method, queryConnect connect) {
		if(value == null) {
			return;
		}
		querys.add(new repositoryQuery(field, value, method, connect));
	}
	
	public void addGroupQuery(String field, Object value, queryMethod method, queryConnect connect) {
		if(value == null) {
			return;
		}
		
		repositoryQuery lastQuery = querys.get(querys.size() - 1);
		if(lastQuery.isGroupQuery()) {
			lastQuery.addGroupQuery(field, value, method, connect);
		} else {
			lastQuery = new repositoryQuery(connect);
			lastQuery.addGroupQuery(field, value, method, null);
			querys.add(lastQuery);
		}
	}
	
	public void addOrder(String field, orderby orderby) {
		orders.add(new repositoryOrder(field, orderby));
	}
	
	public List<repositoryQuery> getQuerys() {
		return querys;
	}

	public List<repositoryOrder> getOrders() {
		return orders;
	}
	
	public void defaultQuery() {}
	public void defaultOrder() {}
}

class repositoryQuery {
	private String field;
	private Object value;
	private RepositoryFilter.queryMethod queryMethod;
	private RepositoryFilter.queryConnect queryConnect;
	private List<repositoryQuery> groupQuerys;
	
	public repositoryQuery(String field, Object value, 
			RepositoryFilter.queryMethod method, RepositoryFilter.queryConnect connect) {
		this.field = field;
		this.value = value;
		this.queryMethod = method;
		this.queryConnect = connect;
	}
	
	public repositoryQuery(RepositoryFilter.queryConnect connect) {
		this.queryConnect = connect;
		groupQuerys = new ArrayList<repositoryQuery>();
	}
	
	public void addGroupQuery(String field, Object value, 
			RepositoryFilter.queryMethod method, RepositoryFilter.queryConnect connect) {
		groupQuerys.add(new repositoryQuery(field, value, method, connect));
	}
	
	public boolean isGroupQuery() {
		return groupQuerys != null;
	}
	
	public String toSqlQuery(Map<String, Object> nameParamMap, Map<String, String> columnMap) {
		if(!isGroupQuery() && ( CollectionUtil.isEmpty(columnMap) || 
				!columnMap.containsKey(field) || StringUtil.isEmpty(columnMap.get(field)))) {
			LogHelper.warn("RepositoryFilter出错");
			LogHelper.warn("columnMap:" + columnMap);
			LogHelper.warn("field名字写错:" + field);
			return "";
		}
		
		StringBuilder resultStr = new StringBuilder(queryConnect == null ? "" : " " + queryConnect.name());
		
		if(!CollectionUtil.isEmpty(groupQuerys)) {
			resultStr.append(" (");
			for(repositoryQuery q : groupQuerys) {
				resultStr.append(q.toSqlQuery(nameParamMap, columnMap));
			}
			resultStr.append(")");
		} else {
			String column = columnMap.get(field);
			
			String r = field + RandomHelper.nextString(3);
			switch(queryMethod) {
			case IN:
				resultStr.append(" `").append(column).append("` IN").append(" (:").append(r).append(")");
				nameParamMap.put(r, value);
				break;
//			case BETWEEN_AND:
//				paramMap.put(r + "1", value);
//				paramMap.put(r + "2", value2);
//				return new StringBuffer(" ").append(connect).append(" `").append(column).append("` BETWEEN :").append(column).append(r).append("1").append(" AND :").append(column).append(r).append("2").toString();
			default:
				resultStr.append(" `").append(column).append("` ").append(queryMethod.getValue()).append(" :").append(r);
				nameParamMap.put(r, value);
			}
		}
		
		return resultStr.toString();
	}
}

class repositoryOrder {
	private String field;
	private RepositoryFilter.orderby queryOrder;
	
	public repositoryOrder(String field, RepositoryFilter.orderby order) {
		this.field = field;
		this.queryOrder = order;
	}
	
	public String toSqlOrder(Map<String, String> columnMap) {
		String column = columnMap.get(field);
		if(StringUtil.isEmpty(column)) {
			LogHelper.error("\n == 属性名属性错误" + field);
			return null;
		}
		return new StringBuffer("`").append(columnMap.get(field)).append("` ").append(queryOrder.name()).toString();
	}
}
