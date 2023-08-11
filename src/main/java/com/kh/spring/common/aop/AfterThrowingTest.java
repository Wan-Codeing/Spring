package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class AfterThrowingTest {
	
	// @AfterThrowing : 메서드 실행이후에 발생하는 예외를 얻어오는 어노테이션
	//		 throwing : 반환한 예외값을 저장할 매개변수명을 지정하는 속성
	
	@AfterThrowing(pointcut = "CommonPointcut.implPointcut()", throwing ="exceptionObj")
	public void returnValue2(JoinPoint jp, Exception exceptionObj) {
		StringBuilder sb = new StringBuilder("Exception : "+exceptionObj.getStackTrace()[0]);
		sb.append("에러메세지 : " + exceptionObj.getMessage()+"\n");
		
		log.error(sb.toString());
	}

}
