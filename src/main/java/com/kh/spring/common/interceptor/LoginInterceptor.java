package com.kh.spring.common.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

// 로그인 여부를 체크해서 로그인하지 않은경우 , return false;
public class LoginInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
		
		
		// 요청 url정보 // /spring/board/list/C -> board/list/C
		String requestUrl = req.getRequestURI().substring(req.getContextPath().length());
		
		// 로그인한 사용자 정보 
		HttpSession session = req.getSession();
		Member loginUser = (Member) session.getAttribute("loginUser");
		
		//로그인 한 사용자라면 true/ 로그인하지 않은 사용자 false
		
		if(loginUser != null) {
			return true;
		}else {
			session.setAttribute("alertMsg", "넌 못지나간다!! Hint: 로그인하시오");
			
			// 로그인 완료후 이동할 url을 session영역안에 저장
			// http://localhost:8081/spring/chat/openChatRoom/1
			String url = req.getRequestURL().toString();
			String queryString = req.getQueryString();
			String nextUrl = url + "?" + queryString;
			
			session.setAttribute("nextUrl",nextUrl);
			
			res.sendRedirect(req.getContextPath()+"/");
			return false;
		}
		
		
	}
	
	
	
	
	
	
	
}
