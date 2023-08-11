package com.kh.spring.common.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class BeforeTest {
	
	@Before("CommonPointcut.implPointcut()")
	public void serviceStart(JoinPoint jp) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n ============================================================================== \n");
		sb.append("start : " + jp.getTarget().getClass().getSimpleName() + " - ");
		sb.append(jp.getSignature().getName());
		sb.append("("+ Arrays.toString(jp.getArgs()) +")");
		log.info(sb.toString());
	}
}
