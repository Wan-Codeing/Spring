package com.kh.spring.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private MemberDao memberDao;
	
	@Override
	public Member loginUser(Member m) {
		/*
		 * SqlSessionTemplate 객체를 bean으로 등록한 후에는 
		 * 스프링컨테이너가 자원 사용후 자동으로 반납을 해주기 때문에 close()할 필요가 없다.
		 * */
		return memberDao.loginUser(m);
	}
	
	@Override
	public int insertMember(Member m) {
		return memberDao.insertMember(m);
	}
	
	@Override
	public int idCheck(String userId) {
		return memberDao.idCheck(userId);
	}
	
	@Override
	public int updateMember(Member m) {
		return memberDao.updateMember(m);
	}
	 
	@Override
	public Member selectOne(String userId) {
		return memberDao.selectOne(userId);
	}

	@Override
	public void updateMemberChangePwd() {
		memberDao.updateMemberChangePwd();
	}
	
	
	
	
	
	
	
}
