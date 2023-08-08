package com.kh.spring.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * lombok
 * - 자동 코드 생성 라이브러리
 * - 반복괴는 getter/setter/toString 등 메소드 작성 코드를 줄여주는 역할의 코드 라이브러리
 * 
 * lombok
 * 1. 라이브러리 다운후 적용(pom.xml)
 * 2. 다운로드된 jar파일 설치 ** ide가 꺼져있는 상태에서 설치할것 **s
 * 3. ide 재실행
 * 
 * lombok 사용시 주의사항
 * - uName, bTitle과 같이 앞글자가 소문자 외자인 필드명은 만들면 안됨
 * - 필드명 작성시 소문자 두굴자 이상으로 시작해야함 ★★
 * - el표기법 사용시 내부적으로 getter메소드를 찾게되는데 이떄 getuName(), getbTitle이라는 이름으로 메소드를 호출하기 때문
 * - 기존방식이라면 getUName()으로 작성될것이기 때문에 호출될수 없다.
 */
//@Setter
//@Getter
//@ToString
@NoArgsConstructor//기본생성자
@AllArgsConstructor//모든필드를 매개변수로 갖는 생성자
@Builder
//@EqualsAndHashCode
@Data
public class Member {

	private int userNo;
	private String userId;
	private String userPwd;
	private String userName;
	private String email;
	private String phone;
	private String address;
	private String Interest;
	private Date enrollDate;
	private Date modifyDate;
	private String status;
	private String gender;
	private String birthday;
	private boolean enable;
	
}
