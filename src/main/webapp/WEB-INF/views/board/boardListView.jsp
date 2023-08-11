<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	  #boardList {text-align:center;}
      #boardList>tbody>tr:hover {cursor:pointer;}

      #pagingArea {width:fit-content; margin:auto;}
      
      #searchForm {
          width:80%;
          margin:auto;
      }
      #searchForm>* {
          float:left;
          margin:5px;
      }
      .select {width:20%;}
      .text {width:53%;}
      .searchBtn {width:20%;}
      /* 썸네일 관련 스타일 */
		#boardList tr > td:nth-of-type(2){ /* 2번째 td(제목) */
		    position: relative;
		}
      .list-thumbnail{
        max-width: 50px;
	    max-height: 30px;
	
	    position: absolute;
	    left : -15px;
	    top : 10px;
      }
</style>

</head>
<body>

	<jsp:include page="/WEB-INF/views/common/header.jsp"/>
	
	<div class="content">
		<br><br>
		<div class="innerOuter" style="padding: 5% 10%;">
			<h2>
				<c:forEach items="${boardTypeList }" var="bt">
					<c:if test="${bt.boardCd == boardCode }">
						${bt.boardName }
					</c:if>
				</c:forEach>
			</h2>
			<br>
			<!-- 로그인시에만 보이는 글쓰기 버튼 -->
			<br>
			<c:if test="${not empty loginUser }">
				<a class="btn btn-secondary" style="float:right;" href="${contextPath }/board/insert/${boardCode}">글쓰기</a>
			</c:if>
			<br>
			
			<table id="boardList" class="table table-hover" align="center">
				<thead>
					<tr>
						<th>글번호</th>
						<th>제목</th>
						<th>작성자</th>
						<th>조회수</th>
						<th>작성일</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${empty list }">
						<td colspan="5">게시글이 없네요..</td>
					</c:if>
					<c:forEach var="b" items="${list }">
						<tr onclick="movePage(${b.boardNo});">
							<td>${b.boardNo }</td>
							<td>${b.boardTitle }</td>
							<td>${b.boardWriter }</td>
							<td>${b.count }</td>
							<td>${b.createDate }</td>
						</tr>
					</c:forEach>
				</tbody>			
			</table>
			<script>
				function movePage(no){
					location.href = "${contextPath}/board/detail/${boardCode}/"+no;
				}
			</script>
			<br>
			
			<c:set var="url" value="${boardCode }?currentPage="/>
			
			<c:if test="${not empty param.condition }">
				<c:set var="sUrl" value="&condition=${param.condition }&keyword=${param.keyword }"/>
			</c:if>
			
			<div id="pagingArea">
				<ul class="pagination">
					<c:choose>
						<c:when test="${pi.currentPage eq 1 }">
							<li class="page-item disabled"><a class="page-link" href="#">Previous</a></li>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link" href="${url}${pi.currentPage -1}${sUrl}">Previous</a></li>
						</c:otherwise>
					</c:choose>
					
					<c:forEach var="item" begin="${pi.startPage }" end="${pi.endPage }">
						<li class="page-item">
							<a class="page-link" href="${url }${item}${sUrl}">${item}</a>
						</li>
					</c:forEach>
					
					<c:choose>
						<c:when test="${pi.currentPage eq pi.maxPage }">
							<li class="page-item disabled"><a class="page-link" href="#">Next</a></li>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link" href="${url}${pi.currentPage +1}${sUrl}">Next</a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
			
			<br clear="both"><br>
			
			
			<form id="searchForm" action="${boardCode }" method="get" align="center">
				<div class="select">
					<select class="custom-select" name="condition">
						<option value="writer" ${param.condition eq 'writer' ? 'selected' : '' }>작성자</option>
						<option value="title" ${param.condition eq 'title' ? 'selected' : '' }>제목</option>
						<option value="content" ${param.condition eq 'content' ? 'selected' : '' }>내용</option>
						<option value="titleAndContent" ${param.condition eq 'titleAndContent' ? 'selected' : '' }>제목+내용</option>
					</select>
				</div>
				<div class="text">
					<input type="text" class="form-control" name="keyword" value="${param.keyword }" />
				</div>
				<button type="submit" class="searchBtn btn btn-secondary">검색</button>
			</form>
			
			
			
			
		</div>	
	</div>	
	<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

</body>
</html>