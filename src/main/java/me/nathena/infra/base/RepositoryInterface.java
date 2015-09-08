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
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public T merge(T t);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter, int pageNo, int rowSize);
	/**
	 * 
	 * <p>Title: load</p> 
	 * <p>Description: 获取</p> 
	 * @param key
	 * @return T
	 */
	public List<T> load(RepositoryFilter filter, int limit);
	/**
	 * 
	 * <p>Title: total</p> 
	 * <p>Description: 总数</p> 
	 * @param key
	 * @return T
	 */
	public int count(RepositoryFilter filter);
}
