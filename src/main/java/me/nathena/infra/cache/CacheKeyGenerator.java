package me.nathena.infra.cache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.interceptor.KeyGenerator;

public class CacheKeyGenerator implements KeyGenerator {

	public static final int NO_PARAM_KEY = 0;
	public static final int NULL_PARAM_KEY = 53;
	
	@Override
	public Object generate(final Object target, final Method method, Object... params) 
	{
		final List<Object> key = new ArrayList<Object>();
        key.add(target.getClass().getName());
        key.add(method.getName());

        if (params.length == 0) {
        	key.add( NO_PARAM_KEY);
		}
        else if (params.length == 1) {
        	key.add( (params[0] == null ? NULL_PARAM_KEY : params[0].hashCode()) );
		}
        else
        {
        	int hashCode = 17;
        	for (final Object o : params) {
        		hashCode = 31 * hashCode + (o == null ? NULL_PARAM_KEY : o.hashCode()) ;
            }
        	
        	key.add(hashCode);
        }
        
        return key;
	}
}
