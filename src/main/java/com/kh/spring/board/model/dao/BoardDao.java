package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;

@Repository
public class BoardDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public List<BoardType> selectBoardTypeList(){
		return sqlSession.selectList("boardMapper.selectBoardTypeList");
	}
	
	public List<Board> selectList(int currentPage, Map<String , Object> paramMap){
		int offset = (currentPage -1) * 5;
		int limit  = 5;
		
		RowBounds rowBounds = new RowBounds(offset,limit);
		
		return sqlSession.selectList("boardMapper.selectList" , paramMap , rowBounds);
	}
	
	public int selectListCount(Map<String , Object> paramMap) {
		return sqlSession.selectOne("boardMapper.selectListCount", paramMap);
	}
	
	public int insertBoard(Board b) {
		int result = 0;
		
		result = sqlSession.insert("boardMapper.insertBoard" , b);
		
		if(result > 0) {
			result = b.getBoardNo();
			// 게시글 삽입 성공시 selectKey태그를 사용하여 셋팅한 boardNo값을 b에 담아줌.
		}
		
		return result;
	}
	
	public int insertAttachment(Attachment attach) {
		return sqlSession.insert("boardMapper.insertAttachment", attach);
	}
	
	public int insertAttachmentList(List<Attachment> list) {
		return sqlSession.insert("boardMapper.insertAttachmentList", list);
	}
	
	
	public BoardExt selectBoard(int boardNo) {
		return sqlSession.selectOne("boardMapper.selectBoard", boardNo);
	}
	
	public int increaseCount(int bno) {
		return sqlSession.update("boardMapper.increaseCount",bno);
	}
	
	public int insertReply(Reply r) {
		return sqlSession.insert("boardMapper.insertReply" , r);
	}
	
	public List<Reply> selectReplyList(int bno){
		return sqlSession.selectList("boardMapper.selectReplyList" , bno);
	}
	
	public Attachment selectAttachment(int fileNo) {
		return sqlSession.selectOne("boardMapper.selectAttachment", fileNo);
	}
	
	public int updateBoard(Board b) {
		return sqlSession.update("boardMapper.updateBoard" , b);
	}
	
	public int deleteAttachment(Map<String, Object> map) {
		return sqlSession.delete("boardMapper.deleteAttachment" , map);
	}
	
	public int updateAttachment(Attachment at) {
		return sqlSession.update("boardMapper.updateAttachment", at);
	}
	
	public List<String> selectFileList(){
		return sqlSession.selectList("boardMapper.selectFileList");
	}
	
	
}
