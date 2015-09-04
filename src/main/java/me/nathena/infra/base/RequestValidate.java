/**
 * 
 */
package me.nathena.infra.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import me.nathena.infra.utils.LogHelper;
import me.nathena.infra.utils.StringUtil;
import me.nathena.infra.utils.Validator;

/**
 * @author GaoWx
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestValidate {
	String[] fileds() default {};
	String failedView() default "";
	
	public static final class RequestValidateRule {
		private static final List<String> RULES = Arrays.asList(new String[]{"REQUIRED", "MOBILE", "INTEGER", "DOUBLE"});
		private static final List<String> INNERRULES = Arrays.asList(new String[]{"MAX", "MIN", "IN"});
		private RequestValidateRule(){};
		
		public static boolean validate(String rule, String value) {
			if(rule == null) {
				LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
				return true;
			}
			
			String upcaseRule = rule.toUpperCase();
			String[] innerRules = upcaseRule.split(":");
			
			if(!RULES.contains(innerRules[0])) {
				LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
				return true;
			}
			
			switch(innerRules[0]) {
			case "REQUIRED":
				return !StringUtil.isEmpty(value);
			case "MOBILE":
				return Validator.isMobile(value);
			case "INTEGER":
				if(!Validator.isNumber(value)) {
					return false;
				}
				
				if(innerRules.length == 3 && INNERRULES.contains(innerRules[1].toUpperCase())) {
					switch(innerRules[1].toUpperCase()) {
					case "MAX":
						return  Validator.isNumber(innerRules[2]) && Integer.valueOf(value) <= Integer.valueOf(innerRules[2]);
					case "MIN":
						return  Validator.isNumber(innerRules[2]) && Integer.valueOf(value) >= Integer.valueOf(innerRules[2]);
					}
				}
				
				return true;
			case "DOUBLE":
				if(!Validator.isFloat(value)) {
					return false;
				}
				
				if(innerRules.length == 3 && INNERRULES.contains(innerRules[1].toUpperCase())) {
					switch(innerRules[1].toUpperCase()) {
					case "MAX":
						return  Validator.isFloat(innerRules[2]) && Double.valueOf(value) <= Double.valueOf(innerRules[2]);
					case "MIN":
						return  Validator.isFloat(innerRules[2]) && Double.valueOf(value) >= Double.valueOf(innerRules[2]);
					}
				}
				
				return true;
			default:
				LogHelper.warn("\n 参数验证规则书写有误 rule :" + rule);
				return true;
			}
		}
	}
}
