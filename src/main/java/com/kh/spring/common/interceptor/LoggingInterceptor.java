package com.kh.spring.common.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingInterceptor extends HandlerInterceptorAdapter	{
	
	//사용자가 사용하고있는 핸드폰 종류
	static String logMP[] = {"iphone","ipod","andriod"};
	
	/*
	 * 모든 경로로 들어오는 요청에 대한 로그를 남기기 위한 메소드 Controller호출 전에 실행됨
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 접속된 장비가 무엇인지(웹? 모바일?)
		String currentDevice ="web";
		String logUA = request.getHeader("user-agent").toLowerCase();
		for( String device : logMP) {
			if(logUA.indexOf(device) > 0) {
				currentDevice = "mobile";
				break;
			}
		}
		// 접속 url, 서버정보 추가
		HttpSession session = request.getSession();
		
		String currentDomain= request.getServerName();
		int currentPort = request.getLocalPort();
		String queryString = "";
		if(request.getMethod().equals("GET")) {
			queryString = request.getQueryString();
		}else {
			Map map = request.getParameterMap();
			Object[] keys = map.keySet().toArray();
			for(int i=0 ; i<keys.length ; i++) {
				if(i > 0) {
					queryString += "&";
				}
				String[] values = (String[])map.get(keys[i]);
				queryString += keys[i]+"=";
				
				int count = 0;
				for(String str : values) {
					if( count > 0 ) {
						queryString += ",";
					}
					queryString += str;
					count++;
				}
			}
		}
		// 파라미터가 아예 없다면 로그에 포함시키지 않도록 조건 추가
		if(queryString == null || queryString.trim().length() == 0) {
			queryString = null;
		}
		// 아이디 정보 추가
		String userId = "";
		Member loginUser = (Member) session.getAttribute("log");
		if(loginUser != null) {
			userId = loginUser.getUserId();
		}
		// IP정보 추가
		String uri = request.getRequestURI();
		String ip = getIp(request);
		
		log.info(ip+" : "+currentDevice+" : "+userId+" "+(request.isSecure() ? "https" : "http")+"://"+currentDomain+":"+currentPort+uri+(queryString != null ? "?" +queryString : ""));
		return true;
	}
	
	// 후처리 함수
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
	throws Exception
	{
		super.postHandle(request, response, handler, modelAndView);
		log.info("----------------------------------------------");
		log.info("modelAndView = {}",modelAndView);
	}
	
	// jsp 작업 완룡 이후
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
	throws Exception
	{
		log.info("--------------------------------view------------------------------------------");
		super.afterCompletion(request, response, handler, ex);
		log.info("--------------------------------end-------------------------------------------");
	}
	
	
	public String getIp(HttpServletRequest request) {
		 String ip = request.getHeader("X-Forwarded-For");
		   
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	         ip = request.getHeader("Proxy-Client-IP");
	      }
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	         ip = request.getHeader("WL-Proxy-Client-IP");
	      }
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	         ip = request.getHeader("HTTP_CLIENT_IP");
	      }
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	         ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	      }
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	         ip = request.getRemoteAddr();
	      }
	      return ip;
	}
	
}
