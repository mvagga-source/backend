<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link rel="stylesheet" href="<c:url value='/css/tui-time-picker.css'/>" />
<link rel="stylesheet" href="<c:url value='/css/tui-date-picker.css'/>" />
<link rel="stylesheet" href="<c:url value='/css/tui-grid.css'/>" />
<link rel="stylesheet" href="<c:url value='/css/tui-pagination.css'/>" />
<script src="<c:url value='/js/jquery.min.js'/>"></script>
<script src="<c:url value='/js/tui-time-picker.js'/>"></script>
<script src="<c:url value='/js/tui-date-picker.js'/>"></script>
<script src="<c:url value='/js/tui-pagination.js'/>"></script>
<script src="<c:url value='/js/tui-grid.js'/>"></script>
<script src="<c:url value='/js/gridutils.js'/>"></script><!-- 공통기능은 클래스 형태로 축약 -->
<link href="<c:url value='/css/notice/notice.css'/>" rel="stylesheet">
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항 관리</title>
    </head>
<body>
    <%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

    <div style="padding: 32px;">
        <div style="background: white; padding: 24px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
            <h2>공지사항 목록</h2>
            <hr style="margin: 16px 0;">
            
            <div id="grid"></div>
        </div>
    </div>
</body>
</html>