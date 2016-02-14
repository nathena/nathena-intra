/**
 * 
 */
package me.nathena.infra.base;

import java.util.List;

/**
 * @author GaoWx
 *
 */
public interface DataConvertor<T1, T2> {
	//将简单的po对象转为复杂的业务对象
	public T1 convertFromPo(T2 t2);
	//有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	//其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	default void build(T1 t1, JdbcGeneralRepository jdbc){};
	//有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作(列表)
	//其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	default void builds(List<T1> t1, JdbcGeneralRepository jdbc){};
	
	//将业务对象转换为po对象
	public T2 convertToPo(T1 t1);
}
