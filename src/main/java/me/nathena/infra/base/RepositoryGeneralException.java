package me.nathena.infra.base;



public class RepositoryGeneralException extends NestedRuntimeException
{
	public RepositoryGeneralException(ExceptionCode code) {
		super(code.getCode(),code.getMsg());
	}
	
	public RepositoryGeneralException(ExceptionCode code,Throwable cause) {
		super(code.getCode(),code.getMsg(),cause);
	}
}
  
