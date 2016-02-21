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
	/**
	 * 有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	 * 其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	 **/
	default void build(T1 t1, JdbcGeneralRepository jdbc) {}
	/**
	 * 有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	 * 其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	 **/
	default void builds(List<T1> t1, JdbcGeneralRepository jdbc) {}
	/**
	 * 把持久化对象[一般指数据库对象]的属性名称转换为上层对象类型的属性名
	 */
	default String getConvertField(String fieldName) {return null;}
	/**
	 * 根据持久化对象[一般指数据库对象]的属性名获取上层对象对应属性的值
	 */
	default Object getConvertValue(T1 t1, String fieldName) {return null;}
	/**
	 * 获取转换的上层对象类型,一般实现该方法即可
	 **/
	default Class<T1> getConvertClass() {return null;}
	/**
	 * 硬编码方式转换,有时候要转换的对象对应上层数据对象无法匹配上(复杂属性时),就需要硬编码来转换
	 **/
	default T1 convert(T2 po) {return null;}
}
