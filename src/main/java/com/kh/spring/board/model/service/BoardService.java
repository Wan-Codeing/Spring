package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.Reply;

public interface BoardService {
	
	public List<Board> selectList(int currentPage, Map<String, Object> paramMap);
	
	public int selectListCount(Map<String, Object> paramMap);
	
	public int insertBoard(Board b, List<Attachment> list, String serverPath, String webPath) throws Exception;
	
	public BoardExt selectBoard(int boardNo);
	
	public int increaseCount(int bno);
	
	public int insertReply(Reply r);
	
	public List<Reply> selectReplyList(int bno);
	
	public Attachment selectAttachment(int fileNo);
	
	public int updateBoard(Board b, List<MultipartFile> list, String serverPath, String WebPath , String deleteList) throws Exception;
	
	public List<String> selectFileList();
	
	
	
	
}
