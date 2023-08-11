package com.kh.spring.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;

@Repository
public class MemberDao {
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public Member loginUser(Member m) {
		return sqlSession.selectOne("memberMapper.loginUser", m);
	}

	public int insertMember(Member m) {
		return sqlSession.insert("memberMapper.insertMember", m);		
	}
	
	public int idCheck(String userId) {
		return sqlSession.selectOne("memberMapper.idCheck", userId);
	}

	public int updateMember(Member m) {
		return sqlSession.update("memberMapper.updateMember" , m);
	}
	
	public Member selectOne(String userId) {
		return sqlSession.selectOne("memberMapper.selectOne", userId);
	}
	
	public void updateMemberChangePwd() {
		sqlSession.update("memberMapper.updateMemberChangePwd");
	}
	
	
	
	
}
