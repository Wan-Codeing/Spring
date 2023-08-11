package com.kh.spring.chat.model.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

@Repository
public class ChatDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	
	public int openChatRoom(ChatRoom chatRoom) {
		
		int result = sqlSession.insert("chattingMapper.openChatRoom" , chatRoom);
		
		if(result > 0) {
			result = chatRoom.getChatRoomNo();
		}
		return result;
	}
	
	public List<ChatRoom> selectChatRoomList(){
		return sqlSession.selectList("chattingMapper.selectChatRoomList");
	}
	
	public int insertMessage(ChatMessage cm) {
		return sqlSession.insert("chattingMapper.insertMessage", cm);
	}
	
	public int joinCheck(ChatRoomJoin join) {
		return sqlSession.selectOne("chattingMapper.joinCheck",join);
	}
	
	public void joinChatRoom(ChatRoomJoin join) {
		sqlSession.insert("chattingMapper.joinChatRoom",join);
	}
	
	public List<ChatMessage> selectChatMessage(int chatRoomNo){
		return sqlSession.selectList("chattingMapper.selectChatMessage",chatRoomNo);
	}
	
	public int exitChatRoom(ChatRoomJoin join) {
		return sqlSession.delete("chattingMapper.exitChatRoom",join);
	}
	
	public int countChatRoomMember(int chatRoomNo) {
		return sqlSession.selectOne("chattingMapper.countChatRoomMember",chatRoomNo);
	}
	
	public int closeChatRoom(int chatRoomNo) {
		return sqlSession.update("chattingMapper.closeChatRoom",chatRoomNo);
	}
	
	
}
