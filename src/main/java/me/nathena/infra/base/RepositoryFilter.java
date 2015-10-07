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
 * 理论需要支持多种数据存储方式的查询,目前只支持简单sql的查询
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
	
	private boolean userDefault = true;
	
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
	
	public boolean isUserDefault() {
		return userDefault;
	}
	public void setUserDefault(boolean userDefault) {
		this.userDefault = userDefault;
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
	
	public repositoryQuery(String field, Object value, 
			RepositoryFilter.queryMethod method, RepositoryFilter.queryConnect connect) {
		this.field = field;
		this.value = value;
		this.queryMethod = method;
		this.queryConnect = connect;
	}
	
	public String toSqlQuery(Map<String, Object> nameParamMap, Map<String, String> columnMap) {
		if(CollectionUtil.isEmpty(columnMap) || !columnMap.containsKey(field)) {
			LogHelper.warn("columnMap:" + columnMap);
			LogHelper.warn("field名字写错:" + field);
			return "";
		}
		
		String column = columnMap.get(field);
		if(StringUtil.isEmpty(column)) {
			LogHelper.error("\n == 属性名属性错误" + field);
			return null;
		}
		
		String r = field + RandomHelper.nextString(3);
		switch(queryMethod) {
		case IN:
			nameParamMap.put(r, value);
			return new StringBuffer(" ").append(queryConnect.name())
					.append(" `").append(column).append("` IN").append(" (:").append(r).append(")").toString();
//		case BETWEEN_AND:
//			paramMap.put(r + "1", value);
//			paramMap.put(r + "2", value2);
//			return new StringBuffer(" ").append(connect).append(" `").append(column).append("` BETWEEN :").append(column).append(r).append("1").append(" AND :").append(column).append(r).append("2").toString();
		default:
			nameParamMap.put(r, value);
			return new StringBuffer(" ").append(queryConnect.name())
					.append(" `").append(column).append("` ")
					.append(queryMethod.getValue()).append(" :").append(r)
					.toString();
		}
	}

	public String getField() {
		return field;
	}

	public Object getValue() {
		return value;
	}

	public RepositoryFilter.queryMethod getQueryMethod() {
		return queryMethod;
	}

	public RepositoryFilter.queryConnect getQueryConnect() {
		return queryConnect;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setQueryMethod(RepositoryFilter.queryMethod queryMethod) {
		this.queryMethod = queryMethod;
	}

	public void setQueryConnect(RepositoryFilter.queryConnect queryConnect) {
		this.queryConnect = queryConnect;
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
	
	public RepositoryFilter.orderby getQueryOrder() {
		return queryOrder;
	}
	public String getField() {
		return field;
	}
	
	public RepositoryFilter.orderby getSqlOrder() {
		return queryOrder;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public void setQueryOrder(RepositoryFilter.orderby queryOrder) {
		this.queryOrder = queryOrder;
	}
}
