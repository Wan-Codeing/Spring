<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>    

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
img{
	width:400px;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	
	<div class="content">
		<br><br>
		<div class="innerOuter">
			<h2>게시글 상세보기</h2>
			<br>
			
			<table id="contentArea" align="center" class="table">
				<tr>
					<th width="100">제목</th>
					<td colspan="3">${board.boardTitle }</td>
				</tr>
				<tr>
					<th>작성자</th>
					<td>
						${board.boardWriter}
					</td>
					<th>작성일</th>
					<td>${board.createDate }</td>
				</tr>
				<c:if test="${!empty board.attachList }">
					<c:if test="${boardCode == 'C' }">
						<c:forEach items="${board.attachList }" var="attach">
							<tr>
								<th>첨부파일</th>
								<td>
									<button type="button" class="btn btn-outline-success btn-block"
									onclick="location.href='${contextPath}/board/fileDownload/${attach.fileNo }'">
										${attach.originName} - 다운로드
									</button>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${boardCode == 'P' }">
						<c:forEach var="i" begin="0" end="${fn:length(board.attachList) -1 }">
							<tr>
								<th>이미지${i+1}</th>
								<td colspan="3">
									<a href="${contextPath}${board.attachList[i].filePath}${board.attachList[i].changeName}" 
									   download="${board.attachList[i].originName }">
									<img src="${contextPath}${board.attachList[i].filePath}${board.attachList[i].changeName}">									
									</a>
								</td>
							</tr>
						</c:forEach>
					</c:if>					
				</c:if>
				<tr>
					<th>내용</th>
					<td></td>
					<th>조회수</th>
					<td>${board.count }</td>
				</tr>
				<tr>
					<td colspan="4">
						<p style="height:150px;">
							${board.boardContent }
						</p>
					</td>
				</tr>
			</table>
			
			<br>
			
			<div align="center">
				<!-- 현재 게시글을 작성한 본인인 경우에만 수정하기 버튼이 보여야함. -->
				<a class="btn btn-primary" href="${contextPath }/board/update/${boardCode}/${board.boardNo}">수정하기</a>
				
			</div>
			
			<br><br>
			<table id="replyArea" class="table" align="center">
				<thead>
					<tr>
						<th colspan="2">
							<textarea class="form-control" name="replyContent" id="replyContent" rows="2" cols="55" 
							  style="resize:none; width:100%;"></textarea>
						</th>
						<th style="vertical-align:middle;">
							<button class="btn btn-secondary" onclick="insertReply();">등록하기</button>
						</th>
					</tr>
					<tr>
						<td colspan="3">댓글(<span id="rcount">${ board.replyList.size() }</span>)</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="reply" items="${board.replyList }">
						<tr>
							<td>${reply.replyWriter }</td>
							<td>${reply.replyContent }</td>
							<td>${reply.createDate }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<script>
				// 비동기요청으로 처리
				// 1) 댓글목록 불러오기기능
				// 2) 댓글 작성기능			
				function insertReply(){
					$.ajax({
						url : "${contextPath}/board/insertReply",
						data: {
							refBno : ${boardNo},
							replyContent : $("#replyContent").val()
						},
						success : function(result) {
							if(result == "1"){
								alertify.alert("서비스 요청 성공", "댓글 등록 성공");
							}else{
								alertify.alert("서비스 요청 실패", "댓글 등록 실패");
							}
							// 댓글 목록 불러오는 기능
							selectReplyList();
						},
						complete : function(){
							 $("#replyContent").val("");
						}
					})
				}
				
				const selectReplyList = () => {
					$.ajax({
						url : '${contextPath}/board/selectReplyList',
						data: {bno : ${boardNo}},
						success : function(replyList){
							let html = "";
							for(let reply of replyList){
								html += "<tr>"
									html += "<td>" + reply.replyWriter +"</td>"
									html += "<td>" + reply.replyContent +"</td>"
									html += "<td>" + reply.createDate +"</td>"
								html += "</tr>"
							}
							$("#replyArea tbody").html(html);
							$("#rcount").html(replyList.length);
						}
					})
				}
				
				
			</script>
			
			
			
			
			
			
			
			
			
			
			
			
			
		
		</div>
	</div>
	<jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>