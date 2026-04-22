<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>ACTION 101 관리자</title>
  <link href="<c:url value='/css/adminMain.css'/>" rel="stylesheet">
</head>
<body>

  <%-- 공통 헤더 & 네비게이션 --%>
  <%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

  <div class="page-body">

    <div class="welcome">
      <h2>관리자 메인</h2>
      <p>관리할 항목을 선택하세요.</p>
    </div>
	<div class="menu-grid">
	
	  <a href="<c:url value='/admin/audition/round'/>" class="menu-card">
	    <div class="icon">🎤</div>
	    <div class="label">오디션 관리</div>
	    <div class="desc">회차 등록·수정, 상태 변경,<br>참가자 탈락 처리</div>
	  </a>
	
	  <a href="<c:url value='/admin/video/list'/>" class="menu-card">
	    <div class="icon">🎬</div>
	    <div class="label">비디오 관리</div>
	    <div class="desc">영상 업로드·관리</div>
	  </a>
	
	  <a href="<c:url value='/admin/schedule/list'/>" class="menu-card">
	    <div class="icon">📅</div>
	    <div class="label">일정 관리</div>
	    <div class="desc">일정 등록·수정·삭제</div>
	  </a>
	
	  <a href="<c:url value='/admin/profile/list'/>" class="menu-card">
	    <div class="icon">👤</div>
	    <div class="label">프로필 관리</div>
	    <div class="desc">아이돌 프로필 등록·수정</div>
	  </a>
	
	  <a href="<c:url value='/admin/goods/list'/>" class="menu-card">
	    <div class="icon">🛍️</div>
	    <div class="label">굿즈 관리</div>
	    <div class="desc">상품 등록·수정·삭제</div>
	  </a>
	
	  <a href="<c:url value='/admin/notice/list'/>" class="menu-card">
	    <div class="icon">📢</div>
	    <div class="label">공지사항 관리</div>
	    <div class="desc">공지 등록·수정·삭제</div>
	  </a>
	
	  <a href="<c:url value='/admin/community/qna/list'/>" class="menu-card">
	    <div class="icon">💬</div>
	    <div class="label">문의사항 관리</div>
	    <div class="desc">회원 문의 확인·답변 처리</div>
	  </a>
	
	  <a href="<c:url value='/admin/community/idea/list'/>" class="menu-card">
	    <div class="icon">💡</div>
	    <div class="label">아이디어 관리</div>
	    <div class="desc">아이디어 제안 확인·처리</div>
	  </a>
	
	  <a href="<c:url value='/admin/community/report/list'/>" class="menu-card">
	    <div class="icon">🚨</div>
	    <div class="label">신고 관리</div>
	    <div class="desc">신고 내역 확인·처리</div>
	  </a>
	
	  <a href="http://localhost:3000/" class="menu-card">
	    <div class="icon">🏠</div>
	    <div class="label">메인 홈으로</div>
	    <div class="desc">ACTION 101 사용자<br>페이지로 돌아가기</div>
	  </a>
	
	</div>

  </div>

</body>
</html>