package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@Order(5)
public class AfterReturningTest {
	
	//@AfterReturning : 메서드 실행 이후에 반환값을 얻어오는 기능의 어노테이션
	// returning : 반환한 예외값을 저장할 매개변수명을 지정하는 속성
	@AfterReturning(pointcut = "CommonPointcut.implPointcut()", returning ="returnObj")
	public void returnValue(JoinPoint jp, Object returnObj) {
		
		if(returnObj instanceof Member) {
			Member loginUser = (Member) returnObj;
			loginUser.setUserName("해린");
		}
		log.info("return value : {}", returnObj);
	}

}
