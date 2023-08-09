package com.kh.spring.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
// Controller 타입의 어노테이션을붙여주면 빈 스캐너가 자동으로 빈으로 등록해줌(servlet-context.xml안에 있는 <context:component-scan>태그)
@SessionAttributes({"loginUser"})
// Model에 추가된 값의 key와 일치하는 값이 있으면 해당값을 session scope로 이동시킨다
public class MemberController {
	
	
	/*
	 *	기존객체 생성 방식
	 *	private MemberService mService = new MemberService(); 
	 *
	 *	서비스가 동시에 많은 횟수가 요청이되면 그만큼 많은 객체가 생성된다.
	 *
	 *	Spring의 DI(Dependency Injection) -> 객체를 스프링에서 직접 생성해서 주입해주는 개념
	 *
	 *	new 연산자를 쓰지 않고 선언만 한후 @Autowired어노테이션을 붙이면 객체를 주입 받을 수 있다
	 */
	
	@Autowired
	private MemberService mService;
	private MemberValidator memValidator;
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	/*
	 * 필드 주입 방식의 장점 : 이해하기 편함. 사용하기 편함.
	 * 
	 * 필드 주입 방식의 단점 : 순환 의존성 문제가 발생할 수 있다.
	 * 					 무분별한 주입시 의존가계 확인이 어렵다.
	 * 					 final 예약어를 지정할수가 없다.
	 * 
	 */
	
	// 생성자 주입 방식
	public MemberController() {
		
	}
	@Autowired
	public MemberController(MemberService mService, MemberValidator memValidator, BCryptPasswordEncoder bcryptPasswordEncoder) {
		this.mService = mService;
		this.memValidator = memValidator;
		this.bcryptPasswordEncoder = bcryptPasswordEncoder;
	}
	/*
	 * 의존성 주입시 권장하는 방식
	 * 생성자에 참조할 클래스를 인자로 받아서 필드에 매핑시킴
	 * 
	 * 장점 : 현재 클래스에서 내가 주입시킬 객체들을 모아서 관리할수 있기 때문에 한눈에 알아보기 편함
	 * 		 코드 분석과 테스트에 유리하며,  final로 필드값을 받을수 있어서 안전하다
	 */
	
	/*
	 * 그 외 방식
	 * Setter 주입방식 : setter메서드로 빈을 주입받는 방식
	 * 생성자에 너무 많은 의존성을 주입받게되면 알아보기 힘들다는 단점이 있어서 보완하기 위해 사용하거나,
	 * 혹은 의존성이 항상 필요한 경우가아니라 선택사항이라면 사용함.
	 */
	
	@Autowired
	public void setMemberSevice(MemberService memberService) {
		this.mService = memberService;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(memValidator);
	}
	
//	@RequestMapping(value="login.me" , method=RequestMethod.POST) // RequestMapping이라는 어노테이션ㅇ르 붙이면 HandlerMapping이라는곳에 등록이 됨
	//()안에 여러개의 속성을 추가 할 수 있음
	
	/*
	 * 스프링에서 parameter(요청시 전달 값)을 받는 방법
	 * 1. HttpServletRequest request를 이용해서 전달받기(기종방식 그대로)
	 * 해당 메소드의 매개변수로 HttpServletRequest를 작성해 놓으면 스프링 컨테이너가 메소드를 호출할떄 자동으로 request객체를 생성해서 매개변수로 주입해준다
	 */
//	public String loginMember(HttpServletRequest request) {
//		String userId = request.getParameter("userId");
//		String userPwd = request.getParameter("userPwd");
//		
//		System.out.println("userId : "+userId);
//		System.out.println("userPwd : "+userPwd);
//		
//		return "main";
//	}
	/*
	 * 2. @RequestParam어노테이션을 이용하는 방법
	 * 기존의 request.getParameter("키")로 뽑는 역할을 대신 수행해주는 어노테이션
	 * input 속성의 value로 jsp에서 작성했던 name값을 입력해주면 알아서 매개변수로 값을 담아온다.
	 * 만약 넘어 온값이 비어있따면 defaultValue로 설정가능
	 */
	/*
	 * @RequestMapping(value="login.me" , method=RequestMethod.POST) public String
	 * loginMember(
	 * 
	 * @RequestParam(value="userId", defaultValue="m")String userId,
	 * 
	 * @RequestParam(value="userPwd") String userPwd ) {
	 * System.out.println("userId : "+userId);
	 * System.out.println("userPwd : "+userPwd);
	 * 
	 * return "main"; }
	 */
	/*
	 * 3. @RequestParam어노테이션을 생략하는 방법
	 * 단, 매개변수의 변수명을 jsp에서 전달한 파라미터의 name속성값과 일치 시켜줘야한다.
	 */
//	@RequestMapping(value="login.me" , method=RequestMethod.POST)
//	public String loginMember(
//							String userId,
//							String userPwd
//							) {
//		System.out.println("userId : "+userId);
//		System.out.println("userPwd : "+userPwd);
//		
//		return "main";
//	}
	/*
	 * 4. 커맨드 객체 방식
	 * 해당 메소드의 매개변수로 요청시 전달값을 담고자하는 VO클래스타입의 변수를 셋팅하고, 요청시전달값의 name속성값이 VO클래스의 담고자하는 필드명과 일치시켜서 작성
	 * 
	 * 스프링컴테이너에서 해당객체를 "기본생성자"로 호출해서 생성 후, 내부적으로 전달받은 key값에 해당하는 setter메서드를 찾아서 전달한값을 필드에 담아둔다.
	 * 따라서 반드시 name속성(키값)과 VO객체의 필드명이 일치해야한다
	 */
	/*
	 * @RequestMapping(value="login.me" , method=RequestMethod.POST) public String
	 * loginMember( Member m ) { System.out.println("userId : "+m.getUserId());
	 * System.out.println("userPwd : "+m.getUsePwd());
	 * 
	 * return "main"; }
	 */
//	@RequestMapping(value="login.me" , method=RequestMethod.POST)
//	public ModelAndView loginMember(
//						@ModelAttribute Member m, HttpSession session, Model model, ModelAndView mv
//							) {
//		/*I
//		 * 요청 처리후 "응답 데이터를 담고" 응답페이지로 url 재요청 하는방법.
//		 * 1) Model객체 이용
//		 * 포워딩할 응답뷰로 전달하고하는 데이터를 맵형식으로 담을수 있는 객체 (Model 객체는 requestScoep를 가지고있다)
//		 * -> request , session을 대신하는 객체
//		 * 
//		 * 기본 scope : request이고, session scope로 변환하고싶은 경우 클래스 위에 @SessionAttribute를 작성하면 된다.
//		 * model 안에 데이터를 추가하는 함수 : addAttribute()
//		 * 
//		 * 2) ModelAndView 객체 이용
//		 * ModelAndView에서는 Model은 데이터를 담을수있는 key-value형태의 객체(위 Model과 동일)
//		 * View는 이동하고자하는 페이지에 대한 정보를 담고있는 객체합쳐서 ModelAndView
//		 */
//		model.addAttribute("erreoMsg","그게 되겠냐?");
//		
//		mv.addObject("errorMsg", "되겠냐고");
//		mv.setViewName("commom/errorPage.jsp");
//		
//		System.out.println("userId : "+m.getUserId());
//		System.out.println("userPwd : "+m.getUsePwd());
//		
//		return mv;
//	}
	
	@PostMapping("/login.me")
	public String loginMember(@ModelAttribute Member m, HttpSession session, Model model)
	{
		//암호화 전 로그인 요청처리
//		Member loginUser = mService.loginUser(m);
//		String url ="";
//		if(loginUser == null) {
//			model.addAttribute("errorMsg","그게되겠냐?");
//			url = "common/errorPage";
//		} else {
//			session.setAttribute("loginUser", loginUser);
//			url = "redirect:/";
//		}
		
		// 암호화 후 요청처리
		Member loginUser = mService.selectOne(m.getUserId());
		// loginUser의 userPwd는 암호화된 상태의 비밀번호
		// m안의 userPwd는 암호화전 상태의 비밀번호가 들어가있음
		
		// BcrpytPqsswordEncoder객체의 matches 메소드 사용
		// matches(평문, 암호문)을 작성하면 내부적으로 두값이 일치하는지 검사 => 일치하면 true, 아니면 false
		String url="";
		if(loginUser != null && bcryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())){
			// 로그인 성공
			model.addAttribute("loginUser",loginUser);
			url = "redirect:/";
		}else {
			// 로그인 실패
			model.addAttribute("errorMsg","아디 or 비번 틀렷대요~~ㅋㅋㅋ");
			url = "common/errorPage";
		}
		
		
		return url;
	}
	
	@GetMapping("/logout.me")
	public String logoutMember(HttpSession session, SessionStatus status) {
		session.invalidate();
		// @SessionAttributes로 session scope에 이관된 데이터는 sessionstatus 를 이용해서 객체 삭제
		status.setComplete();
		return "redirect:/";
	}
	
	@GetMapping("/insert.me")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	@PostMapping("/insert.me")
	public String insertMember(@Validated Member m, HttpSession session, Model model, BindingResult bindingResult ) {
//		if(bindingResult.hasErrors()) {
//			String errors = "";
//			List<ObjectError> errorList = bindingResult.getAllErrors();
//			for(ObjectError err : errorList) {
//				errors += "{"+err.getCode()+":"+err.getDefaultMessage()+"}";
//				model.addAttribute("errorMsg",errors);
//			}
//			return "common/errorPage";
//		}
		// 멤버테이블에 회원가입등록 성공시 -> alertMsg변수에 회원가입 성공메세지 담아서 main페이지로 url재요청 보내기
								//-> errorMsg변수에 실패메세지 담아서, 에러페이로 forwarding하기
		/*
		 * 비밀번호가 사용자가 입력한 그대로이기떄문에 보안에 문제가 있다.
		 * -> BCrpyt방식의 암호화를 통해서 pwd를 암호문으로 변경
		 * 1) spring security모듈에서 제공하는 라이브러리를 pom.xml다운
		 * 2) BCrpytPasswordEncoder클래스를 xml파일에서 bean객체로 등록
		 * 3) web.xml에 2번에서 생성한 xml파일을 로딩 할 수 있도록 param-value에 추가
		 */
		System.out.println("암호화 전 비밀번호 : "+m.getUserPwd());
		//암호화 작업
		String encPwd = bcryptPasswordEncoder.encode(m.getUserPwd());
		
		//암호화된 pwd를 Member m에 담아주기
		m.setUserPwd(encPwd);
		
		System.out.println("암호화 후 비밀번호 : "+m.getUserPwd());
		
		int result = mService.insertMember(m);
		String url="";
		if(result > 0) {
			session.setAttribute("alertMsg","Mission Complete!");
			url="redirect:/";
		}else {
			model.addAttribute("errorMsg","되겠냐고ㅋㅋ");
			url="common/errorPage";
		}
		return url;
	}
	
	@GetMapping("/myPage.me")
	public String myPage() {
		return "member/myPage";
	}
	
	@PostMapping("/update.me")
	public String updateMember(Member m, HttpSession session, Model model , RedirectAttributes ra) {
		int result = mService.updateMember(m);
		String url="";
		if(result > 0) {
			Member updateMember = mService.loginUser(m);
			session.setAttribute("loginUser", updateMember);
			ra.addFlashAttribute("alertMsg","Mission Complete!");
			// 1차적으로 alertMsg sessionScope로 이관
			// 리다이렉트 완료 후 sessionScope에 저장된 alertMsg를 requestScope로 다시 이관
			url="redirect:/myPage.me";
		}else {
			model.addAttribute("errorMsg","수정 안됬지롱~ㅋㅋ");
			url="common/errorPage";
		}
		return url;
	}
	/*
	 * 스프링 예외처리 방법 (3가지, 중복으로 사용 가능!)
	 * 1. 메서드별로 예외 처리(try-catch/ throw) -> 1순위로 적용됨
	 * 
	 * 2. 하나의 컨트롤러에서 발생하는 예외를 모아서 처리하는 방법 -> @ExceptionHandler(메서드 사용) -> 2순위
	 * 
	 * 3. 전역에서 발생하는 예외를 모아서 처리하는 클래스 -> @ControllerAdvice(클래스에 작성) -> 3순위
	 */
//	@ExceptionHandler(Exception.class)
//	public String exceptionHandler(Exception e, Model model) {
//		e.printStackTrace();
//		
//		model.addAttribute("errorMsg","아~ 안된다구~ㅋㅋ");
//		
//		return "common/errorPage";
//	}
	
	//아이디 중복검사 -> 비동기
	@ResponseBody
	@GetMapping("/idCheck.me")
	public String idCheck(String userId) {
		int result = mService.idCheck(userId);
		/*
		 * 컨트롤러에서 반환되는 값은 forward또는 redirect를 위한 경로인 경우가 일반적임.
		 * 즉, 반환되는값은 경로로써 인식함
		 * 
		 * 이를 해결하기위한 어노테이션이 @ResponseBody
		 * -> 반환되는 값을 응답(Response)의 몸통(body)에 추가하여 이전 요청주소로 돌아감
		 * => 컨트롤러에서 반환되는 값이 경로가 아닌 "값 자체"로 인식됨
		 */
		return result+"";
	}
	/*
	 * Spring방식 ajax요청 처리 방법
	 * jsonView 빈을 통해 데이터를 처리하기
	 * 
	 */
	@PostMapping("/selectOne")
	public String selectOne(String userId, Model model) {
		// 1. 업무 로직
		Member m = mService.selectOne(userId);
		
		if(m != null) {
			model.addAttribute("userId",m.getUserId());
			model.addAttribute("userName",m.getUserName());
			// model객체 안에 담긴 데이터를 json으로 변환후 응답처리해줌
		}
		return "jsonView";
	}

	@ResponseBody
	@PostMapping("/selectOne2")
	public Map<String, Object> selectOne2(String userId) {
		Member m = mService.selectOne(userId);
		Map<String,Object> map = new HashMap();
		if(m != null) {
			map.put("userId",m.getUserId());
			map.put("userName",m.getUserName());
		}
		return map;
	}
	
	@PostMapping("/selectOne3")
	public ResponseEntity<Map<String, Object>> selectOne3(String userId) {
		Member m = mService.selectOne(userId);
		Map<String,Object> map = new HashMap();
		if(m != null) {
			map.put("userId",m.getUserId());
			map.put("userName",m.getUserName());
		}
		
		ResponseEntity res = ResponseEntity.ok()
											.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
											.body(map);
		return res;
	}
	
//	@Scheduled(fixedDelay = 1000) // 고정방식
	public void test() {
		log.info("1초마다 출력");
	}
	
	public void testCron() {
		log.info("크크크크킄");
	}
	
}
