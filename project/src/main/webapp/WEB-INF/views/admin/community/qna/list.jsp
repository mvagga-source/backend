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
            <h2>QnA 관리</h2>
            <p>사용자 문의 내역을 확인하고 답변을 등록합니다.</p>
        </div>

        <div class="dashboard-card search-area">
		    <form id="searchForm" onsubmit="return false;">
		        <div class="card-header">
		            <span class="title">QnA 검색 필터</span>
		        </div>
		        <div class="filter-container">
		            <div class="filter-row">
		                <div class="filter-group" style="flex: 1.5;">
		                    <label>검색어</label>
		                    <div class="input-combined">
		                        <select name="category" style="width: 130px;">
		                            <option value="">전체</option>
		                            <option value="qtitle">제목</option>
		                            <option value="qcontent">내용</option>
		                            <option value="mid">작성자</option>
		                        </select>
		                        <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
		                    </div>
		                </div>
		                <div class="filter-group">
		                    <label>답변 상태</label>
		                    <select name="status" id="status" style="width: 100%;">
		                        <option value="">전체</option>
		                        <option value="답변대기">답변대기</option>
		                        <option value="답변완료">답변완료</option>
		                    </select>
		                </div>
		                <div class="filter-group" style="flex: 2;">
		                    <label>등록일자 (작성일)</label>
		                    <div class="input-combined">
		                        <input type="date" name="startDate" id="startDate">
		                        <span class="txt-dash">~</span>
		                        <input type="date" name="endDate" id="endDate">
		                    </div>
		                </div>
		                <div class="filter-btns" style="flex: 0.5; text-align: right;">
		                    <button type="button" id="btnSearch" class="btn-search">검색하기</button>
		                </div>
		            </div>
		        </div>
		    </form>
		</div>

        <div class="dashboard-card grid-area">
            <div class="card-header grid-header">
                <span class="title">문의 목록</span>
                <div class="control-right">
                    <!-- <button id="btnServerDelete" class="btn-action btn-delete-server">삭제</button> -->
                </div>
            </div>
            <div class="grid-control-bar-custom">
                <div class="control-left">
                    <div class="total-info">전체 <strong id="totalCnt">0</strong>건</div>
                </div>
                <div class="control-right">
                    <div class="select-wrapper">
                        <select id="perPage" class="select-custom">
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>
                            <option value="100">100</option>
                        </select>
                    </div>
                </div>
            </div>
            <div id="grid-container"></div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/views/admin/notice/list/noticePop.jsp" %>
<script>
//전역 변수로 선언하여 외부 함수에서도 접근 가능하게 함
let grid;
var finalParams = {};

$(document).ready(function() {
    const dataSource = {
        api: {
            readData: { url: '/admin/community/qna/ajaxList', method: 'GET' }
        },
        initialRequest: false
    };

    const columns = [
    	/*{ 
    	    header: '번호', 
    	    name: 'rowNum', 
    	    width: 60, 
    	    align: 'center',
    	},*/
        { header: '번호', name: 'qno', width: 60, align: 'center', sortable: true, hidden: true },
        { header: '제목', name: 'qtitle', minWidth: 300, sortable: true },
        { 
            header: '상태', 
            name: 'status', 
            width: 100, 
            align: 'center',
            sortable: true,
        },
        { header: '작성자', name: 'member', width: 120, align: 'center', sortable: true,
        	formatter: (ev) => {
                // ev.value는 MemberDto 객체입니다.
                if (ev.value && ev.value.nickname) {
                    return ev.value.nickname;
                }
                // 객체가 없거나 닉네임이 없을 경우 ID를 보여주거나 '미확인' 처리
                return ev.value ? ev.value.id : '미확인';
            } },
        { header: '등록일', name: 'crdt', width: 150, align: 'center', sortable: true, formatter: (ev) => {
            if(ev.value) {
            	return ev.value ? new Date(ev.value).toISOString().replace('T', ' ').substring(0, 19) : '';
            }
            return '';
        } },
        { 
            header: '상세보기', 
            name: 'detailBtn', 
            width: 100, 
            align: 'center',
            formatter: () => '<button type="button" class="btn-grid-edit" style="cursor: pointer;">상세/답변</button>'
        }
    ];

    grid = new GridManager('grid-container', { 
        data: dataSource, 
        columns: columns,
    });

    // 검색/이벤트 로직
    function executeSearch() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchForm').serialize()));
        finalParams = { ...formData, perPage: parseInt($('#perPage').val()) };
        grid.grid.readData(1, finalParams, false);
    }

    $('#btnSearch').on('click', executeSearch);
    
    $('#perPage').on('change', function() {
    	grid.grid.setPerPage(parseInt(this.value));
        //executeSearch();
    });
    
	//그리드 성공후 처리
    grid.grid.on('successResponse', (ev) => {
		const res = JSON.parse(ev.xhr.responseText);
		const total = res?.data?.pagination?.totalCount ?? 0;
		$('#totalCnt').text(total);
    });

    // 상세 버튼 클릭 시 모달 오픈
    grid.grid.on('click', (ev) => {
        const { columnName, rowKey } = ev;
        if (columnName === 'detailBtn' && rowKey !== undefined) {
            const rowData = grid.grid.getRow(rowKey);
			//상세페이지 URL로 이동 (qno 전달)
            location.href = '/admin/community/qna/view?qno=' + rowData.qno;
        }
    });

    // 서버 삭제
    $('#btnServerDelete').on('click', function() {
       if(confirm("선택한 문의를 삭제하시겠습니까?")) {
           grid.deleteCheckedRows('/admin/qna/ajaxDelete', 'qno', finalParams);
       }
    });
    
    executeSearch();	//첫 검색
});
</script>
</body>
</html>