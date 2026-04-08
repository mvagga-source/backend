<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	<link href="<c:url value='/css/schedule/list.css'/>" rel="stylesheet">
    <title>일정 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-container">
	<div class="page-header-title">
		<div class="control-left">
			<h2>일정 관리</h2>
		</div>
		<div class="control-right">
			<div class="filter-btns">
            	<button type="button" id="btnCreate" class="btn-search">등록</button>
            </div>
		</div>        
    </div>

	<!-- 
		등록 
	-->
    <div class="dashboard-card form-section" id="createFormSection">
        <form action="/admin/schedule/save" method="post" id="saveForm" enctype="multipart/form-data">
            <div class="card-header">
                <span class="title">이벤트 등록</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                    <div class="filter-group">
				        <label>이벤트 제목</label>
						<input type="text" name="title" id="i-title" placeholder="제목을 입력하세요">							
				    </div>
                    <div class="filter-group">
				        <label>기간(시작일자 ~ 종료일자)</label>
				        <div class="input-combined">
				            <input type="date" name="startDate" id="i-startDate">
				            <span class="txt-dash">~</span>
				            <input type="date" name="endDate" id="i-endDate">
				        </div>
				    </div>				    
                    <div class="filter-group">
				        <label>중요 이벤트 여부</label>
						<select name="highlightFlag" id="i-highlightFlag">
							<option value="">= 선택 =</option>
							<option value="N">N</option>
							<option value="Y">Y</option>
						</select>							
				    </div>				    
                    <div class="filter-group">
				        <label>이벤트 설명</label>
						<input type="text" name="description" id="i-description" placeholder="설명을 입력하세요">							
				    </div>
                    <div class="filter-btns">
                        <button type="submit" id="btnSaveGrid" class="btn-action btn-save-main">저장</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
    
    <!-- 수정 -->
    <div class="dashboard-card form-section" id="updateFormSection">
        <form action="/admin/schedule/save" method="post" id="updateForm" enctype="multipart/form-data">
        	<input type="hidden" name="eno" id="u-eno"/>
            <div class="card-header">
                <span class="title">비디오 수정</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                    <div class="filter-group">
				        <label>이벤트 제목</label>
						<input type="text" name="title" id="u-title" placeholder="제목을 입력하세요">							
				    </div>
                    <div class="filter-group">
				        <label>기간(시작일자 ~ 종료일자)</label>
				        <div class="input-combined">
				            <input type="date" name="startDate" id="u-startDate">
				            <span class="txt-dash">~</span>
				            <input type="date" name="endDate" id="u-endDate">
				        </div>
				    </div>
					<div class="filter-group">
				        <label>중요 이벤트 여부</label>
						<select name="highlightFlag" id="u-highlightFlag">
							<option value="">= 선택 =</option>
							<option value="N">N</option>
							<option value="Y">Y</option>
						</select>							
				    </div>				    
                    <div class="filter-group">
				        <label>이벤트 설명</label>
						<input type="text" name="description" id="u-description" placeholder="설명을 입력하세요">							
				    </div>
                    <div class="filter-btns">
                        <button type="submit" id="btnSaveGrid" class="btn-action btn-save-main">저장</button>
                    </div>
                </div>
            </div>
        </form>
    </div>    

	<!-- 검색 -->
    <div class="dashboard-card grid-area">
        <div class="card-header grid-header">
            <span class="title">비디오 목록(${eventList.totalCount} 건)</span>
            <!-- <button type="button" onclick="location.href='/admin/notice/write'" class="btn-save" style="background:#1a2c4e; margin-left:10px;">공지 등록</button> -->
        </div>
        <form action="/admin/schedule/list" method="get" id="searchForm">
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group">
                        <label>검색어</label>
                        <div class="input-combined">
                            <select name="searchType" id="searchType" style="width: 130px;">
                                <option value="">전체</option>
                                <option value="TITLE">제목</option>
                                <option value="CONTENT">설명</option>
                            </select>
                            <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
                        </div>
                    </div>
			        <div class="control-right">
			            <div class="select-wrapper">
			            	<c:set var="sizes" value="${[10, 20, 30]}" />
			            	<select name="size" id="size" class="select-custom">
				            	<c:forEach var="s" items="${sizes}" varStatus="status">
				            		<c:if test="${videoList.size == s}">
				            			<option value=${s} selected>${s}개씩</option>
				            		</c:if>
				            		<c:if test="${videoList.size != s}">
				            			<option value=${s}>${s}개씩</option>
				            		</c:if>
				            	</c:forEach>
			            	</select>
			            </div>
			        </div>    				    
                    <div class="filter-btns">
                        <button type="submit" id="btnSearch" class="btn-search"">검색하기</button>
                    </div>
               
                </div>
            </div>
        </form>
        
        <div id="grid-container">
			<table>
				<c:set var="widths" value="${['5%', '25%', '10%', '10%', '15%', '10%', '10%', '5%', '10%']}" />
          		<colgroup>
          			<c:forEach var="w" items="${widths}" varStatus="status">
        				<col style="width: ${w};" />
    				</c:forEach>
          		</colgroup>			
				<thead>
	              <th>순번</th>
	              <th>제목</th>
	              <th>시작일자</th>
	              <th>종료일자</th>
	              <th>설명</th>
	              <th>중요 여부</th>
	              <th>등록일자</th>
	              <th>삭제</th>
	              <th>처리</th>        
				</thead>
				<tbody>
					<c:forEach var="event" items="${eventList.list}">
						<tr>
							<td class="center">${event.eno}</td>
							<td>${event.title}</td>
							<td class="center">${event.startDate}</td>
							<td class="center">${event.endDate}</td>
							<td>${event.description}</td>
							<td class="center">${event.highlightFlag}</td>
							<td class="center">${fn:substring(event.createdAt, 0, 10)}</td>
							<td class="center">${event.deletedFlag}</td>
							<td class="center">
					            <button class="btn-update btn-action btn-save-main"
					            		data-eno="${event.eno}"
					            		data-title="${event.title}"
					            		data-startDate="${event.startDate}"
					            		data-endDate="${event.endDate}"
					            		data-highlightFlag="${event.highlightFlag}"
					            		data-description="${event.description}"
					            >수정</button>
					            <button class="btn-delete btn-action btn-delete-server"
					            		data-eno="${event.eno}"
					            >삭제</button>
							</td>
						</tr>
					</c:forEach>				
				</tbody>
			</table>
			
	        <div class="pagination">
	            <a href="/admin/schedule/list?page=1&search=${search}&searchType=${searchType}">&laquo;</a>
	            
	            <c:forEach var="i" begin="${eventList.startPage}" end="${eventList.endPage}" step="1">
	            	<c:if test="${eventList.page == i}">
	            		<a class="active">${i}</a>
	            	</c:if>
	            	<c:if test="${eventList.page != i}">
	            		 <a href="/admin/schedule/list?page=${i}&search=${search}&searchType=${searchType}">${i}</a>
	            	</c:if>            	
	            </c:forEach>
	            
	            <a href="/admin/schedule/list?page=${eventList.maxPage}&search=${search}&searchType=${searchType}">&raquo;</a>
	        </div>
        
        </div>
        
        
    </div>
</div>
<%@ include file="/WEB-INF/views/admin/notice/list/noticePop.jsp" %>

<script>

	const createSection = document.getElementById("createFormSection");
    const updateSection = document.getElementById("updateFormSection");

    document.getElementById("btnCreate").addEventListener("click", () => {
        createSection.classList.add("active");
        updateSection.classList.remove("active");
    });

    document.addEventListener("click", (e) => {

        if (e.target.classList.contains("btn-update")) {

            const target = e.target;

            /*
            console.log(target);
            console.log(target.dataset.title);
            console.log(target.dataset.startdate);
            console.log(target.dataset.enddate);
            */
            
            document.getElementById("u-eno").value = target.dataset.eno;
            document.getElementById("u-title").value = target.dataset.title;
            document.getElementById("u-startDate").value = target.dataset.startdate;
            document.getElementById("u-endDate").value = target.dataset.enddate;      
            document.getElementById("u-highlightFlag").value = target.dataset.highlightflag;
            document.getElementById("u-description").value = target.dataset.description;

            updateSection.classList.add("active");
            createSection.classList.remove("active");
        }
    });

    // 초기 화면은 등록 폼
    createSection.classList.add("active");
    
    $(document).on("click", ".btn-delete", function () {
    	const eno = $(this).data("eno");
    	
		if(!confirm("정말 삭제 하시겠습니까?")) {
			return;
		}    	
    	
    	$.ajax({
            url: "/admin/schedule/delete?eno=" + eno,
            type: "POST",
            success: function () {
                alert("삭제 완료");
                location.reload();
            }
        });
    });
  
</script>
</body>
</html>