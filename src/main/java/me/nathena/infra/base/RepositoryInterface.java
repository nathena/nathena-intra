package me.nathena.infra.base;

import java.util.List;




public interface RepositoryInterface<T> {

	/**
	 * 
	 * <p>Title: create</p> 
	 * <p>Description: </p> 
	 * @param t
	 * @return T
	 */
	public T create(T t);
	
	/**
	 * 
	 * <p>Title: update</p> 
	 * <p>Description: </p> 
	 * @param t
	 * @return T
	 */
	public T update(T t);
	
	/**
	 * 
	 * <p>Title: update</p> 
	 * <p>Description: </p> 
	 * @param t
	 * @return T
	 */
	public T update(T t, String... ignoreUpdateFileds);
	
	/**
	 * 
	 * <p>Title: remove</p> 
	 * <p>Description: 删除</p> 
	 * @param t void
	 */
	public void remove(T t);
	
	/**
	 * 根据主键删除
	 *
	 * @author nathena 
	 * @date 2013-7-18 上午11:32:37 
	 * @param t void
	 */
	public void delete(Object key);
	/**
	 * 
	 * <p>Title: get</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public T get(Object key);
	/**
	 * 
	 * <p>Title: get</p> 
	 * <p>Description: 插入或更新</p> 
	 * @param key
	 * @return T
	 */
	public T merge(T t);
	/**
	 * 
	 * <p>Title: get</p> 
	 * <p>Description: 根据搜索条件获取,</p> 
	 * @param filter 搜索条件
	 * @return T
	 */
	public T get(RepositoryFilter filter);
	/**
	 * 
	 * <p>Title: get</p> 
	 * <p>Description: 根据搜索条件获取,</p> 
	 * @param filter 搜索条件
	 * @param requiredFields 本次查询需要的属性名称(ps:是对象的属性不是数据库字段),
	 * 						   该入参主要为了优化数据来源是关系型数据库的时的sql,为空则返回完整对象
	 * @return T
	 */
	public T get(RepositoryFilter filter, String... requiredFields);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter, String... requiredFields);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter, int pageNo, int rowSize, String... requiredFields);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter, int limit, String... requiredFields);
	/**
	 * 
	 * <p>Title: total</p> 
	 * <p>Description: 总数</p> 
	 * @param key
	 * @return T
	 */
	public int count(RepositoryFilter filter);
	/**
	 * 
	 * <p>Title: delete</p> 
	 * <p>Description: 按条件删除数据,返回删除数据数</p> 
	 * @param filter
	 * @return int
	 */
	public int delete(RepositoryFilter filter);
	/**
	 * 
	 * <p>Title: delete</p> 
	 * <p>Description: 按条件更新数据,返回更新数据数</p> 
	 * @param t 用户存放数据字段的容器
	 * @param filter 过滤器
	 * @param updateFields 要求更新的字段(ps:要写对象的属性而不是数据库字段),为空更新费id字段
	 * @return int
	 */
	public int update(T t, RepositoryFilter filter, String... updateFields);
}
