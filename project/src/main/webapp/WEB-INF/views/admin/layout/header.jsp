<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link href="<c:url value='/css/header/header.css'/>" rel="stylesheet">
<!-- 헤더 -->
<div class="admin-header">
	<h1>ACTION 101 — 관리자</h1>
	<span>Admin Page</span>
</div>

<%-- 현재 요청된 URI를 가져옴 (예: /admin/notice/list) --%>
<c:set var="requestURI" value="${pageContext.request.requestURI}" />

<!-- 네비게이션 -->
<div class="admin-nav">
	<a href="<c:url value='/admin/audition/list'/>" class="nav-btn ${fn:contains(requestURI, '/audition/') ? 'active' : ''}">오디션 관리</a>
	<a href="<c:url value='/admin/video/list'/>" class="nav-btn ${fn:contains(requestURI, '/video/') ? 'active' : ''}">비디오 관리</a>    
	<a href="<c:url value='/admin/notice/list'/>" class="nav-btn ${fn:contains(requestURI, '/notice/') ? 'active' : ''}">공지사항 관리</a>
	<a href="<c:url value='/admin/test/list'/>" class="nav-btn ${fn:contains(requestURI, '/test/') ? 'active' : ''}">test탭</a>
</div>