package com.kh.spring.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@Controller
@RequestMapping("/board") // 공통주소
// 현재 컨트롤러 호출시 /spring/board의 경로로 들어오는 모든 url요청을 받아준다.
@SessionAttributes({ "loginUser" })
public class BoardController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private ServletContext application;

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping("/list/{boardCode}")
	public String selectList(@PathVariable("boardCode") String boardCode,
			// @PathVariable("key") : URL경로에 포함되어있는 값을 변수로 사용할수 있게 해주는 어노테이션
			// + 자동으로 requestScope에 저장이 된다..
			@RequestParam(value = "currentPage", defaultValue = "1") int currentPage, Model model,
			// 검색요청이 들어오는경우 paramMap내부에는 keyword, condition
			@RequestParam Map<String, Object> paramMap) {
		// 검색 기능 추가
		// 페이징바
		// 게시글 목록
		log.info("paramMap = {}", paramMap);
		paramMap.put("boardCode", boardCode);
		List<Board> list = boardService.selectList(currentPage, paramMap);

		// 총 게시글 갯수
		int total = boardService.selectListCount(paramMap);
		int pageLimit = 10;
		int boardLimit = 5;
		PageInfo pi = Pagination.getPageInfo(total, currentPage, pageLimit, boardLimit);

		model.addAttribute("param", paramMap);
		model.addAttribute("list", list);
		model.addAttribute("pi", pi);

		return "board/boardListView";
	}

	@GetMapping("/insert/{boardCode}")
	public String enrollBoard(@PathVariable("boardCode") String boardCode, Model model) {

		return "board/boardEnrollForm";
	}

	@GetMapping("/update/{boardCode}/{boardNo}")
	public String updateFormBoard(@PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo,
			Model model) {
		BoardExt board = boardService.selectBoard(boardNo);
		board.setBoardContent(Utils.newLineClear(board.getBoardContent()));

		model.addAttribute("board", board);

		return "board/boardUpdateForm";
	}

	@PostMapping("/insert/{boardCode}")
	public String insertBoard(Board b, @RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
			@PathVariable("boardCode") String boardCode, HttpSession session, Model model,
			@ModelAttribute("loginUser") Member loginUser) {

		// 이미지, 파일을 저장할 저장경로 얻어오기
		// /resources/images/board/{boardCode}/
		String webPath = "/resources/images/board/" + boardCode + "/";
		String severFolderPath = application.getRealPath(webPath);

		// Board 객체에 데이터 추가(boardCode , boardWriter)
		b.setBoardWriter(loginUser.getUserNo() + "");
		b.setBoardCd(boardCode);

		log.info("board {}", b);

		// 디렉토리생성 , 해당디렉토리가 존재하지 않는다면 생성
		File dir = new File(severFolderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 첨부파일같은 경우 선택하고 안하고 상관없이 객체는 생성이 된다 단, 길이가 0일수가 있음.
		// 전달된 파일이 있는경우 해당파일을 웹서버에 저장하고, Attachment테이블에 해당정보를 등록.
		// 없는경우 위프로세스를 패스할것.

		List<Attachment> attachList = new ArrayList();
		
		int level = -1;
		for (MultipartFile upfile : upfiles) {
			// input[name=upFile]로 만들어두면 비어있는 file이 넘어올수 있음.
			level++;
			if (upfile.isEmpty())
				continue;

			// 1. 파일명 재정의 해주는 함수.
			String changeName = Utils.saveFile(upfile, severFolderPath);
			Attachment at = Attachment.
							builder().
							changeName(changeName).
							originName(upfile.getOriginalFilename()).
							fileLevel(level).build();
			attachList.add(at);
		}

		int result = 0;

		try {
			result = boardService.insertBoard(b, attachList, severFolderPath, webPath);
		} catch (Exception e) {
			log.error("error = {}", e.getMessage());
			// e.printStackTrace();
		}

		if (result > 0) {
			session.setAttribute("alertMsg", "게시글 작성에 성공하셨습니다.");
			return "redirect:/board/list/" + boardCode;
		} else {
			model.addAttribute("errorMsg", "게시글 작성 실패");
			return "common/errorPage";
		}
	}

	@PostMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard(Board b, @RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
			@PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo, HttpSession session,
			Model model , @RequestParam(value="deleteList" , required = false) String deleteList
			) {
		// 이미지, 파일을 저장할 저장경로 얻어오기
		// /resources/images/board/{boardCode}/
		String webPath = "/resources/images/board/" + boardCode + "/";
		String severFolderPath = application.getRealPath(webPath);

		// Board 객체에 데이터 추가(boardCode , boardWriter , boardNo)
		Member loginUser = (Member) session.getAttribute("loginUser");
		b.setBoardWriter(loginUser.getUserNo() + "");
		b.setBoardCd(boardCode);
		b.setBoardNo(boardNo);
		
		log.info("board ================== {}", b);
		log.info("deleteList =============== {}" , deleteList);
		int result = 0;

		try {
			result = boardService.updateBoard(b, upfiles, severFolderPath, webPath, deleteList);
		} catch (Exception e) {
			log.error("error = {}", e.getMessage());
		}

		if (result > 0) {
			session.setAttribute("alertMsg", "게시글 수정에 성공하셨습니다.");
			return "redirect:/board/detail/" + boardCode+"/"+boardNo;
		} else {
			model.addAttribute("errorMsg", "게시글 수정 실패");
			return "common/errorPage";
		}
	}

	@GetMapping("/detail/{boardCode}/{boardNo}")
	public String selectBoard(@PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo,
			HttpSession session, Model model, HttpServletRequest req, HttpServletResponse res
	/* @ModelAttribute("loginUser") Member loginUser */
	) {

		// 게시판 정보 조회
//		Board b = service.조회;
//		List<Attachment> list = service.조회 두 과저을 합쳐서 한번에 조회
		BoardExt board = boardService.selectBoard(boardNo);
		String url = "";

		model.addAttribute("board", board);
		url = "board/boardDetailView";

		// 상세조회 성공시 쿠키를 이용해서 조회수가 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수 증가되지 않게 설정
		if (board != null) {

			// int userNo = 0;
			String userId = "";

			Member loginUser = (Member) session.getAttribute("loginUser");

			if (loginUser != null) {
				userId = loginUser.getUserId();
			}

			// 게시글의 작성자 아이디와, 현재 세션의 접속중인 아이디가 같지 않은 경우에만 조회수증가
			if (!board.getBoardWriter().equals(userId)) {

				// 쿠키
				Cookie cookie = null;

				Cookie[] cArr = req.getCookies(); // 사용자의 쿠키정보 얻어오기.

				if (cArr != null && cArr.length > 0) {

					for (Cookie c : cArr) {
						if (c.getName().equals("readBoardNo")) {
							cookie = c;
							break;
						}
					}
				}

				int result = 0;

				if (cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없는 케이스
					// 쿠키 생성
					cookie = new Cookie("readBoardNo", boardNo + "");// 게시글작성자와 현재 세션에 저장된 작성자 정보가 일치하지않고, 쿠기도 없다.
					// 조회수 증가 서비스 호출
					result = boardService.increaseCount(boardNo);
				} else { // 존재 했던 케이스
					// 쿠키에 저장된 값중에 현재 조회된 게시글번호(boardNo)를 추가
					// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께

					String[] arr = cookie.getValue().split("/");
					// "reacBoardNo" : "1/2/5/10/135" ==> ["1","2","5","10","135"]

					// 배열을 컬렉션으로 변환 => indexOf를 사용하기 위해서
					// List.indexOf(obj) : list안에서 매개변수로 들어온 obj와 일치(equals)하는 부분의 인덱스를 반환
					// 일치하는 값이 없는경우 -1 반환
					List<String> list = Arrays.asList(arr);

					if (list.indexOf(boardNo + "") == -1) { // 기존 쿠키값에 현재 게시글 번호와 일치하는 값이 없는경우(처음들어온글)
						// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께
						cookie.setValue(cookie.getValue() + "/" + boardNo);
						result = boardService.increaseCount(boardNo);
					}
				}
				if (result > 0) { // 성공적으로 조회수 증가함 ^^
					board.setCount(board.getCount() + 1);

					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간만 유지
					res.addCookie(cookie);
				}
			}
		} else {
			model.addAttribute("errorMsg", "게시글조회 실패 ..");
			url = "common/errorPage";
		}

		return url;
	}

	@GetMapping("/insertReply")
	@ResponseBody // 리턴되는 값이 뷰페이지가 아니라 값 자체임을 의미
	public int insertReply(Reply r, HttpSession session) {

		Member m = (Member) session.getAttribute("loginUser");
		if (m != null) {
			r.setReplyWriter(m.getUserNo() + "");
		}

		int result = boardService.insertReply(r);

		return result;
	}

	@GetMapping("/selectReplyList")
	@ResponseBody
	public List<Reply> selectReplyList(int bno) {
		return boardService.selectReplyList(bno);
	}

	@GetMapping("/fileDownload/{fileNo}")
	public ResponseEntity<Resource> fileDownload(@PathVariable("fileNo") int fileNo)
			throws UnsupportedEncodingException {
		ResponseEntity<Resource> responseEntity = null;
		// 1. db에서 Attachment에서 fileNo 매개변수로 전달받은 fileNo와 일치하는 행 조회
		Attachment at = boardService.selectAttachment(fileNo);
		log.info("at === {} ", at);
		if (at == null) {
			return responseEntity.notFound().build();// stats code 404
		}

		// 2. Resource 객체 얻어오기.
		String saveDirectory = application.getRealPath(at.getFilePath());
		File downFile = new File(saveDirectory, at.getChangeName());
		Resource resource = resourceLoader.getResource("file:" + downFile);
		// getResource : 파일의 위치, 내용을 읽어올수 있음.
		String filename = new String(at.getOriginName().getBytes("utf-8"), "iso-8859-1");
		// 한글깨짐 방지처리(utf-8로 전달시 한글이 깨질수 있음)

		// 3. responseEntity객체 생성 및 리턴.
		responseEntity = ResponseEntity.ok()
				// 컨텐츠타입 : ex) html, jsonm ,xml.. 내가 넘겨주고자하는 데이터가 바이너리형식의 데이터임을 의미
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				// attachment -> 파일을 첨부파일형태로 처리하겠다(다운로드).
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename).body(resource);

		return responseEntity;
	}

}
