<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script src="<c:url value='/js/jquery.min.js'/>"></script>
<link href="<c:url value='/css/notice/list/noticePop.css'/>" rel="stylesheet">
<div id="noticePopup" class="notice-overlay">
    <div class="notice-popup-container">
        <div class="notice-image-header">
            <div class="notice-text-logo">ACTION101 공지사항</div>
        </div>

        <div class="notice-text-section">
            <input type="hidden" id="popRowKey">
            <input type="text" id="popTitle" class="notice-input-title" placeholder="공지 제목을 입력하세요">
            
            <div class="notice-scroll-content">
                <textarea id="popContent" class="notice-textarea-content" placeholder="공지 내용을 상세히 입력하세요"></textarea>
            </div>
            
            <div class="fake-pagination">
                <span class="dot active"></span>
                <span class="dot"></span>
                <span class="dot"></span>
            </div>
        </div>

        <div class="notice-footer">
            <button type="button" class="notice-footer-btn close-btn" onclick="closeNoticePopup()">닫기</button>
            <button type="button" class="notice-footer-btn save-btn" onclick="saveNoticePopup()">적용하기</button>
        </div>
    </div>
</div>