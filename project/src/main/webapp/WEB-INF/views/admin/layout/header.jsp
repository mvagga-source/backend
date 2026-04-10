<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link href="<c:url value='/css/header/header.css'/>" rel="stylesheet">
<!-- 헤더 -->
<div class="admin-header">
	<h1><a id="main-logo" href="/admin/main">ACTION 101 — 관리자</a></h1>
	<span>Admin Page</span>
</div>

<%-- 현재 요청된 URI를 가져옴 (예: /admin/notice/list) --%>
<c:set var="requestURI" value="${pageContext.request.requestURI}" />

<!-- 네비게이션 -->
<div class="admin-nav">
	<a href="<c:url value='/admin/audition/round'/>" class="nav-btn ${fn:contains(requestURI, '/audition/') ? 'active' : ''}">오디션 관리</a>
	<a href="<c:url value='/admin/video/list'/>" class="nav-btn ${fn:contains(requestURI, '/video/') ? 'active' : ''}">비디오 관리</a>
	<a href="<c:url value='/admin/schedule/list'/>" class="nav-btn ${fn:contains(requestURI, '/schedule/') ? 'active' : ''}">일정 관리</a>	
	<a href="<c:url value='/admin/profile/list'/>" class="nav-btn ${fn:contains(requestURI, '/profile/') ? 'active' : ''}">프로필 관리</a>
	<a href="<c:url value='/admin/goods/list'/>" class="nav-btn ${fn:contains(requestURI, '/goods/') ? 'active' : ''}">굿즈 관리</a>    
	<a href="<c:url value='/admin/notice/list'/>" class="nav-btn ${fn:contains(requestURI, '/notice/') ? 'active' : ''}">공지사항 관리</a>
	<a href="<c:url value='/admin/community/qna/list'/>" class="nav-btn ${fn:contains(requestURI, '/community/qna') ? 'active' : ''}">문의사항 관리</a>
	<a href="<c:url value='/admin/community/idea/list'/>" class="nav-btn ${fn:contains(requestURI, '/community/idea') ? 'active' : ''}">아이디어 관리</a>
	<a href="<c:url value='/admin/community/report/list'/>" class="nav-btn ${fn:contains(requestURI, '/community/report') ? 'active' : ''}">신고 관리</a>
</div> 