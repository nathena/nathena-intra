package me.nathena.infra.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MustLoginedInterface {

	public boolean isLogined(HttpServletRequest request,HttpServletResponse response);
}
