package com.kh.spring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTest {
	
	/* 
	 * Logging
	 * - 콘솔로그 : System.out.pirntxx 보다 효율적인 로그관리가 가능.
	 * 
	 * Level(Priority) 우선순위
	 * 
	 * - fatal : 아주 심각한 에러 -> 지금 존재하지 않음.
	 * - error : 요청 처리중 오류 발생시 사용
	 * - wran  : 경고성 메세지. 현재 실행에는 문제가없지만, 향후 오류가 발생할 경우가 있을 경우 사용
	 * - info  : 요청 처리중 상태변경등의 정보성 메세지 출력시 사용@
	 * - debug : 개발중에 필요한 로그. (운영시 필요없음)
	 * - trace : 개발용 debug의 범위를 한정해서 출력
	 * 
	 * 
	 * Slf4j : 스프링에서 제공하는 logging 추상체
	 * 
	 * - 구현체 : log4j
	 * 
	 * 
	 * */
	public static void main(String[] args) {
		log.error("error - {}","에러메세지 ㅎ");
		log.warn("warn");
		log.info("info");
		log.debug("debug");
		log.trace("trace");
	}

}
