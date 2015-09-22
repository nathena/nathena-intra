/**
 * 
 */
package me.nathena.infra.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GaoWx
 *
 */
public interface RequestValidateResponse {
	public void validateResponse(HttpServletRequest request, HttpServletResponse response, String failedTargetView, String msg);
}
