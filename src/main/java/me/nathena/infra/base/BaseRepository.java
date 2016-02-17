package me.nathena.infra.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import me.nathena.infra.utils.CollectionUtil;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.StringUtil;

public abstract class BaseRepository<T> implements RepositoryInterface<T> {
	public static enum QueryType {
		SQL,
	}
	//默认数据源为关系型数据库
	protected QueryType queryType = QueryType.SQL;
	
	@Resource
	protected JdbcGeneralRepository jdbc;
	
	protected Class<T> entityClass;
	
	protected String tableName;
	
	protected Map<String, Method> fieldToMethodMap = new HashMap<String, Method>();
	protected Map<String, String> fieldToColumnMap = new HashMap<String, String>();
	protected Set<String> idFields = new HashSet<String>();
	protected Set<String> transientFields = new HashSet<String>();
	
	@SuppressWarnings("unchecked")
	public BaseRepository()
	{
		this.entityClass = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.tableName = EntitySpecification.getName(entityClass);
		
		Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(entityClass);
		
		Set<Field> ids = accessor.get(Id.class);
		Set<Field> fields = accessor.get(Column.class);
		
		if(!CollectionUtil.isEmpty(ids)) {
			for(Field f : ids) {
				idFields.add(f.getName());
				fieldToMethodMap.put(f.getName(), EntitySpecification.getReadMethod(f));
				fieldToColumnMap.put(f.getName(), EntitySpecification.getName(f));
			}
		}
		
		if(!CollectionUtil.isEmpty(fields)) {
			for(Field f : fields) {
				if(PrimitiveTypeChecked.checkNumberType(f.getType())) {
					transientFields.add(f.getName());
				}
				fieldToMethodMap.put(f.getName(), EntitySpecification.getReadMethod(f));
				fieldToColumnMap.put(f.getName(), EntitySpecification.getName(f));
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: create</p> 
	 * <p>Description: 创建</p> 
	 * @param t
	 * @return 
	 * @see com.e344.springext.repository.RepositoryInterface#create(java.lang.Object)
	 */
	public T create(T t) {
		try {
			boolean autoKey = true;
			
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" insert into `");
			sb.append(tableName).append("` ( ");
			String splite="";
			
			StringBuilder values = new StringBuilder();
			
			Object val = null;
			Iterator<String> fieldIter = fieldToColumnMap.keySet().iterator();
			while(fieldIter.hasNext()) {
				String field = fieldIter.next();
				String column = fieldToColumnMap.get(field);
				Method method = fieldToMethodMap.get(field);
				val = method.invoke(t);
				
				if(!isTransientValue(field,val)) {
					sb.append(splite).append("`").append(column).append("`");
					values.append(splite).append(":"+field);
					paramMap.put(field, val);
					
					splite=" , ";
					
					if(idFields.contains(field)){
						autoKey = false;
					}
				}
			}
			sb.append(") values ( ").append(values).append(" ) ");
			
			if(jdbc.commandUpdate(sb.toString(),paramMap)>0 && idFields.size() == 1 && autoKey)  {
				String fieldName = idFields.iterator().next();
				Field field = entityClass.getDeclaredField(fieldName);
				Method method = EntitySpecification.getWriteMethod(field);
				
				method.invoke(t, jdbc.getAutoIncrementId());
			}
			
			return t;
		}
		catch(Exception e) {
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
	public T update(T t) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Object val = null;
			Iterator<String> fieldIter = fieldToColumnMap.keySet().iterator();
			while(fieldIter.hasNext()) {
				String fieldName = fieldIter.next();
				if(idFields.contains(fieldName)) {
					continue;
				}
				
				String column = fieldToColumnMap.get(fieldName);
				Method method = fieldToMethodMap.get(fieldName);
				val = method.invoke(t);
				
				if(!StringUtil.isEmpty(column) && !isTransientValue(fieldName,val)) {
					sb.append(sp).append("`").append(column).append("` = :"+fieldName);
					paramMap.put(fieldName, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			fieldIter = idFields.iterator();
			while(fieldIter.hasNext()) {
				String idField = fieldIter.next();
				String column = fieldToColumnMap.get(idField);
				Method method = fieldToMethodMap.get(idField);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(column).append("` = :"+idField);
				sp=" and ";
				
				paramMap.put(idField, val);
			}
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		} catch(Exception e) {
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
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			List<String> forceFileds = Arrays.asList(forceUpdateFileds);
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Object val = null;
			Iterator<String> fieldIter = fieldToColumnMap.keySet().iterator();
			while(fieldIter.hasNext()) {
				String fieldName = fieldIter.next();
				if(idFields.contains(fieldName)) {
					continue;
				}
				
				String column = fieldToColumnMap.get(fieldName);
				Method method = fieldToMethodMap.get(fieldName);
				val = method.invoke(t);
				
				if(forceFileds.contains(fieldName) || !isTransientValue(fieldName,val)) {
					sb.append(sp).append("`").append(column).append("` = :"+fieldName);
					paramMap.put(fieldName, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			fieldIter = idFields.iterator();
			while(fieldIter.hasNext()) {
				String idField = fieldIter.next();
				String column = fieldToColumnMap.get(idField);
				Method method = fieldToMethodMap.get(idField);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(column).append("` = :"+idField);
				sp=" and ";
				
				paramMap.put(idField, val);
			}
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}
	
	protected T update(T t,String where) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" update `");
			sb.append(tableName);
			sb.append("` set ");
			String sp="";
			
			Object val = null;
			Iterator<String> fieldIter = fieldToColumnMap.keySet().iterator();
			while(fieldIter.hasNext()) {
				String fieldName = fieldIter.next();
				if(idFields.contains(fieldName)) {
					continue;
				}
				
				String column = fieldToColumnMap.get(fieldName);
				Method method = fieldToMethodMap.get(fieldName);
				val = method.invoke(t);
				
				if(!isTransientValue(fieldName,val)) {
					sb.append(sp).append("`").append(column).append("` = :"+fieldName);
					paramMap.put(fieldName, val);
					
					sp=" , ";
				}
			}
			
			sp=" where ";
			sb.append(sp).append(where);
			
			jdbc.commandUpdate(sb.toString(),paramMap);
			
			return t;
		} catch(Exception e) {
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
	public T merge(T t) {
		try {
			if(exist(t)) {
				t = update(t);
			}
			else {
				t = create(t);
			}
			
			return t;
		}
		catch(Exception e) {
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
	public void remove(T t) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" delete from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			Object val = null;
			Iterator<String> fieldIter = idFields.iterator();
			while(fieldIter.hasNext()) {
				String idFieldName = fieldIter.next();
				String column = fieldToColumnMap.get(idFieldName);
				Method method = fieldToMethodMap.get(idFieldName);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(column).append("` = :"+idFieldName);
				sp=" and ";
				
				paramMap.put(idFieldName, val);
			}
			jdbc.commandUpdate(sb.toString(), paramMap);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_DELETE,e);
		}
	}
	/**
	 * TODO 这个方法写的不好
	 */
	public void delete(Object key) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" delete from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			if( idFields.size()>1 && EntitySpecification.isEmbeddableAccessor(key)) {
				Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(key.getClass());
				Set<Field> keyField = accessor.get(Column.class);
				
				Field field = null;
				String name = null;
				Method method = null;
				Object val = null;
				Iterator<Field> fieldIter = keyField.iterator();
				while(fieldIter.hasNext()) {
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					method = EntitySpecification.getReadMethod(field);
					val = method.invoke(key);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					sp=" and ";
					
					paramMap.put(name, val);
				}
			} else {
				String fieldName = idFields.iterator().next();
				String column = fieldToColumnMap.get(fieldName);
				
				sb.append(sp).append("`").append(column).append("` = :"+fieldName);
				paramMap.put(fieldName, key);
			}
			
			jdbc.commandUpdate(sb.toString(), paramMap);
		} catch(Exception e) {
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
	
	/**
	 * TODO 这个方法写的不好
	 */
	protected T get( Object key, String... columnLabelNames ) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" select * ");
			if( null != columnLabelNames) {
				for(String columnLabelName : columnLabelNames) {
					if(!StringUtils.isEmpty(columnLabelName)) {
						sb.append(","+columnLabelName);
					}
				}
			}
			sb.append(" from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			
			String sp=" and ";
			
			if( idFields.size()>1 && EntitySpecification.isEmbeddableAccessor(key)) {
				Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(key.getClass());
				Set<Field> keyField = accessor.get(Column.class);
				
				Field field = null;
				String name = null;
				Method method = null;
				Object val = null;
				Iterator<Field> fieldIter = keyField.iterator();
				while(fieldIter.hasNext()) {
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					method = EntitySpecification.getReadMethod(field);
					val = method.invoke(key);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					sp=" and ";
					
					paramMap.put(name, val);
				}
			} else {
				String fieldName = idFields.iterator().next();
				String column = fieldToColumnMap.get(fieldName);
				
				sb.append(sp).append("`").append(column).append("` = :"+fieldName);
				paramMap.put(fieldName, key);
			}
			
			return jdbc.getEntity(entityClass, sb.toString(), paramMap);
		} catch(Exception e) {
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
	public boolean exist(T t) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" select 1 from `");
			sb.append(tableName);
			sb.append("` where 1 ");
			String sp=" and ";
			
			Object val = null;
			Iterator<String> fieldIter = idFields.iterator();
			while(fieldIter.hasNext()) {
				String fieldName = fieldIter.next();
				String column = fieldToColumnMap.get(fieldName);
				Method method = fieldToMethodMap.get(fieldName);
				val = method.invoke(t);
				
				sb.append(sp).append("`").append(column).append("` = :"+fieldName);
				sp=" and ";
				
				paramMap.put(fieldName, val);
			}
			sb.append(" LIMIT 1");
			
			return jdbc.queryForInt(sb.toString(), paramMap) > 0;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	private boolean isTransientValue(String fieldame, Object value) {
		return value==null || ( transientFields.contains(fieldame) && ("0".equals(value.toString()) || "0.0".equals(value.toString())) );
	}

	private void attachQuery(StringBuilder sql, RepositoryFilter filter, Map<String, Object> params) {
		if(filter != null) {
			if(filter.isDefaultQuery()) {
				filter.defaultQuery();
			}
			
			if(!CollectionUtil.isEmpty(filter.getQuerys())) {
				for(repositoryQuery query : filter.getQuerys()) {
					sql.append(query.toSqlQuery(params, fieldToColumnMap));
				}
			}
		}
	}
	
	private void attachOrder(StringBuilder sql, RepositoryFilter filter, Map<String, Object> params) {
		if(filter != null) {
			filter.defaultOrder();
			if(!CollectionUtil.isEmpty(filter.getOrders())) {
				sql.append(" ORDER BY ");
				String splite = "";
				for(repositoryOrder order : filter.getOrders()) {
					sql.append(splite).append(order.toSqlOrder(fieldToColumnMap));
					splite = ",";
				}
			}
		}
	}

	@Override
	public int count(RepositoryFilter filter) {
		try {
			StringBuilder sql = new StringBuilder("SELECT count(1) FROM `").append(tableName).append("` WHERE 1");
			
			if(filter == null) {
				return jdbc.queryForInt(sql.toString());
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
	
			attachQuery(sql, filter, params);
			
			return jdbc.queryForInt(sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public T get(RepositoryFilter filter, String... requiredFields) {
		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			if(CollectionUtil.isEmpty(requiredFields)) {
				sql.append("*");
			} else {
				String split = "";
				for(String fieldName : requiredFields) {
					String column = fieldToColumnMap.get(fieldName);
					if(StringUtil.isEmpty(column)) {
						LogHelper.error("\n == 属性名属性错误" + fieldName);
						continue;
					}
					
					sql.append(split).append("`").append(column).append("` ");
					split = ",";
				}
			}
			
			sql.append(" FROM `").append(tableName).append("` WHERE 1");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT 1");
			return jdbc.getEntity(entityClass, sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	@Override
	public T get(RepositoryFilter filter) {
		try {
			StringBuilder sql = new StringBuilder("SELECT * FROM `");
			
			sql.append(tableName).append("` WHERE 1");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT 1");
			return jdbc.getEntity(entityClass, sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	@Override
	public int delete(RepositoryFilter filter) {
		try {
			StringBuilder sql = new StringBuilder("DELETE FROM `").append(tableName).append("` WHERE 1 ");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			
			return jdbc.commandUpdate(sql.toString(),params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_DELETE,e);
		}
	}
	
	@Override
	public int update(T t, RepositoryFilter filter, String... updateFields) {
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = new StringBuilder(" UPDATE `");
			sb.append(tableName);
			sb.append("` SET ");
			String sp="";
			
			Object val = null;
			Iterator<String> fieldIter = null;
			if(CollectionUtil.isEmpty(updateFields)) {
				fieldIter = fieldToColumnMap.keySet().iterator();
			} else {
				fieldIter = Arrays.asList(updateFields).iterator();
			}
			
			while(fieldIter.hasNext()) {
				String fieldName = fieldIter.next();
				if(idFields.contains(fieldName)) {
					continue;
				}
				
				String column = fieldToColumnMap.get(fieldName);
				Method method = fieldToMethodMap.get(fieldName);

				if(!StringUtil.isEmpty(column)) {
					val = method.invoke(t);
					sb.append(sp).append("`").append(column).append("` = :"+fieldName);
					paramMap.put(fieldName, val);
					
					sp=" , ";
				} else {
					LogHelper.error("\n == 属性名属性错误" + fieldName);
				}
			}
			
			sb.append(" WHERE 1 ");
			attachQuery(sb, filter, paramMap);
			
			return jdbc.commandUpdate(sb.toString(),paramMap);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_UPDATE,e);
		}
	}

	@Override
	public List<T> load(RepositoryFilter filter, String... requiredFields) {
		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			if(CollectionUtil.isEmpty(requiredFields)) {
				sql.append("*");
			} else {
				String split = "";
				for(String fieldName : requiredFields) {
					String column = fieldToColumnMap.get(fieldName);
					if(StringUtil.isEmpty(column)) {
						LogHelper.error("\n == 属性名属性错误" + fieldName);
						continue;
					}
					
					sql.append(split).append("`").append(column).append("` ");
					split = ",";
				}
			}
			
			sql.append(" FROM `").append(tableName).append("` WHERE 1");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			return jdbc.getList(entityClass, sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public List<T> load(RepositoryFilter filter, int pageNo, int rowSize,
			String... requiredFields) {
		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			if(CollectionUtil.isEmpty(requiredFields)) {
				sql.append("*");
			} else {
				String split = "";
				for(String fieldStr : requiredFields) {
					String column = fieldToColumnMap.get(fieldStr);
					if(StringUtil.isEmpty(column)) {
						LogHelper.error("\n == 属性名属性错误" + fieldStr);
						continue;
					}
					
					sql.append(split).append("`").append(column).append("` ");
					split = ",";
				}
			}
			
			sql.append(" FROM `").append(tableName).append("` WHERE 1");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT :rowOffset, :rowSize");
			params.put("rowOffset", (pageNo - 1) * rowSize);
			params.put("rowSize", rowSize);
			
			return jdbc.getList(entityClass, sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public List<T> load(RepositoryFilter filter, int limit,
			String... requiredFields) {
		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			if(CollectionUtil.isEmpty(requiredFields)) {
				sql.append("*");
			} else {
				String split = "";
				for(String fieldStr : requiredFields) {
					String column = fieldToColumnMap.get(fieldStr);
					if(StringUtil.isEmpty(column)) {
						LogHelper.error("\n == 属性名属性错误" + fieldStr);
						continue;
					}
					
					sql.append(split).append("`").append(column).append("` ");
					split = ",";
				}
			}
			
			sql.append(" FROM `").append(tableName).append("` WHERE 1");
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT :limit");
			params.put("limit", limit);
			
			return jdbc.getList(entityClass, sql.toString(), params);
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public <T2> T2 get(Object key, DataConvertor<T2, T> dataConvertor) {
		if(dataConvertor == null) {	
			LogHelper.error("数据转换对象不能为空dataConvertor:null");
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		if(dataConvertor.getConvertClass() == null) {
			T po = get(key);
			
			T2 bean = dataConvertor.convert(po);
			dataConvertor.build(bean, jdbc);
			
			return bean;
		}
		
		try {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			
			StringBuilder sb = getSelectSql(dataConvertor);
			
			String sp=" and ";
			
			if( idFields.size()>1 && EntitySpecification.isEmbeddableAccessor(key)) {
				Map<Class<?>,Set<Field>> accessor = EntitySpecification.getAllAccessor(key.getClass());
				Set<Field> keyField = accessor.get(Column.class);
				
				Field field = null;
				String name = null;
				Method method = null;
				Object val = null;
				Iterator<Field> fieldIter = keyField.iterator();
				while(fieldIter.hasNext()) {
					field = fieldIter.next();
					name = EntitySpecification.getName(field);
					method = EntitySpecification.getReadMethod(field);
					val = method.invoke(key);
					
					sb.append(sp).append("`").append(name).append("` = :"+name);
					sp=" and ";
					
					paramMap.put(name, val);
				}
			} else {
				String fieldName = idFields.iterator().next();
				String column = fieldToColumnMap.get(fieldName);
				
				sb.append(sp).append("`").append(column).append("` = :"+fieldName);
				paramMap.put(fieldName, key);
			}
			
			T2 bean = jdbc.getEntity(dataConvertor.getConvertClass(), sb.toString(), paramMap);
			dataConvertor.build(bean, jdbc);
			
			return bean;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public <T2> T2 get(RepositoryFilter filter, DataConvertor<T2, T> dataConvertor, String... requiredFields) {
		if(dataConvertor == null) {	
			LogHelper.error("数据转换对象不能为空dataConvertor:null");
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		if(dataConvertor.getConvertClass() == null) {
			T po = get(filter, requiredFields);
			
			T2 bean = dataConvertor.convert(po);
			dataConvertor.build(bean, jdbc);
			
			return bean;
		}
		
		try {
			StringBuilder sql = getSelectSql(dataConvertor, requiredFields);
						
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT 1");
			T2 bean = jdbc.getEntity(dataConvertor.getConvertClass(), sql.toString(), params);
			dataConvertor.build(bean, jdbc);
			return bean;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	@Override
	public <T2> List<T2> load(RepositoryFilter filter, DataConvertor<T2, T> dataConvertor, String... requiredFields) {
		if(dataConvertor == null) {	
			LogHelper.error("数据转换对象不能为空dataConvertor:null");
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		if(dataConvertor.getConvertClass() == null) {
			List<T> po = load(filter, requiredFields);
			
			return convertList(po, dataConvertor);
		}
		
		try {
			StringBuilder sql = getSelectSql(dataConvertor, requiredFields);
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			List<T2> beans = jdbc.getList(dataConvertor.getConvertClass(), sql.toString(), params);
			dataConvertor.builds(beans, jdbc);
			return beans;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public <T2> List<T2> load(RepositoryFilter filter, int pageNo, int rowSize, DataConvertor<T2, T> dataConvertor,
			String... requiredFields) {
		if(dataConvertor == null) {	
			LogHelper.error("数据转换对象不能为空dataConvertor:null");
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		if(dataConvertor.getConvertClass() == null) {
			List<T> po = load(filter, pageNo, rowSize, requiredFields);
			
			return convertList(po, dataConvertor);
		}
		
		try {
			StringBuilder sql = getSelectSql(dataConvertor, requiredFields);
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT :rowOffset, :rowSize");
			params.put("rowOffset", (pageNo - 1) * rowSize);
			params.put("rowSize", rowSize);
			
			List<T2> beans = jdbc.getList(dataConvertor.getConvertClass(), sql.toString(), params);
			dataConvertor.builds(beans, jdbc);
			return beans;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}

	@Override
	public <T2> List<T2> load(RepositoryFilter filter, int limit, DataConvertor<T2, T> dataConvertor,
			String... requiredFields) {
		if(dataConvertor == null) {	
			LogHelper.error("数据转换对象不能为空dataConvertor:null");
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		if(dataConvertor.getConvertClass() == null) {
			List<T> po = load(filter, limit, requiredFields);
			
			return convertList(po, dataConvertor);
		}
		
		try {
			StringBuilder sql = getSelectSql(dataConvertor, requiredFields);
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			attachQuery(sql, filter, params);
			attachOrder(sql, filter, params);
			
			sql.append(" LIMIT :limit");
			params.put("limit", limit);
			
			List<T2> beans = jdbc.getList(dataConvertor.getConvertClass(), sql.toString(), params);
			dataConvertor.builds(beans, jdbc);
			return beans;
		} catch(Exception e) {
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY,e);
		}
	}
	
	private StringBuilder getSelectSql(DataConvertor<?, T> dataConvertor, String... requiredFields) {
		if(dataConvertor == null) {
			LogHelper.error("数据转换对象不能为空dataConvertor:" + dataConvertor);
			throw new RepositoryGeneralException(ExceptionCode.BASE_JDBC_QUERY);
		}
		
		StringBuilder sql = new StringBuilder("SELECT ");
		Set<String> selectFields = fieldToColumnMap.keySet();
		if(!CollectionUtil.isEmpty(requiredFields)) selectFields = new HashSet<>(Arrays.asList(requiredFields));
		String split = "";
		for(String field : selectFields) {
			String rewriteField = dataConvertor.fieldConvert(field);
			String column = fieldToColumnMap.get(field);
			if(StringUtil.isEmpty(column)) {
				LogHelper.error("\n ===========================\n 属性名错误:" + field + "找不到对应的数据库字段\n=========================");
				continue;
			}
			rewriteField = (rewriteField == null ? field : rewriteField);
			sql.append(split).append(" `").append(column).append("` AS `").append(rewriteField).append("`");
			split = ",";
		}
		
		sql.append(" FROM `").append(tableName).append("` WHERE 1");
		
		return sql;
	}
	
	private <T2> List<T2> convertList(List<T> pos, DataConvertor<T2, T> dataConvertor) {
		List<T2> returns = new ArrayList<T2>();
		if(!CollectionUtil.isEmpty(pos))
			for(T po : pos)
				returns.add(dataConvertor.convert(po));
		
		dataConvertor.builds(returns, jdbc);
		
		return returns;
	}
}
