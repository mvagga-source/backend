<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link href="<c:url value='/css/notice/list.css'/>" rel="stylesheet">
<link href="<c:url value='/css/qna/view.css'/>" rel="stylesheet">
<script src="<c:url value='/js/jquery.min.js'/>"></script>

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
                            <label>신고자(닉네임)</label>
                            <input type="text" value="${report.member.nickname}" readonly class="readonly-input">
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
                            <strong>신고 대상 고유번호:</strong> ${report.targetId} 
                            <span style="margin-left: 20px;"><strong>대상:</strong> ${report.targetIdName == 'bno' ? '게시글(bno)' : ''}${report.targetIdName == 'cno' ? '댓글(cno)' : ''}</span>
                        </div>
                    </div>
                    
                    <div class="filter-group" style="margin-top: 20px;">
                        <label>신고 사유 (요약)</label>
                        <input type="text" value="${report.reason}" readonly class="readonly-input" style="width: 100%;">
                    </div>

                    <div class="filter-group">
                        <label>상세 신고 내용</label>
                        <div class="readonly-content-box" style="min-height: 150px;">${report.reasonContent}</div>
                    </div>

                    <div class="filter-group">
                        <label>증거 파일 첨부</label>
                        <div class="file-download-area">
                            <c:choose>
                                <c:when test="${not empty report.repofile}">
                                    <a href="/admin/download/download?fileName=${report.repofile}" class="btn-action btn-add" target="_blank" style="text-decoration: none; display: inline-flex; align-items: center; gap: 5px;">
                                        <span>📂</span> ${report.repofile} 다운로드
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
            
            <div class="target-content-preview" style="margin-top: 20px;">
			    <label style="font-weight: bold; color: #409eff;">🚨 신고 대상 원문 확인</label>
			    <div id="originContentArea" style="background: #fff; border: 1px solid #dcdfe6; padding: 20px; border-radius: 8px; margin-top: 10px;">
			        </div>
			    
			    <div style="margin-top: 10px; text-align: right;">
			        <button type="button" id="btnOpenOrigin" class="btn-action" style="display:none; background-color: #67c23a; color: white; border: none; padding: 5px 15px; border-radius: 4px; cursor: pointer;">
			            원문 게시글 보기 (새 창)
			        </button>
			    </div>
			</div>

            <div class="dashboard-card" style="border: 1.5px solid #1a2c4e; margin-top: 30px;">
			    <div class="card-header" style="background-color: #f8f9fc;">
			        <span class="title" style="color: #409eff;">🔧 관리자 처리</span>
			    </div>
			    
			    <div class="filter-container">
			        <div class="manual-search-box">
			            <p style="margin-bottom: 12px; font-size: 13px; color: #666;">
			                <i class="icon">ℹ️</i> 시스템 매핑이 누락되었거나 URL로만 접수된 경우, 아래에 직접 번호를 입력하여 원문을 확인할 수 있습니다.
			            </p>
			            <div style="display: flex; align-items: center; gap: 15px; flex-wrap: wrap;">
			                <div>
			                    <label>대상 구분</label>
			                    <select id="editTargetIdName" name="targetIdName" class="input-styled">
			                        <option value="bno" ${report.targetIdName == 'bno' ? 'selected' : ''}>게시글(bno)</option>
			                        <option value="cno" ${report.targetIdName == 'cno' ? 'selected' : ''}>댓글(cno)</option>
			                    </select>
			                </div>
			                <div>
			                    <label>고유번호</label>
			                    <input type="number" id="editTargetId" name="targetId" value="${report.targetId}" class="input-styled" style="width: 120px;" placeholder="번호 입력">
			                </div>
			                <!-- <button type="button" id="btnManualCheck" class="btn-action" style="background-color: #409eff; color: white; border: none; padding: 7px 15px; border-radius: 4px; cursor: pointer;">
			                    원문 다시 불러오기
			                </button> -->
			            </div>
			        </div>
			
			        <hr style="border: 0; border-top: 1px solid #eee; margin: 25px 0;">
			
			        <div class="filter-group">
			            <label style="display: block; margin-bottom: 10px; font-weight: bold; color: #333;">최종 처리 결과 선택</label>
			            <select name="status" id="processStatus" class="input-styled" style="width: 200px; height: 40px; font-size: 14px;">
			                <option value="신고대기" ${report.status == '신고대기' ? 'selected' : ''}>신고대기 (검토중)</option>
			                <option value="처리완료" ${report.status == '처리완료' ? 'selected' : ''}>처리완료 (블라인드/삭제)</option>
			                <option value="신고반려" ${report.status == '신고반려' ? 'selected' : ''}>신고반려 (정상게시물)</option>
			            </select>
			            <p style="font-size: 12px; color: #909399; margin-top: 8px;">* '처리완료' 시 해당 게시물은 즉시 사용자 화면에서 숨겨집니다.</p>
			        </div>
			    </div>
			</div>

            <div class="detail-btn-area">
                <button type="button" class="btn-action btn-add" id="btnUpdateStatus" style="cursor: pointer; border-radius: 4px; padding: 0 40px; height: 45px; font-size: 15px;">처리 상태 저장</button>
                <button type="button" class="btn-action btn-delete-server" id="btnGoList" style="cursor: pointer; padding: 0 40px; height: 45px; font-size: 15px; background-color: #909399;">목록으로</button>
            </div>
        </form>
    </div>
</div>

<script>
$(document).ready(function() {
    // 상태 변경 저장 버튼
    $('#btnUpdateStatus').on('click', function() {
    	const status = $('#processStatus').val();
        const formData = $('#reportDetailForm').serialize();
        
        if(!confirm("해당 신고의 상태를 [" + status + "](으)로 변경하시겠습니까?")) return;

        $.ajax({
            url: '/admin/community/report/ajaxUpdateStatus', 
            type: 'POST',
            data: formData,
            success: function(res) {
                if(res.success) {
                	alert("성공적으로 처리되었습니다.");
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
    
    const targetIdName = "${report.targetIdName}"; 
    const targetId = "${report.targetId}";
    
    // 1. targetId가 없거나 0인 경우 아예 영역을 숨김
    if(!targetId || targetId === "0" || targetId === "") {
        $('.target-content-preview').hide(); 
        return; // 아래 AJAX 로직 실행 안 함
    }

    // 2. 번호가 있을 때만 원문 불러오기 실행
    $.ajax({
        url: '/admin/community/report/ajaxGetOrigin',
        type: 'GET',
        data: { targetIdName: targetIdName, targetId: targetId },
        success: function(res) {
            if(res.success && res.data) {
                let html = "";
                if(targetIdName === 'bno') {
                    html = `<div class="origin-post">
                                <h3 style="margin-bottom:10px;">[제목] \${res.data.btitle}</h3>
                                <div style="border-top: 1px solid #eee; padding-top:10px;">\${res.data.bcontent}</div>
                            </div>`;
                    \$('#btnOpenOrigin').attr('onclick', "window.open('http://${serverHost}:3000/Community/BoardView/" + targetId + "', '_blank')").show();
                } else if(targetIdName === 'cno') {
                    html = `<div class="origin-comment" style="background:#f9f9f9; padding:15px; border-radius:5px;">
                                <p><strong>작성자:</strong> \${res.data.member?.nickname || res.data.member?.id}</p>
                                <p style="margin-top:10px;"><strong>댓글 내용:</strong></p>
                                <div style="background:#fff; border:1px solid #ddd; padding:10px; margin-top:5px;">\${res.data.ccontent}</div>
                            </div>`;
                    if(res.data.board && res.data.board.bno) {
                        $('#btnOpenOrigin').attr('onclick', "window.open('http://${serverHost}:3000/Community/BoardView/" + res.data.board.bno + "', '_blank')").show();
                    }
                }
                $('#originContentArea').html(html);
                $('.target-content-preview').show(); // 데이터가 로드되면 확실히 보여줌
            } else {
                // 번호는 있는데 데이터가 없는 경우 (이미 삭제된 글 등)
                $('#originContentArea').html("<p style='color:red;'>조회 가능한 원문이 없습니다. (이미 삭제되었습니다.)</p>");
                $('.target-content-preview').show();
            }
        },
        error: function() {
            // 서버 에러 시 영역 숨김 처리
            $('.target-content-preview').hide();
        }
    });
});
</script>
</body>
</html>