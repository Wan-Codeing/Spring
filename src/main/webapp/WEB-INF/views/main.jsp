<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
		<br>
		<div class="content" data-aos="fade-up" data-aos-delay="550">
			<div class="content-1">
               <h3>회원 정보 조회</h3>
               <p>아이디를 입력 받아 일치하는 회원 정보를 출력</p>
               아이디 : <input type="text" id="in1">
               		  <button id="select1">조회</button>
               <div id="result1" style="height:150px"></div>
               
               <script>
               		AOS.init();
               		//회원 정보 조회 (ajax)
               		document.getElementById('select1').addEventListener("click",function(){
               			const in1 = document.getElementById('in1');
               			const result1 = document.getElementById('result1');
               			
               			$.ajax({
               				url : '/spring/selectOne3',
               				data : {"userId" : in1.value},
               				type : "POST",
               				dataType : "JSON",
               				success : function(result){
               					console.log(result);
               					result1.innerHTML = "";
               					if(result.userId){
               						const ul = document.createElement("ul");
               						const li1 = document.createElement("li");
               						li1.innerText = "아이디 : "+ result.userId;
               						li1.style.listStyleType = "none";
               						const li2 = document.createElement("li");
               						li2.innerText = "이름 : "+ result.userName;
               						li2.style.listStyleType = "none";
               						ul.append(li1,li2);
               						result1.append(ul);
               					}
               				}
               			})
               		})
               </script>
        	</div>
		</div>
	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>