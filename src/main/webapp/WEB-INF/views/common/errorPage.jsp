<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="header.jsp"/>
	<br>
		<div align="center">
			<img src="https://cdn1.iconfinder.com/data/icons/video-game-elements-4/32/Warning_C-512.png">
			<br><br>
			<h1 style="font-weight:bold;">${errorMsg}</h1>
		</div>
	<br>	
	<jsp:include page="footer.jsp"/>
</body>
</html>