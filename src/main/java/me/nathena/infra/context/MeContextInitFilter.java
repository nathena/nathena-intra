/**
 * 
 */
package me.nathena.infra.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GaoWx
 *
 */
public class MeContextInitFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			AppsContext.initRequestContext((HttpServletRequest)request, (HttpServletResponse)response);
		}
				
		chain.doFilter(request, response);
		
		if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			AppsContext.destoryRequestContext();
		}
	}

	@Override
	public void destroy() {
		
	}

}
