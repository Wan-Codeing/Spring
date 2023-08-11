package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@Order(1)
public class Aftertest {
	
	@After("CommonPointcut.implPointcut()")
	public void serviceEnd(JoinPoint jp) {
		// joinpoint : advice가 적용될 수 있는 모든 후보 메서드
		
		// JoinPoint 인터페이스 : advice가 실제로 적용되는 Target Object에 접근할수 있는 메서드를 제공하는 인터페이스
		//						=> 해당 클래스의 정보, 전달되는 매개변수, 메서드명, 반환값, 예외 정보 등을 얻어올수 있음
		// 						=> 항상 첫번째 매개변수로 JoinPoint가 들어가야함
		StringBuilder sb = new StringBuilder();
		
		// getTarget() : advice가 적용된 객체를 반환(각 *ServiceImpl클래스들)
		sb.append("end : " + jp.getTarget().getClass().getSimpleName() + " - ");
		// getSignature : 수행되는ㅇ 메서드의 정보
		sb.append(jp.getSignature().getName()); //실행한 메서드명
		sb.append("\n ============================================================================== \n");
		log.info(sb.toString());
	}}
