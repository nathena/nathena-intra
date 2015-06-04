package me.nathena.infra.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.nathena.infra.base.BaseControl;
import me.nathena.infra.base.MustLoginedInterface;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SpringHandlerInterceptorAdapter extends HandlerInterceptorAdapter
{

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance 
		response.setDateHeader("Expires", -1); //Causes the proxy cache to see the page as "stale" 
		response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility 
		
		AppsContext.initRequestContext(request, response);
		
		if( handler instanceof HandlerMethod)
		{
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Object bean = handlerMethod.getBean();
			
			if(bean instanceof BaseControl)
			{
				BaseControl obj = (BaseControl)bean;
				if( !obj.preHandle(request, response) )
				{
					return false;
				}
			}
			
			if(bean instanceof MustLoginedInterface)
			{
				MustLoginedInterface obj = (MustLoginedInterface)bean;
				if(!obj.isLogined(request,response))
				{
					return false;
				}
			}
		}
		
		return super.preHandle(request, response, handler);
	}

	
	@Override
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		AppsContext.destoryRequestContext();
		
		if( handler instanceof HandlerMethod)
		{
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Object bean = handlerMethod.getBean();
			
			if(bean instanceof BaseControl)
			{
				BaseControl obj = (BaseControl)bean;
				if( !obj.afterCompletion(request, response) )
				{
					return;
				}
			}
		}
		
		super.afterCompletion(request, response, handler, ex);
	}
}
