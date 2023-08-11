package com.kh.spring.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.chat.model.dao.ChatDao;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.common.Utils;

@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	private ChatDao chatDao;
	
	@Override
	public int openChatRoom(ChatRoom chatRoom) {
		return chatDao.openChatRoom(chatRoom);
	}
	
	@Override
	public List<ChatRoom> selectChatRoomList(){
		return chatDao.selectChatRoomList();
	}
	
	@Override
	public int insertMessage(ChatMessage cm) {
		
		cm.setMessage(Utils.XSSHandling(cm.getMessage()));
		cm.setMessage(Utils.newLineHandling(cm.getMessage()));
		
		return chatDao.insertMessage(cm);
	}
	
	@Override
	public List<ChatMessage> joinChatRoom(ChatRoomJoin join){
		// 현재회원이 해당 채팅방에 참여하고있는지 확인
		int result = chatDao.joinCheck(join);
		// 참여하고있지 않은경우 참여
		if(result == 0) {
			// 현재 참여하고있는 채팅방이 아님
			chatDao.joinChatRoom(join);
		}
		// 채팅메세지 목록 조회후 반환
		return chatDao.selectChatMessage(join.getChatRoomNo());
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int exitChatRoom(ChatRoomJoin join) {
		// 채팅방 나가기 (ChatRoomJoin테이블에서 delete문 실행)
		int result = chatDao.exitChatRoom(join);
		
		if(result > 0) {
			// 나가기 성공시
			// 현재 방에 몇명이 있나 확인
			int cnt = chatDao.countChatRoomMember(join.getChatRoomNo());
			// 현재 0명일 경우 방 닫기 => ChatRoom테이블에 status값을 update시킬 예정
			if(cnt == 0) {
				result = chatDao.closeChatRoom(join.getChatRoomNo());
			}
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
