/**
 * 
 */
package me.nathena.infra.base;

import java.util.ArrayList;
import java.util.List;


/**
 * 2015-09-08
 * reponsitory层的抽象查询接口类,
 * 理论需要支持多种数据存储方式的查询,目前只支持简单sql的查询
 * @author GaoWx
 *
 */
public abstract class RepositoryFilter {
	//TODO 延迟初始化?
	protected List<SqlQuery> sqlQueries = new ArrayList<SqlQuery>();
	
	public List<SqlQuery> toSqlQuerys() {
		return sqlQueries;
	}
	
	public String toOrders() {return "";}
}
