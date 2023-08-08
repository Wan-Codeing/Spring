package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardDao boardDao;
	
	@Override
	public List<Board> selectList(int currentPage, Map<String,Object> paramMap){
		return boardDao.selectList(currentPage, paramMap);
	}
	
	@Override
	public int selectListCount(Map<String,Object> paramMap) {
		return boardDao.selectListCount(paramMap);
	}
	
	/*
	 * 게시글 + 첨부파일이 함께 삽입 => 이중 하나라도 에러가 발생해서 전체 롤백처리를 해줘야함
	 * @Transactional 선언적 트랜잭션 처리방법
	 * rollbackFor => rollback처리를 수행하기위한 에러의 종류
	 * 즉, rollbackFor에 속성값으로 추가된 예외가 발생할 경우 rollback처리를 시켜준다
	 */
	@Transactional(rollbackFor = {Exception.class}) // 어떤종류의 예외가 발생했든 발생했다면 무조건 rollback시키겠다
	@Override
	public int insertBoard (Board b,List<Attachment> attachList,String severPath,String webPath) throws Exception {
		
		// 1) 게시글 삽입
		// 게시글 등록 후 해당 게시글의 pk 값을 반환받기
		// 게시글 삽입시 게시글의 제목과, 본문에 들어가는 문자열에 크로스 사이트 스크립트 공격을 방지하기위한 메소드 추가
		// 텍스트 에어리어 태그에 엔터, 스페이스바 
		b.setBoardTitle(Utils.XSSHandleing(b.getBoardTitle()));
		b.setBoardContent(Utils.XSSHandleing(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		
		int boardNo = boardDao.insertBoard(b); // 반환값은 처리된 행의 갯수가 아닌 pk값을 반환받음
												// 제대로 삽입이 안될경우 0을 반환할 예정
		
		// 2) 첨부파일 등록 -> list가 비어있찌 않은 경우 실행
//		int result = 1;
//		if(boardNo > 0 && !attachList.isEmpty()) {
//			for( Attachment attach : attachList ) {
//				attach.setRefBno(boardNo);
//				attach.setFilePath(webPath);
//				result = insertAttachment(attach);
//			}
//		}
		int result = 0;
		if(boardNo > 0 && !attachList.isEmpty()){
			for( Attachment attach : attachList ) {
				attach.setRefBno(boardNo);
				attach.setFilePath(webPath);
			}
		}
		result = boardDao.insertAttachmentList(attachList);
		
		if(result != attachList.size()) {//이비지삽입 실패시 강제 예외 발생
			throw new Exception("예외발생");
		}
		return result;
	}
	
	public int insertAttachment(Attachment attach) {
		return boardDao.insertAttachment(attach);
	}
	
	@Override
	public BoardExt selectBoard(int boardNo) {
		return boardDao.selectBoard(boardNo);
	}
	
	@Override
	public int increaseCount(int bno) {
		return boardDao.increaseCount(bno);
	}
	
	@Override
	public int insertReply(Reply r) {
		return boardDao.insertReply(r);
	}
	
	@Override
	public List<Reply> selectReplyList(int bno){
		return boardDao.selectReplyList(bno);
	}
}
