package com.kh.spring.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"loginUser","chatRoomNo"})
public class ChatController {

	
	@Autowired
	private ChatService service;
	
	@GetMapping("/chat/chatRoomList")
	public String  selectChatRoomList(Model model) {
		
		//업무로직 - db에서 채팅방 목록 조회
		List<ChatRoom> crList = service.selectChatRoomList();
		log.info("채팅방 리스트 ===== {} " , crList);
		model.addAttribute("chatRoomList", crList);
		
		return "chat/chatRoomList";
	}
	
	
	@PostMapping("/chat/openChatRoom")
	public String openChatRoom(
			@ModelAttribute("loginUser") Member loginUser,
			Model model,
			ChatRoom room,
			RedirectAttributes ra
			) {
		
		room.setUserNo(loginUser.getUserNo());
		
		int chatRoomNo = service.openChatRoom(room);
		
		String path = "redirect:/chat/";
		
		if(chatRoomNo > 0) {
			ra.addFlashAttribute("alertMsg", "채팅방 생성 성공!");
			path +="room/"+chatRoomNo;
		} else {
			
			ra.addFlashAttribute("alertMsg", "채팅방 만들기 실패");
			path += "chatRoomList";
		}
		
		return path;
	}
	
	//채팅방 입장
	@GetMapping("/chat/room/{chatRoomNo}")
	public String joinChatRoom(
			@ModelAttribute("loginUser") Member loginUser,
			Model model,
			@PathVariable("chatRoomNo") int chatRoomNo,
			ChatRoomJoin join,
			RedirectAttributes ra
			) {
		join.setUserNo(loginUser.getUserNo());
		join.setChatRoomNo(chatRoomNo);
		
		// 업무로직 - 채팅방의 채팅내용 불러오기. + 채팅방 입장기능
		List<ChatMessage> list = service.joinChatRoom(join);
		log.info("넘겨받은 list ===== {}", list);
		if(list != null) {
			model.addAttribute("list",list);
			model.addAttribute("chatRoomNo",chatRoomNo); // session에 올림
			
			return "chat/chatRoom";
		}else {
			ra.addFlashAttribute("alertMsg", "채팅방이 존재하지 않습니다. 아니 있었는데? 아뇨 없어요~");
			return "redirect:/chat/chatRoomList";
		}
	}
	
	@GetMapping("/chat/exit/{chatRoomNo}")
	public String exitChatRoom(
			@ModelAttribute("loginUser") Member loginUser,
			ChatRoomJoin join,
			@PathVariable("chatRoomNo") int chatRoomNo,
			Model model
			) {
		// 업무로직 -> 채팅방 나가기 ChatRommJoin에서 사용자 정보제거
		// -> 마지막으로 나간 인원이 본인이라면, 채팅방 삭제
		join.setUserNo(loginUser.getUserNo());
		join.setChatRoomNo(chatRoomNo);
		
		int result = service.exitChatRoom(join);
		
		if(result > 0) {
			return "redirect:/chat/chatRoomList";
		}else {
			model.addAttribute("errorMsg","넌 못지나간다!");
			return "common/errorPage";
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
