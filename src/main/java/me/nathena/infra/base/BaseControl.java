package me.nathena.infra.base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.nathena.infra.context.AppsContext;
import me.nathena.infra.context.RequestContext;
import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.NumberUtil;
import me.nathena.infra.utils.StringUtil;

public abstract class BaseControl implements RequestParamValidateController {
	
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response)
	{
		return true;
	}

	public boolean afterCompletion(HttpServletRequest request,HttpServletResponse response)
	{
		return true;
	}
	
	/**
	 * 统一参数验证处理
	 */
	public boolean validate(HttpServletRequest request,HttpServletResponse response, RequestValidate validates) {
		if(validates != null && validates.fileds() != null) {
			for(String filed : validates.fileds()) {
				String[] filedParts = filed.split(";");
				if(filedParts.length < 2) {
					continue;
				}
				String filedName = filedParts[0];
				String rules = filedParts[1];
				String msg = filedParts.length > 2 ? filedParts[2] : "参数错误";
				
				String parameterValue = request.getParameter(filedName);

				for(String rule : rules.split(",")) {
					if(!RequestValidate.RequestValidateRule.validate(rule, parameterValue)) {
						LogHelper.info("\n 参数验证失败:" + filedName + ",要求:" + rules + ",实际:" + parameterValue);
						String failedTargetView = StringUtil.isEmpty(validates.failedView()) ? "/" : validates.failedView();//跳转失败页面
						paramValidateFailResponse(request, response, failedTargetView, msg);
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 参数验证失败默认处理,具体业务可以覆盖改方法,根据具体业务返回信息
	 * @param request
	 * @param response
	 * @param validates
	 * @param msg
	 */
	protected void paramValidateFailResponse(HttpServletRequest request, HttpServletResponse response, String failedTargetView, String msg) {
		if(isAjaxRequest(request)) {
			toResponse(msg);
		} else {
			try {
				request.setAttribute("errMsg", msg);
				request.getRequestDispatcher(failedTargetView).forward(request, response);
			} catch (ServletException | IOException e) {
				LogHelper.error("\n 跳转异常", e);
			}
		}
	}
	
	public void toResponse(Object content)
	{
		LogHelper.debug("\n返回结果:==" + content.toString() + "==");
		toResponse(content,"text/plain");
	}
	
	public int getRequestPageNo()
	{
		RequestContext contexts = AppsContext.currentRequestContext();
		HttpServletRequest request = contexts.getRequest();
		String page = request.getParameter("page");
		if(page==null)
		{
			return 1;
		}
		else
		{
			return Integer.valueOf(page,10);
		}
	}
	
	public int getRequestPageRows()
	{
		RequestContext contexts = AppsContext.currentRequestContext();
		HttpServletRequest request = contexts.getRequest();
		
		String rows = request.getParameter("rows");
		if(rows==null)
		{
			int _pageRow = NumberUtil.parseInt(AppsContext.getProperty("pageRows"));
			
			return _pageRow>0?_pageRow:10;
		}
		else
		{
			return Integer.valueOf(rows,10);
		}
	}
	
	public boolean isAjaxRequest(HttpServletRequest request)
	{
		//ajax 请求
		if( "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) || NumberUtil.parseInt(request.getParameter("inajax")) > 0 )
		{
			return true;
		}
		return false;
	}
	
	private void toResponse(Object content,String content_type)
	{
		RequestContext contexts = AppsContext.currentRequestContext();
		HttpServletResponse response = contexts.getResponse();
		
		PrintWriter writer = null;
		try
		{
			if(content_type!=null)
			{
				response.setContentType(content_type);
			}
			response.setCharacterEncoding("UTF-8");
			
			writer = response.getWriter();
			writer.print(content);
			writer.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writer!=null)
			{
				writer.close();
			}
		}
	}
}
