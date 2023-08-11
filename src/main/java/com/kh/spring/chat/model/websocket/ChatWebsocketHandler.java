package com.kh.spring.chat.model.websocket;

import java.sql.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWebsocketHandler extends TextWebSocketHandler{
	
	@Autowired
	private ChatService chatService;	

	/*
	 * WebSocketHandler 인터페이스 : 웹소켓을 위한 메소드를 지원하는 인터페이스
	 * -> TextWebSocketHandler : 웹소켓핸들러 인터페이스를 구현한 클래스로 "Text"관련된 기능을 처리하는데 중점을 두었다.
	 *  
	 *  * 웹소캣 핸들러 주요 메서드 *
	 *  
	 *  void handlerMessage(WebSocketSession session, WebSocketMessage message)
	 *   - 클라이언트로부터 메세지가 도착했을시 실행.
	 *   
	 *  void afterConnectionEstablished(WebSocketSession session)
	 *   - 클라이언트와 연결이 완료되고, 통신할 준비가 되면 실행
	 *   
	 *  void afterConnectionClosed(WebSoketSession session, closeStatus closeStaus)
	 *   - 클라이언트와 연결이 "종료"되면 실행
	 *   
	 *  void handlerTransportError(WebSocketSession session, Throwable exception)
	 *   - 메세지 "전송"중 에러가발생하면 실행
	 *  */
	
	private Set<WebSocketSession> sessions =  Collections.synchronizedSet(new HashSet<WebSocketSession>()) ;
	/* 
	 * synchronizedSet : 동기화된 set을 반환해주는 메소드
	 *                   멀티스레드환경에서 하나의 컬렉션요소에 여러 스레드가 동시에 접근하면 충돌이 발생 할 수 있으므로
	 *                   동기화를 진행
	 * */
	
	// 클라이언트와 연결수립 및 통신준비 완료시 수행되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		
		// WebSocketSession : 웹소켓에 접속요청한 클라이언트의 세션
		log.info("{} 가 연결됨 " + session.getId()); // 세션의 아이디 확인
		
		sessions.add(session);
	}
	
	// 클라이언트와 연결이 종료되면 수행
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		sessions.remove(session);
		// 웹소켓이 연결이 종료되는 경우 sessions내부에 있는 클라이언트의 session정보를 삭제.
	}
	
	
	// 클라이언트로부터 텍스트 메세지를 전달 받았을때 수행
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		// TextMessage : 웹소켓을 이용해 전달된 텍스트가 담겨있는 객체
		
		// payload : 전송되는 데이터(JSON객체로 전달)
		
		log.info("전달된 메세지 : {} " , message.getPayload());
		
		// JackSon라이브러리 : java에서 json을 다루기 위한 라이브러리
		// Jackson-databind -> objectMapper를 이용해서 JSON형태로 넘어온 데이터를 특정 vo필드에 맞게 자동 매핑
		ObjectMapper objectMapper = new ObjectMapper();
		
		ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
		
		log.info("채팅 메세지 : {}" ,chatMessage);
		
		chatMessage.setCreateDate(new Date(System.currentTimeMillis()));
		
		// 전달받은 메세지를 db에 삽입
		int result = chatService.insertMessage(chatMessage);
		if(result > 0) {
			//같은방에 접속중인 클라이언들에게 전달받은 메세지를 보내기.
			for(  WebSocketSession s          :         sessions) {
				// 반복을 진행중인 WebSocketSession에 담겨있는 방번호 빼오기.
				int chatRoomNo = (Integer) s.getAttributes().get("chatRoomNo");
				
				log.info("채팅을 보낸 방번호 : {}",chatRoomNo);
				
				// 메세지에 담겨있는 채팅방 번호와 현재 웹소켓 세션에있는 채팅방번호를 비교
				if(chatMessage.getChatRoomNo()  == chatRoomNo) {
					// 같은방 사용자라면 클라이언트에게 JSON형식으로 메세지 보내기.
					s.sendMessage(new TextMessage( new Gson().toJson(chatMessage)    ));
				}
			}
			
		}
		
	}
	
	
	
	
	
}
