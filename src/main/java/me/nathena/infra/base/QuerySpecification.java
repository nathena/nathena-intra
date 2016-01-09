package me.nathena.infra.base;

import java.util.Map;

/**
 * 通用过滤器
 * @Title: QuerySpecification.java
 * @Package me.nathena.infra.base
 * @author nathena  
 * @date 2016年1月9日 下午1:19:59
 * @version V1.0 
 * @UpdateHis:
 *      TODO
 */
public interface QuerySpecification {

	public Map<String,Map<String,Object>> getQuerySpecification();
}
