package me.nathena.infra.base;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.StringUtil;
import me.nathena.infra.utils.Validator;

public class RequestValidator {
	private static final List<String> RULES = Arrays.asList(new String[]{"REQUIRED", "MOBILE", "INTEGER", "DOUBLE"});
	private static final List<String> SECOND_RULES = Arrays.asList(new String[]{"MAX", "MIN", "IN"});
	private RequestValidator(){};
	
	public static boolean isPass(String errMsg) {
		return StringUtil.isEmpty(errMsg);
	}
	
	public static final String getErrorMsg(HttpServletRequest request, RequestValidate validates) {
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
					if(!validate(rule, parameterValue)) {
						LogHelper.info("\n 参数验证失败:" + filedName + ",要求:" + rules + ",实际:" + parameterValue);
						return msg;
					}
				}
			}
		}
		
		return null;
	}
	
	public static boolean validate(String rule, String value) {
		if(rule == null) {
			LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
			return true;
		}
		
		String upcaseRule = rule.toUpperCase();
		String[] secondRules = upcaseRule.split(":");
		
		if(!RULES.contains(secondRules[0])) {
			LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
			return true;
		}
		
		switch(secondRules[0]) {
		case "REQUIRED":
			return !StringUtil.isEmpty(value);
		case "MOBILE":
			return StringUtil.isEmpty(value) || Validator.isMobile(value);
		case "INTEGER":
			if(!Validator.isNumber(value)) {
				return false;
			}
			
			if(secondRules.length == 3 && SECOND_RULES.contains(secondRules[1].toUpperCase())) {
				switch(secondRules[1].toUpperCase()) {
				case "MAX":
					return  Validator.isNumber(secondRules[2]) && Integer.valueOf(value) <= Integer.valueOf(secondRules[2]);
				case "MIN":
					return  Validator.isNumber(secondRules[2]) && Integer.valueOf(value) >= Integer.valueOf(secondRules[2]);
				}
			}
			
			return true;
		case "DOUBLE":
			if(!Validator.isFloat(value)) {
				return false;
			}
			
			if(secondRules.length == 3 && SECOND_RULES.contains(secondRules[1].toUpperCase())) {
				switch(secondRules[1].toUpperCase()) {
				case "MAX":
					return  Validator.isFloat(secondRules[2]) && Double.valueOf(value) <= Double.valueOf(secondRules[2]);
				case "MIN":
					return  Validator.isFloat(secondRules[2]) && Double.valueOf(value) >= Double.valueOf(secondRules[2]);
				}
			}
			
			return true;
		default:
			LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
			return true;
		}
	}
}
