<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	<link href="<c:url value='/css/video/list.css'/>" rel="stylesheet">
    <title>비디오 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-container">
	<div class="page-header-title">
		<div class="control-left">
			<h2>비디오 관리</h2>
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
        <form action="/admin/video/saveVideo" method="post" id="saveForm" enctype="multipart/form-data">
            <div class="card-header">
                <span class="title">비디오 등록</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group" >
                        <label>아이돌</label>
                        <select name="idol_profile" id="i-idol_profile">
                            <option value="">== 선택 ==</option>
                            <c:forEach var="idol" items="${idolList}">
                            	<option value=${idol.profileId}>${idol.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-group">
				        <label>노래 제목</label>
						<input type="text" name="title" id="i-title" placeholder="제목을 입력하세요">							
				    </div>
                    <div class="filter-group">
				        <label>유튜브 URL</label>
						<input type="text" name="url" id="i-url" placeholder="url을 입력하세요">							
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
        <form action="/admin/video/saveVideo" method="post" id="updateForm" enctype="multipart/form-data">
        	<input type="hidden" name="id" id="u-id"/>
            <div class="card-header">
                <span class="title">비디오 수정</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group" >
                        <label>아이돌</label>
                        <select name="idol_profile" id="u-idol_profile">
                            <option value="">== 선택 ==</option>
                            <c:forEach var="idol" items="${idolList}">
                            	<option value=${idol.profileId}>${idol.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-group">
				        <label>노래 제목</label>
						<input type="text" name="title" id="u-title" placeholder="제목을 입력하세요">							
				    </div>
                    <div class="filter-group">
				        <label>유튜브 URL</label>
						<input type="text" name="url" id="u-url" placeholder="url을 입력하세요">							
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
            <span class="title">비디오 목록 (${videoList.totalCount} 건)</span>
            <!-- <button type="button" onclick="location.href='/admin/notice/write'" class="btn-save" style="background:#1a2c4e; margin-left:10px;">공지 등록</button> -->
        </div>
        <form action="/admin/video/list" method="get" id="searchForm">
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group">
                        <label>검색어</label>
                        <div class="input-combined">
                            <select name="searchType" id="searchType" style="width: 130px;">
                                <option value="">전체</option>
                                <option value="NAME">이름</option>
                                <option value="TITLE">제목</option>
                            </select>
                            <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
                        </div>
                    </div>
                    <!-- 
                    <div class="filter-group" style="flex: 2;">
				        <label>등록일자</label>
				        <div class="input-combined">
				            <input type="date" name="startDate" id="startDate">
				            <span class="txt-dash">~</span>
				            <input type="date" name="endDate" id="endDate">
				        </div>
				    </div>
				     -->
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
				<c:set var="widths" value="${['5%', '10%', '30%', '30%', '10%', '5%', '10%']}" />
          		<colgroup>
          			<c:forEach var="w" items="${widths}" varStatus="status">
        				<col style="width: ${w};" />
    				</c:forEach>
          		</colgroup>			
				<thead>
	              <th>순번</th>
	              <th>이름</th>
	              <th>노래 제목</th>
	              <th>유튜브 URL</th>
	              <th>등록일자</th>
	              <th>삭제</th>        
	              <th>처리</th>
				</thead>
				<tbody>
					<c:forEach var="video" items="${videoList.list}">
						<tr>
							<td class="center">${video.id}</td>
							<td>${video.idol_profile.name}</td>
							<td>${video.title}</td>
							<td>${video.url}</td>
							<td class="center">${fn:substring(video.createdAt, 0, 10)}</td>
							<td class="center">${video.deletedFlag}</td>
							<td class="center">
					            <button class="btn-update btn-action btn-save-main"
					            		data-id="${video.id}"
					            		data-title="${video.title}"
					            		data-profileId="${video.idol_profile.profileId}"
					            		data-url="${video.url}"
					            		>수정</button>
					            <button class="btn-delete btn-action btn-delete-server"
					            		data-id="${video.id}"
					            >삭제</button>
							</td>
						</tr>
					</c:forEach>				
				</tbody>
			</table>
			
	        <div class="pagination">
	            <!-- <a href="/board/blist?page=1">&laquo;</a>  -->
	            <a href="/admin/video/list?page=1&search=${search}&searchType=${searchType}">&laquo;</a>
	            
	            <c:forEach var="i" begin="${videoList.startPage}" end="${videoList.endPage}" step="1">
	            	<c:if test="${videoList.page == i}">
	            		<a class="active">${i}</a>
	            	</c:if>
	            	<c:if test="${videoList.page != i}">
	            		 <a href="/admin/video/list?page=${i}&search=${search}&searchType=${searchType}">${i}</a>
	            	</c:if>            	
	            </c:forEach>
	            
	            <a href="/admin/video/list?page=${videoList.maxPage}&search=${search}&searchType=${searchType}">&raquo;</a>
	            <!-- <a href="/board/blist?page=${map.maxPage}">&raquo;</a> -->
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
            console.log(target.dataset.profileid);
            console.log(target.dataset.url);
            */
            
            document.getElementById("u-id").value = target.dataset.id;
            document.getElementById("u-title").value = target.dataset.title;
            document.getElementById("u-idol_profile").value = target.dataset.profileid;
            document.getElementById("u-url").value = target.dataset.url;

            updateSection.classList.add("active");
            createSection.classList.remove("active");
        }
    });

    // 초기 화면은 등록 폼
    createSection.classList.add("active");
    
    $(document).on("click", ".btn-delete", function () {
    	const id = $(this).data("id");
    	
		if(!confirm("정말 삭제 하시겠습니까?")) {
			return;
		}    	
    	
    	$.ajax({
            url: "/admin/video/delete?id=" + id,
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