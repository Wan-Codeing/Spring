package com.kh.spring.common.interceptor;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.BoardType;

public class BoardTypeInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	private ServletContext application;
	
	@Autowired
	private BoardDao boardDao;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		// application scope에 boardTypeList를 저장
		
		// 항상 저장시키는게 아니라 처음 한번.(application에 boardTypeList가 없는경우에만)
		// application 객체 얻어오기
		if(application.getAttribute("boardTypeList") == null) {
			
			//db에서 boardTypeList를 조회해서 application에 저장
			List<BoardType> boardTypeList = boardDao.selectBoardTypeList();
			
			application.setAttribute("boardTypeList", boardTypeList);
			
			application.setAttribute("contextPath", request.getContextPath());
		}
		
		
		return true;
	}
	
}

