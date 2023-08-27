package com.server.auth.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthStubAop {
	// @Around("execution(* com.server.auth.controller.)")
	// public Object login() {
	// 	return new Object();
	// }
}
