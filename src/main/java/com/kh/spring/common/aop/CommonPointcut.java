package com.kh.spring.common.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcut {
	// Pointcut을 모아놓은 클래스
	
	// 게시판 서비스용
	@Pointcut("execution(* com.kh.spring.board..*Impl.*(..))")
	public void boardPointcut() {}
	
	// 모든 서비스용
	@Pointcut("execution(* com.kh.spring..*Impl.*(..))")
	public void implPointcut() {}
}
