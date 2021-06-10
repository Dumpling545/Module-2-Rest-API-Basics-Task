package com.epam.esm.service.impl.aspect;

import com.epam.esm.service.exception.ServiceException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;

/**
 * Aspect used to wrap all uncaught data access exceptions into
 * ServiceException
 */
@Aspect
public class ServiceExceptionAspect {
	@AfterThrowing(pointcut = "execution(* com.epam.esm.service.impl.*(..))",
			throwing = "ex")
	public void rethrowServiceException(DataAccessException ex)
			throws ServiceException
	{
		throw new ServiceException(ex);
	}
}
