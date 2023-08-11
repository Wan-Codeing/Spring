<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="${contextPath }/resources/css/main-style.css"
	rel="stylesheet">
<link href="${contextPath }/resources/css/chat-style.css"
	rel="stylesheet">
<style>
	.chatting-area{
		margin :auto;
		height : 600px;
		width : 800px;
		margin-top : 50px;
		margin-bottom : 500px;
	}
	#exit-area{
		text-align:right;
		margin-bottom : 10px;
	}
	.display-chatting {
		width:42%;
		height:450px;
		border : 1px solid gold;
		overflow: auto; /*스크롤 처럼*/
		list-style:none;
		padding: 10px 10px;
		background : lightblue;
		z-index: 1;
   		 margin: auto;
		background-image : url(${contextPath}/resources/main/chunsickbackground.png);
	    background-position: center;
	}
	.img {
		width:100%;
		height:100%;
 		position: relative;
 		z-index:-100;
	}
	.chat{
		display : inline-block;
		border-radius : 5px;
		padding : 5px;
		background-color : #eee;
	}
	.input-area{
		width:100%;
		display:flex;
		justify-content: center;
	}
	#inputChatting{
		width: 32%;
		resize : none;
	}
	#send{
		width:20%;
	}
	.myChat{
		text-align: right;
	}
	.myChat > p {
		background-color : gold;
	}
	.chatDate{
		font-size : 10px;
	}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	
		<div class="chatting-area">
			<div id="exit-area">
				<button class="btn btn-outline-danger" id="exit-btn" onclick="location.href='${contextPath}/chat/exit/${chatRoomNo}';">나가기</button>
			</div>
			<ul class="display-chatting">
				<c:forEach items="${list}" var="msg">
					<c:if test="${msg.userNo eq loginUser.userNo }">
						<li class="myChat">
							<span class="chatDate">${msg.createDate}</span>
							<p class="chat">${msg.message}</p>
						</li>
					</c:if>
					<c:if test="${msg.userNo ne loginUser.userNo}">
						<li>
							<b>${msg.userName }</b>
							<p class="chat">${msg.message}</p>
							<span class="chatDate">${msg.createDate}</span>	
						</li>
					</c:if>
				</c:forEach>
				
				
				
			</ul>
			
			<div class="input-area">
				<textarea id="inputChatting" row="3"></textarea>
				<button id="send">보내기</button>
			</div>
			
		</div>
		
		<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
		<script>
			const userNo = "${loginUser.userNo}";
			const userName = "${loginUser.userName}";
			const chatRoomNo = "${chatRoomNo}";
			const contextPath= "${contextPath}"; 
			
			// 로그인이 되어있는 경우에만 WebSocket객체 생성할 예정
			let chattingSock = new SockJS(contextPath+"/chat");
			// /chat이라는 요청주소를 통해 웹소켓 객체 생성완료 websocket프로토콜을 이용해서 해당 주소와 데이터를 실시간으로 송/수신 할 수 있다\
			
			/*
				WebSocket
				- 브라우저와 웹서버간의 통신을 지원하는 프로토콜
				
				* 전이중 통신(Full Duplex) : 두대의 단말기가 데이터를 동시에 송/수신하기 위해 각각 독립된 회선을 사용하는 통신방식
				
				- HTML5부터 지원
				- Java에서는 7버전부터 지원(8버전이상에서 사용을 권장)
				- Spring Framework 4버전 이상부터 지원
			*/
		</script>
		<script src="${contextPath}/resources/js/chat.js"></script>
	<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>