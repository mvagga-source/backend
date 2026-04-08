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
<link href="<c:url value='/css/notice/list.css'/>" rel="stylesheet">
<link href="<c:url value='/css/qna/view.css'/>" rel="stylesheet">
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>QnA 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
    <div class="admin-container">
        <div class="page-header-title">
            <h2>QnA 상세 및 답변</h2>
            <p>사용자의 문의 내용을 확인하고 관리자 답변을 등록합니다.</p>
        </div>

        <form id="qnaDetailForm">
            <input type="hidden" name="qno" value="${qna.qno}">

            <div class="dashboard-card">
                <div class="card-header">
                    <span class="title">사용자 문의 내용</span>
                    <div class="control-right">
                        <span class="total-info">등록일: <strong>${qna.crdt}</strong></span>
                    </div>
                </div>
                <div class="filter-container">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label>작성자(닉네임)</label>
                            <input type="text" value="${qna.member.nickname}" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>아이디</label>
                            <input type="text" value="${qna.member.id}" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>현재 상태</label>
                            <input type="text" value="${qna.status}" readonly class="readonly-input">
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>문의 제목</label>
                        <input type="text" value="${qna.qtitle}" readonly class="readonly-input" style="width: 100%;">
                    </div>
                    <div class="filter-group">
                        <label>문의 내용</label>
                        <div class="readonly-content-box">${qna.qcontent}</div>
                    </div>
                </div>
            </div>

            <div class="dashboard-card" style="border: 1.5px solid #1a2c4e;">
                <div class="card-header" style="background-color: #f8f9fc;">
                    <span class="title" style="color: #0080ff;">관리자 답변 등록</span>
                </div>
                <div class="filter-container">
                    <div class="filter-group">
                        <label>답변 내용</label>
                        <textarea name="answerContent" id="answerContent" 
                                  placeholder="사용자에게 전달될 답변을 입력하세요." 
                                  style="height: 250px; width: 100%; padding: 15px; border: 1px solid #dcdfe6; resize: vertical;">${qna.answerContent}</textarea>
                    </div>
                </div>
            </div>

            <div class="detail-btn-area">
                <button type="button" class="btn-action btn-add" id="btnSaveAnswer" style="padding: 0 40px; height: 45px; font-size: 15px;">답변 저장하기</button>
                <button type="button" class="btn-action btn-delete-server" id="btnGoList" style="padding: 0 40px; height: 45px; font-size: 15px; background-color: #909399;">목록으로</button>
            </div>
        </form>
    </div>
</div>
<script>
$(document).ready(function() {
	// 저장 버튼 클릭
    $('#btnSaveAnswer').on('click', function() {
        const answer = $('#answerContent').val().trim();
        if(!answer) {
            alert("답변 내용을 입력해주세요.");
            return;
        }

        if(!confirm("답변을 등록하시겠습니까?")) return;

        $.ajax({
            url: '/admin/community/qna/ajaxSaveReply',
            type: 'POST',
            data: $('#qnaDetailForm').serialize(),
            success: function(res) {
                if(res.success) {
                    alert("성공적으로 저장되었습니다.");
                    location.href = '/admin/community/qna/list'; // 저장 후 목록 이동
                } else {
                    alert("오류 발생: " + res.message);
                }
            }
        });
    });

    // 목록 버튼 클릭
    $('#btnGoList').on('click', function() {
        location.href = '/admin/community/qna/list';
    });
});
</script>
</body>
</html>