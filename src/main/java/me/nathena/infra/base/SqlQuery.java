/**
 * 
 */
package me.nathena.infra.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nathena.infra.utils.CollectionUtil;
import me.nathena.infra.utils.RandomHelper;

import org.springframework.util.StringUtils;

/**
 * @author GaoWx
 *
 */
public class SqlQuery {
	private String column; 
	private String opr;
	private Object value;
	private Object value2;
	private String connect = "AND";
	public SqlQuery() {}
	public SqlQuery(String connect, String column, String opr, Object values) {
		this.column = column;
		this.opr = opr;
		this.connect = connect;
		this.value = values;
	}
	
	public SqlQuery(String column, String opr, Object values) {
		this.column = column;
		this.opr = opr;
		this.value = values;
	}
	//双目运算符
	public SqlQuery(String column, String opr, Object values, Object values2) {
		this.column = column;
		this.opr = opr;
		this.value = values;
		this.value2 = values2;
	}
	public SqlQuery(String connect, String column, String opr, Object values, Object values2) {
		this.column = column;
		this.opr = opr;
		this.connect = connect;
		this.value = values;
		this.value2 = values2;
	}
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getOpr() {
		return opr;
	}
	public void setOpr(String opr) {
		this.opr = opr;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getConnect() {
		return connect;
	}
	public void setConnect(String connect) {
		this.connect = connect;
	}
	public Object getValue2() {
		return value2;
	}
	public void setValue2(Object value2) {
		this.value2 = value2;
	}
	public static final String eq = "=", gt = ">", gte = ">=", lt = "<", lte = "<=",
							   in = "IN", between_and = "bt", like = "LIKE", like_left = "LIKE_LEFT", like_right = "LIKE_RIGHT";
	public String toSearchSql(Map<String, Object> paramMap) {
		if(StringUtils.isEmpty(opr)) {
			return "";
		}
		
		String r = RandomHelper.nextString(3);
		switch(opr) {
		case in:
			paramMap.put(column + r, value);
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" ").append(opr).append(" (:").append(column).append(r).append(")").toString();
		case between_and:
			paramMap.put(column + r + "1", value);
			paramMap.put(column + r + "2", value2);
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" BETWEEN :").append(column).append(r).append("1").append(" AND :").append(column).append(r).append("2").toString();
		case like:
			paramMap.put(column + r, "%" + value + "%");
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" LIKE :").append(column).append(r).toString();
		case like_left:
			paramMap.put(column + r, "%" + value);
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" LIKE :").append(column).append(r).toString();
		case like_right:
			paramMap.put(column + r, value + "%");
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" LIKE :").append(column).append(r).toString();
		default:
			paramMap.put(column + r, value);
			return new StringBuffer(" ").append(connect).append(" ").append(column).append(" ").append(opr).append(" :").append(column).append(r).toString();
		}
	}
	
	public static String toDynamicSql(String table, List<SqlQuery> searchs, Map<String, Object> params) {
		StringBuffer sql = new StringBuffer("SELECT * FROM `").append(table).append("` WHERE 1 ");
		
		if(!CollectionUtil.isEmpty(searchs)) {
			for(SqlQuery s : searchs) {
				sql.append(s.toSearchSql(params));
			}
		}
		
		return sql.toString();
	}
	
	public static String toCountSql(String table, List<SqlQuery> searchs, Map<String, Object> params) {
		StringBuffer sql = new StringBuffer("SELECT COUNT(1) FROM `").append(table).append("` WHERE 1 ");
		
		if(!CollectionUtil.isEmpty(searchs)) {
			for(SqlQuery s : searchs) {
				sql.append(s.toSearchSql(params));
			}
		}
		
		return sql.toString();
	}
	
	public static void main(String[] args) {
		List<SqlQuery> l = new ArrayList<SqlQuery>();
		l.add(new SqlQuery("col1", eq, "1"));
		l.add(new SqlQuery("col2", gt, "2"));
		System.out.println(toDynamicSql("table1", l, new HashMap<String, Object>()));
	}
}
