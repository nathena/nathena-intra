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
	//有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	//其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	default void build(T1 t1, JdbcGeneralRepository jdbc) {};
	//有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作(列表)
	//其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	default void builds(List<T1> t1, JdbcGeneralRepository jdbc) {};
	//把数据库对象和实际转换对象的属性进行转换
	default String fieldConvert(String poField) {return null;};
	
	default Class<T1> getConvertClass() {return null;};
	
	//硬编码方式转换,有时候要转换的对象其实数据对象无法匹配上(复杂属性时),就需要硬编码来转换
	default T1 convert(T2 po) {return null;};
}
