package com.kh.spring.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberDao memberDao;
	
	@Override
	public Member loginUser(Member m){
		// 자동반납 필요 없음, 알아서 반환해줌 by 스프링컨테이너
		Member loginUser = memberDao.loginUser(m);
		return loginUser;
	}
	
	@Override
	public int insertMember(Member m) {
		int result = memberDao.insertMember(m);
		return result;
	}
	
	@Override
	public int idCheck(String userId) {
		int result = memberDao.idCheck(userId);
		return result;
	}
	
	@Override
	public int updateMember(Member m) {
		int result = memberDao.updateMember(m);
		return result;
	}
	
	@Override
	public Member selectOne(String userId) {
		return memberDao.selectOne(userId);
	}
}
