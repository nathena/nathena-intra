package me.nathena.infra.base;




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
}
