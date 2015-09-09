package me.nathena.infra.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;

import me.nathena.infra.utils.CollectionUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;

@Cacheable(value="jytnn.cache")
public abstract class BaseRepository<T> implements RepositoryInterface<T> {

	@Resource
	protected JdbcGeneralRepository jdbc;
	
	protected Class<T> entityClass;
	
	protected String tableName;
	
	protected Set<Field> ids;
	protected Set<Field> fields;
	
	@SuppressWarnings("unchecked")
	public BaseRepository()
	{
		this.entityClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.tableName = EntitySpecification.getName(entityClass);
		
		Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(entityClass);
		
		this.ids = accessor.get(Id.class);
		this.fields = accessor.get(Column.class);
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: create</p> 
	 * <p>Description: 创建</p> 
	 * @param t
	 * @return 
	 * @see com.e344.springext.repository.RepositoryInterface#create(java.lang.Object)
	 */
	public T create(T t) 
	{
		try
		{
			boolean autoKey = true;
			
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" insert into `");
			sb.append(tableName);
			sb.append("` ( ");
			String sp="";
			
			StringBuilder values = new StringBuilder();
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = fields.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				if(!isTransientValue(field,val))
				{
					sb.append(sp).append("`").append(name).append("`");
					//sb.append(sp).append(name);
					values.append(sp).append(":"+name);
					paramMap.put(name, val);
					
					sp=" , ";
					
					if( ids.contains(field)){
						autoKey = false;
					}
				}
			}
			sb.append(") values ( ").append(values).append(" ) ");
			
			if( jdbc.commandUpdate(sb.toString(),paramMap)>0 && ids.size() == 1 && autoKey ) 
			{
				field = ids.iterator().next();
				method = EntitySpecification.getWriteMethod(field);
				
				method.invoke(t, jdbc.getAutoIncrementId());
			}
			
			return t;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_CREATE,e);
		}
		
		
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: update</p> 
	 * <p>Description: 更新</p> 
	 * @param t
	 * @return 
	 * @see com.e344.springext.repository.RepositoryInterface#update(java.lang.Object)
	 */
	public T update(T t) 
	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = fields.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				if( ids.contains(field))
				{
					continue;
				}
				
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				if(!isTransientValue(field,val))
				{
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					paramMap.put(name, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			fieldIter = ids.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(name).append("` = :"+name);
				//sb.append(sp).append(name).append(" = :"+name);
				sp=" and ";
				
				paramMap.put(name, val);
			}
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}
	/**
	 * 
	 * <p>Title: update</p> 
	 * <p>Description: </p> 
	 * @param t
	 * @return T
	 */
	public T update(T t, String... forceUpdateFileds)	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			List<String> forceFileds = Arrays.asList(forceUpdateFileds);
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = fields.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				if( ids.contains(field))
				{
					continue;
				}
				
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				if(forceFileds.contains(field.getName()) || !isTransientValue(field, val))
				{
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					paramMap.put(name, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			fieldIter = ids.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(name).append("` = :"+name);
				//sb.append(sp).append(name).append(" = :"+name);
				sp=" and ";
				
				paramMap.put(name, val);
			}
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}
	
	protected T update(T t,String where)
	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = fields.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				if( ids.contains(field))
				{
					continue;
				}
				
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				if(!isTransientValue(field,val))
				{
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					paramMap.put(name, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			sb.append(sp).append(where);
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}
	
	/**
	 * 
	 * <p>Title: replace</p> 
	 * <p>Description: 替换数据</p> 
	 * @param t
	 * @return T
	 */
	public T merge(T t)
	{
		try
		{
			if(exist(t))
			{
				t = update(t);
			}
			else
			{
				t = create(t);
			}
			
			return t;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: remove</p> 
	 * <p>Description:删除 </p> 
	 * @param t 
	 * @see com.e344.springext.repository.RepositoryInterface#remove(java.lang.Object)
	 */
	public void remove(T t) 
	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" delete from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = ids.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(name).append("` = :"+name);
				//sb.append(sp).append(name).append(" = :"+name);
				sp=" and ";
				
				paramMap.put(name, val);
			}
			jdbc.commandUpdate(sb.toString(), paramMap);
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_DELETE,e);
		}
	}

	public void delete(Object key) 
	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" delete from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			if( ids.size()>1 && EntitySpecification.isEmbeddableAccessor(key))
			{
				Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(key.getClass());
				Set<Field> keyField = accessor.get(Column.class);
				
				Field field = null;
				String name = null;
				Method method = null;
				Object val = null;
				Iterator<Field> fieldIter = keyField.iterator();
				while(fieldIter.hasNext())
				{
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					method = EntitySpecification.getReadMethod(field);
					val = method.invoke(key);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					sp=" and ";
					
					paramMap.put(name, val);
				}
			}
			else
			{
				Field field = null;
				String name = null;
				Iterator<Field> fieldIter = ids.iterator();
				while(fieldIter.hasNext())
				{
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					sp=" and ";
					
					paramMap.put(name, key);
				}
			}
			
			jdbc.commandUpdate(sb.toString(), paramMap);
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_DELETE,e);
		}
		
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: get</p> 
	 * <p>Description:获取 </p> 
	 * @param key
	 * @return 
	 * @see com.e344.springext.repository.RepositoryInterface#get(java.lang.Object)
	 */
	public T get( Object key ) {
		return get(key,new String[]{});
	}
	
	protected T get( Object key, String... columnLabelNames ) {
		
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" select * ");
			if( null != columnLabelNames)
			{
				for(String columnLabelName : columnLabelNames)
				{
					if(!StringUtils.isEmpty(columnLabelName))
					{
						sb.append(","+columnLabelName);
					}
				}
			}
			sb.append(" from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			
			String sp=" and ";
			
			if( ids.size()>1 && EntitySpecification.isEmbeddableAccessor(key))
			{
				Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(key.getClass());
				Set<Field> keyField = accessor.get(Column.class);
				
				Field field = null;
				String name = null;
				Method method = null;
				Object val = null;
				Iterator<Field> fieldIter = keyField.iterator();
				while(fieldIter.hasNext())
				{
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					method = EntitySpecification.getReadMethod(field);
					val = method.invoke(key);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					sp=" and ";
					
					paramMap.put(name, val);
				}
			}
			else
			{
				Field field = null;
				String name = null;
				Iterator<Field> fieldIter = ids.iterator();
				while(fieldIter.hasNext())
				{
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					//sb.append(sp).append(name).append(" = :"+name);
					sp=" and ";
					
					paramMap.put(name, key);
				}
			}
			
			return jdbc.getEntity(entityClass, sb.toString(), paramMap);
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	/**
	 * 
	 * <p>Title: exist</p> 
	 * <p>Description: 验证对象是否存在</p> 
	 * @param t
	 * @return boolean
	 */
	public boolean exist(T t)
	{
		try
		{
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" select count(1) from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			Field field = null;
			String name = null;
			Method method = null;
			Object val = null;
			Iterator<Field> fieldIter = ids.iterator();
			while(fieldIter.hasNext())
			{
				field = fieldIter.next();
				name = EntitySpecification.getName(field);
				method = EntitySpecification.getReadMethod(field);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(name).append("` = :"+name);
				//sb.append(sp).append(name).append(" = :"+name);
				sp=" and ";
				
				paramMap.put(name, val);
			}
			
			int count = jdbc.queryForInt(sb.toString(), paramMap);
			
			return count>0;
		}
		catch(Exception e)
		{
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	private static boolean isTransientValue(Field field,Object value)
	{
		return value==null || ( PrimitiveTypeChecked.checkNumberType(field.getType()) && ("0".equals(value.toString()) || "0.0".equals(value.toString())) );
	}

	@Override
	public List<T> load(RepositoryFilter filter) {
		StringBuffer sql = new StringBuffer("SELECT * FROM `").append(tableName).append("` WHERE 1");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(filter != null && !CollectionUtil.isEmpty(filter.toSqlQuerys())) {
			for(SqlQuery query : filter.toSqlQuerys()) {
				sql.append(query.toSearchSql(params));
			}
			
			sql.append(filter.toOrders());
		}
		
		
		
		return jdbc.getList(entityClass, sql.toString(), params);
	}

	@Override
	public List<T> load(RepositoryFilter filter, int pageNo, int rowSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM `").append(tableName).append("` WHERE 1");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(filter != null && !CollectionUtil.isEmpty(filter.toSqlQuerys())) {
			for(SqlQuery query : filter.toSqlQuerys()) {
				sql.append(query.toSearchSql(params));
			}
			
			sql.append(filter.toOrders());
		}

		sql.append(" LIMIT :rowOffset, :rowSize");
		params.put("rowOffset", (pageNo - 1) * rowSize);
		params.put("rowSize", rowSize);
		
		return jdbc.getList(entityClass, sql.toString(), params);
	}

	@Override
	public List<T> load(RepositoryFilter filter, int limit) {
		StringBuffer sql = new StringBuffer("SELECT * FROM `").append(tableName).append("` WHERE 1");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(filter != null && !CollectionUtil.isEmpty(filter.toSqlQuerys())) {
			for(SqlQuery query : filter.toSqlQuerys()) {
				sql.append(query.toSearchSql(params));
			}
			
			sql.append(filter.toOrders());
		}
		
		sql.append(" LIMIT :limit");
		params.put("limit", limit);
		
		return jdbc.getList(entityClass, sql.toString(), params);
	}

	@Override
	public int count(RepositoryFilter filter) {
		StringBuffer sql = new StringBuffer("SELECT count(1) FROM `").append(tableName).append("` WHERE 1");
		
		if(filter == null || !CollectionUtil.isEmpty(filter.toSqlQuerys())) {
			return jdbc.queryForInt(sql.toString());
		}
		
		Map<String, Object> params = new HashMap<String, Object>();

		for(SqlQuery query : filter.toSqlQuerys()) {
			sql.append(query.toSearchSql(params));
		}
		return jdbc.queryForInt(sql.toString(), params);
	}

	@Override
	public T get(RepositoryFilter filter, String... requiredFields) {
		StringBuffer sql = new StringBuffer("SELECT ");
		if(requiredFields == null) {
			sql.append("*");
		} else {
			Iterator<Field> fieldIter = fields.iterator();
			String split = "";
			for(String fieldStr : requiredFields) {
				//TODO 这里的实现导致O(N2)的复杂度 待优化(HashMap?)
				while(fieldIter.hasNext()) {
					Field filed = fieldIter.next();
					if(filed.getName().equals(fieldStr)) {
						String name = EntitySpecification.getName(filed);
						sql.append(split).append("`").append(name).append("` ");
						split = ",";
					}
				}
			}
		}
		
		sql.append(" FROM `").append(tableName).append("` WHERE 1");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(filter != null && !CollectionUtil.isEmpty(filter.toSqlQuerys())) {
			for(SqlQuery query : filter.toSqlQuerys()) {
				sql.append(query.toSearchSql(params));
			}
		}
		
		sql.append(" LIMIT 1");
		return jdbc.getEntity(entityClass, sql.toString(), params);
	}
}
