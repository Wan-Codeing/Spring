package com.kh.spring.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;
import oracle.net.aso.s;

@Slf4j
@Controller
@RequestMapping("/board")
// 현재 컨트롤러 호출시 /spring/board의 경로로 들어오는 모든 url요청을 받아준다
@SessionAttributes({"loginUser"})
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ServletContext application;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@GetMapping("/list/{boardCode}")
	public String selectList(
			@PathVariable("boardCode") String boardCode,
			@RequestParam(value="currentPage",defaultValue="1") int currentPage,
			Model model,
			//검색요청이 들어오는경우 paramMap 내부에는 key와 condition이 있음
			@RequestParam Map<String, Object> paramMap
			) 
	{
		// @PathVariable("key") : URL경로에 포함되어있는 값을 변수로 사용할수 있게 해주는 어노테이션 + 자동으로 requestScope에 저장됨	
		// 페이징바
		// 게시글 목록
		paramMap.put("boardCode", boardCode);
		List<Board> list = boardService.selectList(currentPage, paramMap);
		
		// 총게시글 갯수
		int total = boardService.selectListCount(paramMap);
		int pageLimit = 10;
		int boardLimit = 5;
		PageInfo pi = Pagination.getPageInfo(total,currentPage,pageLimit,boardLimit);
		
		// 검색
		
			model.addAttribute("param",paramMap);
			model.addAttribute("list",list);
			model.addAttribute("pi",pi);
		return "board/boardListView";
	}
	
	@GetMapping("/insert/{boardCode}")
	public String enrollBoard(@PathVariable("boardCode") String boardCode, Model model) {
		
		return "board/boardEnrollForm";
	}
	
	@PostMapping("insert/{boardCode}")
	public String insertBoard(Board b, @RequestParam(value="upfile" , required=false) List<MultipartFile> upfiles,
			@PathVariable("boardCode") String boardCode, Model model, @ModelAttribute("loginUser") Member loginUser,
			HttpSession session) {
		// 이미지, 파일을 저장할 저장경로
		
		// / resources/images/board/{boardCode}/
		String webPath = "/resources/images/board/"+boardCode+"/";
		String severFolderPath = application.getRealPath(webPath);
		
		// Board객체에 데이터 추가(boardCode, boardWriter)
		b.setBoardWriter(loginUser.getUserNo()+"");
		b.setBoardCd(boardCode);
		log.info("board {}", b);
		
		// 디렉토리 생성 , 해당 디렉토리가 존재하지 않는다면 생성
		File dir = new File(severFolderPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// 첨부파일같은경우 선샡ㄱ하고 안하고 상관없이 객체는 생성이 된다 단, 길이가 0일수가 있음
		// 전달된 파일이 있는경우 해당 파일을 웹서버에 저장하고, Attachment에 해당 정보를 등록
		// 없는경우 위 프로세스를 패스할 것
		
		List<Attachment> attachList = new ArrayList();
		
		for( MultipartFile upfile : upfiles ) {
			if(upfile.isEmpty()) {
				continue;
			}
			// 1. 파일명 재정의해주는 함수
			String changeName = Utils.saveFile(upfile, severFolderPath);
			Attachment at = new Attachment();
			at.setChangeName(changeName);
			at.setOriginName(upfile.getOriginalFilename());
			attachList.add(at);
		}
		
		int result = 0;
		try {
			result = boardService.insertBoard(b, attachList, severFolderPath, webPath);
		} catch (Exception e) {
			log.error("error = {}", e.getMessage());
		}
		
		if(result > 0) {
			session.setAttribute("alertMsg", "게시글 작성 성공! ㅊㅋㅊㅋ");
			return "redirect:/board/list/"+boardCode;
		}else {
			model.addAttribute("errorMsg","게시글 작성 실패 ! ㅠㅠㅠㅠ다시해봐...");
			return "common/errorPage";
		}
	}
	
	@GetMapping("detail/{boardCode}/{boardNo}")
	public String selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			HttpSession session,
			Model model,
			HttpServletRequest req,
			HttpServletResponse res
			) {
		// 게시판 정보 조회 
		BoardExt board =boardService.selectBoard(boardNo);
		// 상세조회 성공시 쿠키를 이용해서 조회수 중복으로 조회되지 않도록 방지 + 본인의 글은 애초에 조회수 증가되지 않게 설정
		
		// 성공시
		
		String url = "";
		model.addAttribute("board",board);
		url ="board/boardDetailView";
		// 성공시
		if(board != null) {
			String userId = "";
			
			Member loginUser = (Member) session.getAttribute("loginUser");
			
			if(loginUser != null) {
				userId = loginUser.getUserId();
			}
			// 게시글 작성자의 아이디와, 현재 세션의 접속중인 아이디가 같지 않은 경우에만 조회수 증가
			if(!board.getBoardWriter().equals(userId)) {
				
				// 쿠키
				Cookie cookie = null;
				
				Cookie[] cArr = req.getCookies(); // 사용자의 쿠기정보 얻어오기
				
				if(cArr != null && cArr.length > 0) {
					for( Cookie c : cArr) {
						if(c.getName().equals("readBoardNo")) {
							cookie = c;
							break;
						}
					}
				}
				
				int result = 0;
				if(cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없는 케이스
					// 쿠키 생성
					cookie = new Cookie("readBoardNo", boardNo+""); // 게시글작성자와 현재 세션에 저장된 작성자 정보가 일치하지않고 ,쿠키도 없다.
					// 조회수 증가 서비스 호출
					result = boardService.increaseCount(boardNo);
				} else { // 존재했던 케이스 
					// 쿠키에 저장된 값중에 현재 조회된 게시글 번호(boardNo)를 추가
					// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께
					
					String[] arr = cookie.getValue().split("/");
					// "readBoardNo" : "1/2/5/10/135..." => ["1","2","5","10","135"...]
					
					// 컬렉션으로 변환 => indexOf를 쓰기위해
					// list.indexOf(obj) : 리스트안에서 매개변수로 들어온 obj와 일치(equals)하는 부분의 인덱스를 반환
					//					   일치하는 값이 없는경우 -1을 반환
					List<String> list = Arrays.asList(arr);
					if(list.indexOf(boardNo+"") == -1) {
						cookie.setValue(cookie.getValue()+"/"+boardNo);
						result = boardService.increaseCount(boardNo);
					}
					
				}
				if (result > 0) { // 성공적으로 조회수 증가함
					board.setCount(board.getCount() + 1);
					
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간 유지
					res.addCookie(cookie);
				} 
			}
		}else {
			model.addAttribute("errorMsg","게시글 조회 실패...");
			url = "common/errorPage";
		}
		
		return url;
	}
	
	@ResponseBody // 리턴값이 뷰페이지가아니라 값자체를 의미
	@GetMapping("/insertReply")
	public String insertReply(String rContent, HttpSession session,int boardNo) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		Reply r = new Reply();
		int  result=0;
		if(loginUser != null) {
			r.setReplyWriter(loginUser.getUserNo()+"");
			r.setReplyContent(rContent);
			r.setRefBno(boardNo);
			result = boardService.insertReply(r);
		}
	
		return result+"";
	}
	
	@ResponseBody
	@GetMapping("/selectReplyList")
	public List<Reply> selectReplyList(int bno){
		
		// 댓글 목록 조회
		return boardService.selectReplyList(bno);
		
	}
	
}


