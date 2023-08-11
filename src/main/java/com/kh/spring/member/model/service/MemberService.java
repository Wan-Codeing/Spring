package com.kh.spring.member.model.service;

import com.kh.spring.member.model.vo.Member;

public interface MemberService {

	public Member loginUser(Member m);
	
	public int insertMember(Member m);
	
	public int idCheck(String userId);
	
	public int updateMember(Member m);
	
	public Member selectOne(String userId);


	public void updateMemberChangePwd();
	
	
	
	
	
	
	
	
	
	
	
	
	
}
