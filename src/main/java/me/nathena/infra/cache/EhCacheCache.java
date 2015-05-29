package me.nathena.infra.cache;

import java.util.List;

import me.nathena.infra.utils.LogHelper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

public class EhCacheCache implements Cache 
{
	private final Ehcache cache;
	private static final String[] evictKeys = new String[]{"remove","delete","update","add","create","insert","save","merge","change","modify"};

	/**
	 * Create an {@link EhCacheCache} instance.
	 * @param ehcache backing Ehcache instance
	 */
	public EhCacheCache(Ehcache ehcache) {
		Assert.notNull(ehcache, "Ehcache must not be null");
		Status status = ehcache.getStatus();
		Assert.isTrue(Status.STATUS_ALIVE.equals(status), "An 'alive' Ehcache is required - current cache is " + status.toString());
		this.cache = ehcache;
	}


	public String getName() {
		return this.cache.getName();
	}

	public Ehcache getNativeCache() {
		return this.cache;
	}

	public ValueWrapper get(Object key) {
		Element element = this.cache.get(key);
		return (element != null ? new SimpleValueWrapper(element.getObjectValue()) : null);
	}

	@SuppressWarnings("rawtypes")
	public void put(Object key, Object value) {
		
		//key 为  com.e344.core.cache.ehcache.CacheKeyGenerator
		if( null!=key && key instanceof List )
		{
			String _putKey = (((List)key).get(1)).toString().toLowerCase();
			for(String evictKey : evictKeys )
			{
				if( _putKey.indexOf(evictKey) > -1 )
				{
					evict(key);
					return;
				}
			}
		}
		
		this.cache.put(new Element(key, value));
	}

	@SuppressWarnings("rawtypes")
	public void evict(Object key) 
	{
		List keys = this.cache.getKeys();
		//key 为  com.e344.core.cache.ehcache.CacheKeyGenerator
		if( null != key && key instanceof List)
		{
			for( Object _key : keys )
			{
				if( null != _key && _key instanceof List )
				{
					Object _evictKey = ((List)key).get(0);
					Object _putKey = ((List)_key).get(0);
					
					if( _evictKey.toString().equals(_putKey.toString()) )
					{
						LogHelper.debug(this.getClass().getName()+" evict key : "+_key);
						this.cache.remove(_key);
					}
				}
			}
		}
		else
		{
			this.cache.remove(key);
		}
	}

	public void clear() {
		this.cache.removeAll();
	}
	
}
