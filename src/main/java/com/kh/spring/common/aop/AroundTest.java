package com.kh.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@Order(4)
public class AroundTest {
	
	// @Around : before + after 합쳐놓은 어노테이
	@Around("CommonPointcut.implPointcut()")
	public Object checkRunningTime(ProceedingJoinPoint jp) throws Throwable{
		// ProceddingJoinPoint : 전/후처리 관련된 기능을 제공. 값을 얻어올수 있는 메서드도 함께 제공한다.
		
		// proceed()메소드 호출전 : @Before 용도
		// before
		long start = System.currentTimeMillis();
		
		Object obj = jp.proceed(); // before와 after의 중간지점
		
		// proceed()메소드 호출후 : @After 용도
		//after
		long end = System.currentTimeMillis();
		
		log.info("Running Time : {} ms" , (end-start));
		return obj;
	}
	
}
