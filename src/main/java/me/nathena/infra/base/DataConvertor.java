/**
 * 
 */
package me.nathena.infra.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GaoWx
 *
 */
public abstract class DataConvertor<T1, T2> {
//	public DualHashBidiMap fieldMap = new DualHashBidiMap();
	public Map<String, String> fieldMap = new HashMap<String, String>();
	private Class<T1> t1Class;
	
	@SuppressWarnings("unchecked")
	public DataConvertor() {
		Type[] types = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments();
		
		t1Class = (Class<T1>)types[0];
	}
	/**
	 * 注册字段映射关系
	 * @param t1Field
	 * @param t2Fild
	 */
	protected void registerFieldMap(String t1Field, String t2Fild) {
		fieldMap.put(t2Fild, t1Field);
	}
	/**
	 * 有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	 * 其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	 **/
	public void build(T1 t1, JdbcGeneralRepository jdbc) {}
	/**
	 * 有时业务对象是由主表关联从表组装起来的,这里提供接口执行获取从表组装的操作
	 * 其实如果是多表联查可以一次查询就得到业务所需数据,但单表就需要组装
	 **/
	public void builds(List<T1> t1, JdbcGeneralRepository jdbc) {}
	/**
	 * 把持久化对象[一般指数据库对象]的属性名称转换为上层对象类型的属性名
	 */
	public String getConvertField(String fieldName) {
		return fieldMap.get(fieldName);
	}
	/**
	 * 根据持久化对象[一般指数据库对象]的属性名获取上层对象对应属性的值
	 */
	public Object getConvertValue(T1 t1, String fieldName) {return null;}
	/**
	 * 获取转换的上层对象类型
	 **/
	public Class<T1> getConvertClass() {
		return t1Class;
	}
	/**
	 * 硬编码方式转换,有时候要转换的对象对应上层数据对象无法匹配上(复杂属性时),就需要硬编码来转换
	 **/
	public T1 convert(T2 po) {return null;}
}
