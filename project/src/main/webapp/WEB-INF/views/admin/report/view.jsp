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
    <title>신고 상세 관리</title>
    <style>
        .status-badge { padding: 4px 12px; border-radius: 4px; font-weight: bold; font-size: 13px; }
        .status-wait { background-color: #fef0f0; color: #f56c6c; border: 1px solid #fde2e2; }
        .status-complete { background-color: #f0f9eb; color: #67c23a; border: 1px solid #e1f3d8; }
        .target-info-box { background-color: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 5px solid #409eff; margin-top: 10px; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
    <div class="admin-container">
        <div class="page-header-title">
            <h2>신고 상세 조회</h2>
            <p>접수된 신고 내용을 확인하고 처리 상태를 관리합니다.</p>
        </div>

        <form id="reportDetailForm">
            <input type="hidden" name="repono" value="${report.repono}">

            <div class="dashboard-card">
                <div class="card-header">
                    <span class="title">신고 접수 정보</span>
                    <div class="control-right">
                        <span class="total-info" style="margin-right: 15px;">상태: 
                            <span class="status-badge ${report.status == '신고대기' ? 'status-wait' : 'status-complete'}">${report.status}</span>
                        </span>
                        <span class="total-info">신고일: <strong><fmt:formatDate value="${report.crdt}" pattern="yyyy-MM-dd HH:mm:ss"/></strong></span>
                    </div>
                </div>
                
                <div class="filter-container">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label>신고자(닉네임/ID)</label>
                            <input type="text" value="${report.member.nickname} (${report.member.id})" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>신고 유형</label>
                            <input type="text" value="${report.reportType}" readonly class="readonly-input">
                        </div>
                        <div class="filter-group">
                            <label>신고 대상 구분</label>
                            <input type="text" value="${report.targetType}" readonly class="readonly-input" style="color: #409eff; font-weight: bold;">
                        </div>
                    </div>

                    <div class="target-info-box">
                        <div style="font-size: 14px; color: #606266;">
                            <strong>신고 대상 고유번호(PK):</strong> ${report.targetId} 
                            <span style="margin-left: 20px;"><strong>대상 컬럼명:</strong> ${report.targetIdName}</span>
                        </div>
                    </div>
                    
                    <div class="filter-group" style="margin-top: 20px;">
                        <label>신고 사유 (요약)</label>
                        <input type="text" value="${report.reason}" readonly class="readonly-input" style="width: 100%;">
                    </div>

                    <div class="filter-group">
                        <label>상세 신고 내용</label>
                        <div class="readonly-content-box" style="min-height: 150px;">
                            ${report.reasonContent}
                        </div>
                    </div>

                    <div class="filter-group">
                        <label>증거 파일 첨부</label>
                        <div class="file-download-area">
                            <c:choose>
                                <c:when test="${not empty report.repofile}">
                                    <a href="${hostUrl}${report.repofile}" class="btn-action btn-add" target="_blank" style="text-decoration: none; display: inline-flex; align-items: center; gap: 5px;">
                                        <span>📂</span> ${report.repofile} (파일 보기/다운로드)
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #909399; font-size: 14px;">첨부된 증거 파일이 없습니다.</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <div class="dashboard-card" style="border: 1.5px solid #1a2c4e; margin-top: 30px;">
                <div class="card-header" style="background-color: #f8f9fc;">
                    <span class="title" style="color: #409eff;">관리자 처리</span>
                </div>
                <div class="filter-container">
                    <div class="filter-group">
                        <label>처리 상태 변경</label>
                        <select name="status" id="processStatus" style="width: 200px; padding: 8px;">
                            <option value="신고대기" ${report.status == '신고대기' ? 'selected' : ''}>신고대기</option>
                            <option value="처리완료" ${report.status == '처리완료' ? 'selected' : ''}>처리완료</option>
                            <option value="신고반려" ${report.status == '신고반려' ? 'selected' : ''}>신고반려</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="detail-btn-area">
                <button type="button" class="btn-action btn-add" id="btnUpdateStatus" style="padding: 0 40px; height: 45px; font-size: 15px;">처리 상태 저장</button>
                <button type="button" class="btn-action btn-delete-server" id="btnGoList" style="padding: 0 40px; height: 45px; font-size: 15px; background-color: #909399;">목록으로</button>
            </div>
        </form>
    </div>
</div>

<script>
$(document).ready(function() {
    // 상태 변경 저장 버튼
    $('#btnUpdateStatus').on('click', function() {
        const status = $('#processStatus').val();
        
        if(!confirm("해당 신고의 상태를 [" + status + "](으)로 변경하시겠습니까?")) return;

        $.ajax({
            url: '/admin/community/report/ajaxUpdateStatus', 
            type: 'POST',
            data: {
                repono: $('input[name="repono"]').val(),
                status: status
            },
            success: function(res) {
                if(res.success) {
                    alert("처리 상태가 변경되었습니다.");
                    location.reload();
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
        location.href = '/admin/community/report/list';
    });
});
</script>
</body>
</html>