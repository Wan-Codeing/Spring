package com.kh.spring.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
// Controller 타입의 어노테이션을 붙여주면 빈 스캐너가 자동으로 빈으로 등록해줌.(servlet-context.xml안에 있는
// <context:component-scan>태그)
//@RequestMapping("/member") -> 공통주소(클래스레벨에 선언)
//localhost:8081/spring/member(공통주소)/login.me(그외주소, 메소드레벨에 선언)
// 단, 클래스레벨에 @RequestMapping이 존재하지 않는경우 메서드레벨에서 단독으로 요청을 처리한다.
@SessionAttributes({"loginUser","nextUrl"})
// Model에 추가된 값의 key와 일치하는 값이 있으면 해당값을 session scope로 이동시킨다.
public class MemberController extends QuartzJobBean{
	
	
	/*
	 * 기존객체 생성 방식
	 * private MemberService mService = new MemberService();
	 * 
	 * 서비스가 동시에 많은 횟수가 요청이되면 그만큼 많은 객체가 생성된다.
	 * 
	 * Spring의 DI(Dependency Injection)-> 객체를 스프링에서 직접 생성해서 주입해주는 개념
	 * 
	 * new 연산자를 쓰지 않고 선언만 한 후 @Autowired어노테이션을 붙이면 객체를 주입받을수 있다.
	 *  */
	
	//@Autowired
	private MemberService mService;
	
	private MemberValidator memValidator;
	
	private BCryptPasswordEncoder bcrypotPasswordEncoder;
	/*
	 * 필드주입방식의 장점 : 이해하기편함. 사용하기도 편함.
	 * 
	 * 필드주입방식의 단점 : 순환 의존성 문제가 발생할 수 있다
	 *                  무분별할 주입시 의존관계 확인이 어렵다
	 *                  final 예약어를 지정할수가 없다.
	 *  */
	
	// 생성자 주입방식
	public MemberController() {
		
	}
	
	@Autowired
	public MemberController(MemberService mService , MemberValidator memValidator , BCryptPasswordEncoder bcrypotPasswordEncoder) {
		this.mService = mService;
		this.memValidator = memValidator;
		this.bcrypotPasswordEncoder = bcrypotPasswordEncoder;
	}
	
	/*
	 * 의존성 주입시 권장하는 방식
	 * 생성자에 참조할 클래스를 인자로 받아서 필드에 매핑시킴
	 * 
	 * 장점 : 현재클래스에서 내가 주입시킬 객체들을 모아서 관리할수 있기 때문에 한눈에 알아보기 편함
	 *       코드분석과 테스트에 유리하며, final로 필드값을 받을수 있어서 안전하다 
	 *  */
	
	/*
	 * 그외 방식
	 * Setter 주입방식 : setter메서드로 빈을 주입받는 방식 
	 * 생성자에 너무 많은 의존성을 주입하게 되면 알아보기 힘들다는 단점이 있어서 보완하기 위해 사용하거나,
	 * 혹은 의존성이 항상 필요한 경우가아니라 선택사항이라면 사용함.
	 *  */
	
	@Autowired
	public void setMemberService(MemberService mService) {
		this.mService = mService;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(memValidator);
	}
	
	//@RequestMapping( value = "login.me" , method = RequestMethod.POST) 
	// RequestMapping이라는 어노테이션을 붙이면 HandlerMapping곳에 등록이 됨
	//()안에 여러개의 속성을 추가 할수 있음.
	
	/* 
	 * 스프링에서 parameter(요청시 전달 값)을 받는 방법
	 * 1. HttpServletRequest request를 이용해서 전달받기(기존방식 그대로)
	 * 해당 메소드의 매개변수로 HttpServletRequset를 작성해 놓으면 스프링 컨테이너가 해당 메소드를 호출할때
	 * 자동으로 request객체를 생성해서 매개변수로 주입해준다.
	 * */
//	public String loginMember(HttpServletRequest request) {
//		String userId = request.getParameter("userId");
//		String userPwd = request.getParameter("userPwd");
//		
//		System.out.println("userId : "+userId);
//		System.out.println("userPwd: "+userPwd);
//		
//		return "main";
//	}
	/* 
	 * 2. @RequestParam어노테이션을 이용하는 방법.
	 *    기존의 request.getParameter("키")로 뽑는 역할을 대신 수행해주는 어노테이션.
	 *    input 속성의 value로 jsp에서 작성했던 name값을 입력해주면 알아서 매개변수로 값을 담아온다.
	 *    만약 넘어온값이 비어있다면 defaultValue로 설정 가능.
	 * */
//	@RequestMapping( value = "login.me" , method = RequestMethod.POST)
//	public String loginMember(
//						@RequestParam(value="userId" , defaultValue="m") String userId,
//						@RequestParam(value="userPwd") String userPwd
//			) {
//		System.out.println("userId : "+userId);
//		System.out.println("userPwd: "+userPwd);
//		
//		return "main";
//	}
	/* 
	 * 3. @RequestParam어노테이션을 생략하는 방법.
	 * 단, 매개변수의 변수명을 jsp에서 전달한 파라미터의 name속성값과 일치시켜줘야한다.
	 * + defaultValue사용 불가
	 * */
//	@RequestMapping( value = "login.me" , method = RequestMethod.POST)
//	public String loginMember(
//						String userId,
//						String userPwd
//			) {
//		System.out.println("userId : "+userId);
//		System.out.println("userPwd: "+userPwd);
//		
//		return "main";
//	}
	/* 
	 * 4. 커맨드 객체 방식
	 * 해당 메소드의 매개변수로 요청시 전달값을 담고자하는 VO클래스타입의 변수를 세팅하고, 요청시 전달값의 name속성값이 VO클래스의
	 * 담고자하는 필드명과 일치시켜서 작성
	 * 
	 * 스프링컨테이너에서 해당객체를 "기본 생성자"로 호출해서 생성 후 , 내부적으로 전닫받은 key값에 해당하는 setter메서드를 찾아서
	 * 전달한값을 필드에 담아준다. 따라서 반드시 name속성값(키값)과 vo객체의 필드명이 일치해야한다.
	 * */
//	@RequestMapping( value = "login.me" , method = RequestMethod.POST)
//	public String loginMember(
//			/* @ModelAttribute */
//			Member m
//			) {
//		System.out.println("userId : "+m.getUserId());
//		System.out.println("userPwd: "+m.getUserPwd());
//		
//		return "main";
//	}
//	@RequestMapping( value = "login.me" , method = RequestMethod.POST)
//	public ModelAndView loginMember(
//			@ModelAttribute Member m , HttpSession session , Model model, ModelAndView mv
//			) {
//		/* 
//		 * 	요청 처리 후 "응답 데이터를 담고" 응답페이지로 url재요청 하는 방법.
//		 *  1) Model 객체 이용
//		 *  포워딩할 응답뷰로 전달하고자하는 데이터를 맵형식으로 담을수 있는 객체 (Model객체는 requestScope를 가지고 있음)
//		 *  -> request, session을 대신하는 객체
//		 *  
//		 *  - 기본 scope : request이고, session scope로 변환하고싶은 경우 클래스 위에 @SessionAttribute를 작성하면 된다.
//		 *  model안에 데이터를 추가하는 함수 : addAttribute()
//		 *  
//		 *  2) ModelAndView 객체 이용
//		 *     ModelAndView에서 Model은 데이터를 담을수 있는 key-value형태의 객체(위 Model과 동일)
//		 *     View는 이동하고자하는 페이지에 대한 정보를 담고있는 객체합쳐서 ModelAndView
//		 *     mv에 model에 데이터를 추가하는 함수
//		 *     mv.addObject(key,value)
//		 *     mv에 view에 데이터를 추가하는 함수
//		 *     mv.setViewName("이동할페이지")
//		 * 
//		 * 
//		 * -> Model로 데이터를 전달하든, ModelAndView로 데이터를 전달하든 결국은 ModelAndView로 통합되서
//		 *    Spring container에 의해 관리된다.
//		 * */
//		model.addAttribute("errorMsg","오류발생");
//		
//		mv.addObject("errorMsg", "modelAndView 테스트");
//		mv.setViewName("common/errorPage");
//		
//		
//		System.out.println("userId : "+m.getUserId());
//		System.out.println("userPwd: "+m.getUserPwd());
//		
//		return mv;
//	}
	
	@PostMapping("/login.me")
	public String loginMember(
							@ModelAttribute Member m , 
							HttpSession session , 
							Model model,
							@SessionAttribute(required = false) String nextUrl
			) {
		log.info("찍어보자구 {} , {}",m , m);
		//암호화 전 로그인 요청처리
//		Member loginUser = mService.loginUser(m);
//		String url = "";
//		if(loginUser == null) {
//			model.addAttribute("errorMsg","오류발생");
//			url = "common/errorPage";
//		}else {
//			session.setAttribute("loginUser", loginUser);
//			url = "redirect:/";
//		}
		
		//암호화 후 로그인 요청 처리
		Member loginUser = mService.selectOne(m.getUserId());
		// loginUser의 userPwd는 암호화된 상태의 비밀번호
		// m안의 userPwd는 암호화전 상태의 비밀번호
		
		// BcrpytPasswordEncoder객체의 matches 메소드 사용
		// matches(평문, 암호문)을 작성하면 내부적으로 두 값이 일치하는 검사 후 일치하면 true/ 일치하지 않으면 false
		
		String url = "";
		if(loginUser != null && bcrypotPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) {
			//로그인 성공
			if(loginUser.getChangePwd().equals("Y")) {
				session.setAttribute("alertMsg","비밀번호를 변경해주세요");
			}
			model.addAttribute("loginUser",loginUser);
			url = "redirect:"+(nextUrl != null ? nextUrl : "/");
			
			// 사용한 nextUrl제거
			model.addAttribute("nextUrl",null);
		}else {
			model.addAttribute("errorMsg","아이디 또는 비밀번호가 일치하지 않습니다.");
			url = "common/errorPage";
		}
		
		return url;
	}
	
	@GetMapping("/logout.me")
	public String logoutMember(HttpSession session , SessionStatus status ) {
		
		
		//@SessionAttributes로 session scope에 이관된 데이터는 sessionstatus를 이용해서 객체를 없애야한다.
		session.invalidate();
		status.setComplete();
		
		return "redirect:/";
	}
	
	@GetMapping("/insert.me")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	@PostMapping("/insert.me")
	public String insertMember(@Validated Member m , HttpSession session, Model model ,BindingResult bindingResult) {
		// 멤버테이블에 회원가입등록 성공시 -> alertMsg변수에 회원가입 성공 메세지 담아서 main페이지로 url재요청 보내기
		//                     실패시 -> errorMsg변수에 실패매세지 담아서 , 에러페이지로 forwarding하기
		/* 
		 *  비밀번호가 사용자가 입력한 그대로이기때문에 보안에 문제가 있다.
		 *  -> BCrypt방식의 암호화를 통해서 pwd를 암호문으로 변경.
		 *  1) spring security모듈에서 제공하는 라이브러리를 pom.xml다운
		 *  2) BCrpytPasswordEncoder클래스를 xml파일에서 bean객체로 등록
		 *  3) web.xml에 2번에서 생성한 xml파일을 로딩할수 있도록 param-value에 추가.
		 */
		System.out.println("암호화 전 비밀번호 : "+m.getUserPwd());
		
		//암호화 작업
		String encPwd = bcrypotPasswordEncoder.encode(m.getUserPwd());
		
		//암호화된 pwd를 Member m에 담아주기
		m.setUserPwd(encPwd);
		
		System.out.println("암호화 후 비밀번호 : "+m.getUserPwd());
		
		//1. memberService호출해서 insertMember 실행(Insert)
		int result = mService.insertMember(m);
		String url = "";
		
		if(result > 0) {
			//성공시
			session.setAttribute("alertMsg", "회원가입성공");
			url = "redirect:/";
		}else {
			//실패 - 에러페이지로
			model.addAttribute("errorMsg","회원가입실패");
			url = "common/errorPage";
		}
		
		return url;
	}
	
	
	@GetMapping("/myPage.me")
	public String myPage() {
		return "member/myPage";
	}
	
	// 내정보수정 성공시-> myPage로 url재요청
	// 실패시 -> 에러페이지로
	@PostMapping("/update.me")
	public String updateMember(Member m , Model model, HttpSession session , RedirectAttributes ra ) {
		
		int result = mService.updateMember(m);
		
		if(result > 0) {
			
			// 업데이트성공시 db에 등록된 회원정보 다시 불러오기
			Member updateMember = mService.loginUser(m);
			session.setAttribute("loginUser",updateMember);
			ra.addFlashAttribute("alertMsg", "내정보수정 성공");
			// 1차적으로 alertMsg sessionScope로 이관
			// 리다이렉트 완료 후 sessionScope에 저장된 alertMsg를 requestScope로 다시 이관
			
			//session.setAttribute("alertMsg", "정보수정 성공");
			
			//return "member/myPage"; // forward -> 그값을 그대로 유지한채로 해당페이지 요청
			
			return "redirect:/myPage.me"; // redirect -> 새로고침을 실행 url요청
		}else {
			model.addAttribute("errorMsg","회원 정보 수정 실패");
			return "common/errorPage";
		}
		
		
	}
	
	
	
	/*
	 *  스프링 예외처리 방법(3가지, 중복으로 사용 가능!)
	 *  
	 *  1. 메서드별로 예외처리(try-catch  / throws) -> 1순위로 적용됨
	 *  
	 *  2. 하나의 컨트롤러에서 발생하는 예외를 모아서 처리하는 방법 => @ExceptionHandler(메서드에 작성) -> 2순위
	 *  
	 *  3. 전역에서 발생하는 예외를 모아서 처리하는 클래스 -> @ControllerAdvice(클래스에 작성) -> 3순위
	 *  
	 *  */
	
//	@ExceptionHandler(Exception.class)
//	public String exceptionHandler(Exception e, Model model) {
//		e.printStackTrace();
//		
//		model.addAttribute("errorMsg","서비스 이용중 문제가 발생했습니다.");
//		
//		return "common/errorPage";
//	}
	
	
	//아이디 중복검사--> 비동기요청
	@ResponseBody// 비동기 요청시 사용.
	@GetMapping("/idCheck.me")
	public String idCheck(String userId) {
		
		int result = mService.idCheck(userId);
		
		/*
		 * 컨트롤러에서 반환되는 값은 forward 또는 redirect를 위한 경로인 경우가 일반적임. 즉, 반환되는 값은
		 * 경로로써 인식함.
		 *  
		 * 이를 해결하기위한 어노테이션이 @ResponseBody.  
		 *  -> 반환되는 값을 응답(response)의 몸통(body)에 추가하여 이전 요청주소로 돌아감
		 *  => 컨트롤러에서 반환되는 값이 경로가 아닌 "값 자체"로 인식됨.
		 *  
		 *  */
		
		return result+"";
	}
	/*
	 * Spring방식 ajax요청 처리 방법
	 * * jsonView 빈을 통해 데이터를 처리하기
	 *  */
	@PostMapping("/selectOne")
	public String selectOne(String userId ,Model model) {
		// 1. 업무로직
		Member m = mService.selectOne(userId);
		
		if(m != null) {
			model.addAttribute("userId", m.getUserId());
			model.addAttribute("userName", m.getUserName());
		}
		
		// model객체 안에 담긴 데이터를 json으로 변환후 응답처리해줌.
		// 내가 요청한 jsonView가 jsp파일이 아닌 실제 bean이름으로 매핑을 시켜주는 BeanNameViewResolver를 추가해줘야함.
		return "jsonView";
	}
	
	@ResponseBody
	@PostMapping("/selectOne2")
	public Map<String, Object> selectOne2(String userId) {
		// 1. 업무로직
		Member m = mService.selectOne(userId);
		Map<String , Object> map = new HashMap();
		
		if(m != null) {
			map.put("userId", m.getUserId());
			map.put("userName", m.getUserName());
		}
		
		return map;
	}
		
	@PostMapping("/selectOne3")
	public ResponseEntity<Map<String,Object>> selectOne3(String userId) {
		// 1. 업무로직
		Member m = mService.selectOne(userId);
		Map<String , Object> map = new HashMap();
		
		ResponseEntity res = null;
		
		if(m != null) {
			map.put("userId", m.getUserId());
			map.put("userName", m.getUserName());

			res = ResponseEntity
		             .ok()
		             .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
		             .body(map);
		}else {
			res = ResponseEntity
		             .notFound()
		             .build();
		}
		
		return res;
	}
	
	
	
	//@Scheduled(fixedDelay = 1000)// 고정방식
	public void test() {
		log.info("1초마다 출력");
	}
	
	public void testCron() {
		log.info("크론탭 방식 테스트 ");
	}
	
	public void testQuartz() {
		log.info("콰츠 테스트");
	}
	
	/*
	 * 회원정보 확인 스케쥴러
	 * 매일 오전 1시에 모든 사용자의 정보를 검색하여 사용자가 비밀번호를 안바꾼지 3개월이 지났다면 , changePwd 이 칼럼에 값을 
	 * Y로 변경.
	 * 
	 * 사용자가 로그인했을때 changePwd값이 Y라면 비밀번호 변경페이지로 이동 
	 *  */
	
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// JobDataAsMap으로 등록한 bean객체 가져옴
		MemberService mService = (MemberService) context.getMergedJobDataMap().get("mService");
		
		mService.updateMemberChangePwd();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
