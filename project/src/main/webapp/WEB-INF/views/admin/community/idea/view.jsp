<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link href="<c:url value='/css/notice/list.css'/>" rel="stylesheet">
<link href="<c:url value='/css/qna/view.css'/>" rel="stylesheet">

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>아이디어 상세 관리</title>
</head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
    <div class="admin-container">
        <div class="page-header-title">
            <h2>아이디어 상세 조회</h2>
            <p>제안된 아이디어의 상세 내용을 확인하고 검토 의견을 등록합니다.</p>
        </div>

        <form id="ideaDetailForm">
            <input type="hidden" name="ideano" value="${idea.ideano}">

            <div class="dashboard-card">
                <div class="card-header">
                    <span class="title">아이디어 제안 정보</span>
                    <div class="control-right">
                        <span class="total-info">등록일: <strong><fmt:formatDate value="${idea.crdt}" pattern="yyyy-MM-dd HH:mm:ss"/></strong></span>
                    </div>
                </div>
                <div class="filter-container">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label>제안자(닉네임)</label>
                            <input type="text" value="${idea.member.nickname}" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>아이디</label>
                            <input type="text" value="${idea.member.id}" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>카테고리</label>
                            <input type="text" value="${idea.ideacategory}" readonly class="readonly-input">
                        </div>
                    </div>
                    
                    <div class="filter-group">
                        <label>아이디어 제목</label>
                        <input type="text" value="${idea.ideatitle}" readonly class="readonly-input" style="width: 100%;">
                    </div>

                    <div class="filter-group">
                        <label>상세 제안 내용</label>
                        <div class="readonly-content-box">${idea.ideacontent}</div>
                    </div>

                    <div class="filter-group">
                        <label>첨부파일</label>
                        <div class="file-download-area">
                            <c:choose>
                                <c:when test="${not empty idea.ideafile}">
                                    <a href="/admin/download/download?fileName=${idea.ideafile}" class="btn-action btn-add" style="text-decoration: none; display: inline-flex; align-items: center; gap: 5px;">
                                        <span>💾</span> ${idea.ideafile} (다운로드)
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #909399; font-size: 14px;">첨부된 파일이 없습니다.</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <%-- <div class="dashboard-card" style="border: 1.5px solid #1a2c4e;">
                <div class="card-header" style="background-color: #f8f9fc;">
                    <span class="title" style="color: #0080ff;">관리자 검토 의견</span>
                </div>
                <div class="filter-container">
                    <div class="filter-group">
                        <label>검토 및 피드백 내용</label>
                        <textarea name="adminFeedback" id="adminFeedback" placeholder="아이디어에 대한 검토 의견을 입력하세요." 
                                  style="height: 200px; width: 100%; padding: 15px; border: 1px solid #dcdfe6; resize: vertical;">${idea.member}</textarea>
                    </div>
                </div>
            </div>

            <div class="detail-btn-area">
                <button type="button" class="btn-action btn-add" id="btnSaveFeedback" style="padding: 0 40px; height: 45px; font-size: 15px;">의견 저장하기</button>
                <button type="button" class="btn-action btn-delete-server" id="btnGoList" style="padding: 0 40px; height: 45px; font-size: 15px; background-color: #909399;">목록으로</button>
            </div> --%>
        </form>
    </div>
</div>

<script>
$(document).ready(function() {
    // 의견 저장 버튼
    $('#btnSaveFeedback').on('click', function() {
        const feedback = $('#adminFeedback').val().trim();
        if(!feedback) {
            alert("검토 의견을 입력해주세요.");
            return;
        }

        if(!confirm("작성하신 의견을 저장하시겠습니까?")) return;

        $.ajax({
            url: '/admin/community/idea/ajaxSaveFeedback', // 실제 컨트롤러 매핑 주소
            type: 'POST',
            data: $('#ideaDetailForm').serialize(),
            success: function(res) {
                if(res.success) {
                    alert("성공적으로 저장되었습니다.");
                    location.href = '/admin/idea/list';
                } else {
                    alert("오류 발생: " + res.message);
                }
            },
            error: function() {
                alert("서버 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 목록으로 이동
    $('#btnGoList').on('click', function() {
        location.href = '/admin/community/idea/list';
    });
});
</script>
</body>
</html>