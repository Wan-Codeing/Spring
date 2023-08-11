/**
 * 
 */
 // 페이지 로딩 완료후 채팅창 밑으로 내리기
 // 즉시 실행 함수(IIFE, 속도 빠름, 변수명 중복 문제 해결)
 (
    function(){
        const display = document.getElementsByClassName("display-chatting")[0];
        
        if(display != null){
            display.scrollTop = display.scrollHeight;
        }
    }
)();

// 채팅 메세지 보내기 기능
document.getElementById("send").addEventListener("click", sendMessage);

// 채팅 메세지 보내기 함수
function sendMessage(){
    
    // 채팅이 입력되고 있는 textarea
    const inputChatting = document.getElementById("inputChatting");
    
    if(inputChatting.value.trim().length == 0){
        // 입력이 되지 않은경우
        alert("채팅을 입력해주세요 ㅎ");
        
        inputChatting.value =""; // 공백문자 삭제
        inputChatting.focus();
    }else{
        // 입력이 된경우
        
        // 메세지 입력시 필요한 데이터를 객체로 생성
        const chatMessage = {
            "userNo" : userNo,
            "userName" : userName,
            "chatRoomNo" : chatRoomNo,
            "message" : inputChatting.value
        };
        
        const parsedMessage = JSON.stringify(chatMessage);
        
        // send(값) : 웹 소켓 핸들러로 값을 보냄 ==> 웹소켓 핸들러 내부의 handleTextMessage가 받아줌
        chattingSock.send(parsedMessage);
        
        inputChatting.value = "";
        
    }
}

// 웹소켓 핸들러에서 클라이언트 소켓으로 메세지를 전달하는 구문을 감지하는 이벤트 핸들러
// s.sendMessage()함수가 호출되는 시점을 감지
chattingSock.onmessage = function(e){
    console.log(e, e.data);
    // e.data : 전달된 메세지(JSON)
    
    // 전달받은 메세지를 JS객체로 변환
    const chatMessage = JSON.parse(e.data);
    console.log(chatMessage);
    
    const li = document.createElement("li");
    const p = document.createElement("p");
    p.classList.add("chat");
    
    // 글내용 추가 + 개행처리
    p.innerHTML = chatMessage.message.replace( /\\n/gm , "<br>" );
    
    const span = document.createElement("span");
    span.classList.add("chatDate");
    span.innerText = currentTime(); // 날짜 추가
    
    // 내가쓴채팅 ? 남의쓴채팅?
    if(chatMessage.userNo == userNo){
    	li.classList.add("myChat"); // 내가 쓴 글 스타일 적용
		li.append(span , p);    	
    } else {
    	li.innserHTML = "<b>" + chatMessage.userName + "</b><br>";
    	li.append(p, span);
    }
    
    // 채팅창 
    const display = document.getElementsByClassName("display-chatting")[0];
    
    // 채팅창에 채팅내용 추가
    display.append(li);
    
    // 채팅창 맨 아래로 내리기
    display.scrollTop = display.scrollHeight;
    // scrollTop : 스크롤위치값
    // scrollHeight : 스크롤되는 요소의 전체 높이
}

// 현재시간 출력 함수
function currentTime(){
	const now = new Date();
	
	let time ="";
	
	time = now.getFullYear() + "-" +addZero(now.getMonth() + 1) + "-" + addZero(now.getDate());
	
	return time; 
}

function addZero(time){
	return time < 10 ? "0" + time : time;
}