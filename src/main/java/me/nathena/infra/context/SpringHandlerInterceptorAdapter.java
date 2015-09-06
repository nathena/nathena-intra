package me.nathena.infra.context;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.nathena.infra.base.BaseControl;
import me.nathena.infra.base.MustLoginedInterface;
import me.nathena.infra.base.RequestValidate;
import me.nathena.infra.base.RequestValidateResponse;
import me.nathena.infra.base.RequestValidator;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.StringUtil;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SpringHandlerInterceptorAdapter extends HandlerInterceptorAdapter
{

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, 
			Object handler) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		if( handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Object bean = handlerMethod.getBean();

			RequestValidate validates = handlerMethod.getMethodAnnotation(RequestValidate.class);
			if(validates != null) {
				String errMsg = RequestValidator.getErrorMsg(request, validates);
				if(!RequestValidator.isPass(errMsg)) {
					if(bean instanceof RequestValidateResponse) {
						((RequestValidateResponse)bean).validateResponse(request, response, 
								validates.failedView(), errMsg);
					} else {
						defaultValidateResponse(request, response, validates.failedView(), errMsg);
					}
					return false;
				}
			}
			
			if(bean instanceof BaseControl) {
				BaseControl obj = (BaseControl)bean;
				
				if(!obj.preHandle(request, response)) {
					return false;
				}
			}
			
			if(bean instanceof MustLoginedInterface) {
				MustLoginedInterface obj = (MustLoginedInterface)bean;
				if(!obj.isLogined(request,response)) {
					return false;
				}
			}
		}
		
		return super.preHandle(request, response, handler);
	}
	
	//参数验证失败时返回的默认实现,可实现RequestValidateResponse接口自定义返回
	//默认采用rest风格以http状态码表征请求错误
	private void defaultValidateResponse(HttpServletRequest request, HttpServletResponse response, 
			String failedTargetView, String msg) {
		PrintWriter writer = null;
		try {
			if(StringUtil.isEmpty(failedTargetView)) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				writer = response.getWriter();
				writer.write(msg);
				writer.flush();
			} else {
				request.setAttribute("errMsg", msg);
				request.getRequestDispatcher(failedTargetView).forward(request, response);
			}			
		} catch (ServletException | IOException e) {
			LogHelper.error("跳转时错误", e);
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, 
			Object handler, Exception ex) throws Exception {
		
		if( handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Object bean = handlerMethod.getBean();
			
			if(bean instanceof BaseControl) {
				BaseControl obj = (BaseControl)bean;
				if(!obj.afterCompletion(request, response)) {
					return;
				}
			}
		}
		
		super.afterCompletion(request, response, handler, ex);
	}
}
