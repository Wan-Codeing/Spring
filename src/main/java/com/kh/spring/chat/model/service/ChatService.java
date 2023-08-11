package com.kh.spring.chat.model.service;

import java.util.List;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

public interface ChatService {

	int openChatRoom(ChatRoom room);
	
	List<ChatRoom> selectChatRoomList();
	
	int insertMessage(ChatMessage cm);
	
	List<ChatMessage> joinChatRoom(ChatRoomJoin join);
	
	int exitChatRoom(ChatRoomJoin join);
}
