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
    <title>공지사항 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
<div class="admin-container">
	<div class="page-header-title">
        <h2>공지사항 관리</h2>
    </div>
    <div class="dashboard-card search-area">
        <form id="searchForm" onsubmit="return false;">
            <div class="card-header">
                <span class="title">공지사항 검색 필터</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group">
                        <label>검색어</label>
                        <div class="input-combined">
                            <select name="category" style="width: 130px;">
                                <option value="">전체</option>
                                <option value="ntitle">제목</option>
                                <option value="ncontent">내용</option>
                            </select>
                            <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
                        </div>
                    </div>
                    <div class="filter-group" style="flex: 2;">
				        <label>노출 게시 기간</label>
				        <div class="input-combined">
				            <input type="date" name="startDate" id="startDate">
				            <span class="txt-dash">~</span>
				            <input type="date" name="endDate" id="endDate">
				        </div>
				    </div>
                    <div class="filter-btns">
                        <button type="button" id="btnSearch" class="btn-search">검색하기</button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <div class="dashboard-card grid-area">
        <div class="card-header grid-header">
            <span class="title">공지사항 목록</span>
            <div class="control-right">
            	<!-- <button type="button" onclick="location.href='/admin/notice/write'" class="btn-save" style="background:#1a2c4e; margin-left:10px;">공지 등록</button> -->
	            <button id="btnSaveGrid" class="btn-action btn-save-main">
	                저장
	            </button>
	            <button id="btnServerDelete" class="btn-action btn-delete-server">
	                삭제
	            </button>
            </div>
        </div>
        <div class="grid-control-bar-custom">
	        <div class="control-left">
	        	<div class="total-info">전체 <strong id="totalCnt">0</strong>건</div>
	        </div>
	        
	        <div class="control-right">
	        	<button id="btnAddRow" class="btn-action btn-add">
	                행 추가
	            </button>
	            <button id="btnRemoveRow" class="btn-action btn-remove">
	                행 삭제
	            </button>
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

// 1. 팝업 제어 함수 (전역 범위로 배치)
function openNoticePopup(rowKey, data) {
    document.getElementById('popRowKey').value = rowKey;
    document.getElementById('popTitle').value = data.ntitle || '';
    document.getElementById('popContent').value = data.ncontent || '';
    document.getElementById('noticePopup').style.display = 'flex';
}

function closeNoticePopup() {
    document.getElementById('noticePopup').style.display = 'none';
}

/**
 * 팝업 내용을 그리드 행에 반영
 */
function saveNoticePopup() {
    const rowKey = document.getElementById('popRowKey').value;
    const title = document.getElementById('popTitle').value;
    const content = document.getElementById('popContent').value;

    if (!rowKey) return;

    // 전역 변수 grid 사용
    grid.grid.setValue(rowKey, 'ntitle', title);
    grid.grid.setValue(rowKey, 'ncontent', content);

    alert('그리드에 반영되었습니다. 최종 저장은 저장 버튼을 눌러주세요.');
    closeNoticePopup();
}

$(document).ready(function() {
    // 그리드 초기화
    const data = {
        api: {
            readData: { url: '/admin/notice/ajaxList', method: 'GET' },
            modifyData: { url: '/admin/notice/ajaxModify', method: 'POST', contentType: 'application/json' }
        },
        initialRequest: false
    };

    const columns = [
        { header: '번호', name: 'nno', width: 80, align: 'center', sortable: true, hidden: true },
        { header: '제목', name: 'ntitle', minWidth: 300, editor: 'text', sortable: true, whiteSpace: 'normal' },
        { header: '내용', name: 'ncontent', minWidth: 300, hidden: true },
        { header: '노출시작일', name: 'startDate', align: 'center', editor: {
            type: 'datePicker',
            options: {
                format: 'yyyy-MM-dd HH:mm:00',
                timepicker: true, // 시분초 포함
            }
        }, sortable: true },
        { header: '노출종료일', name: 'endDate', align: 'center', editor: {
            type: 'datePicker',
            options: {
                format: 'yyyy-MM-dd HH:mm:00',
                timepicker: true, // 시분초 포함
            }
        }, sortable: true },
        { 
            header: '팝업편집', 
            name: 'editBtn', 
            width: 100, 
            align: 'center',
            formatter: () => '<button type="button" class="btn-grid-edit">편집</button>'
        },
        { header: '등록일', name: 'crdt', align: 'center', sortable: true, formatter: (ev) => {
            if(ev.value) {
            	return ev.value ? new Date(ev.value).toISOString().replace('T', ' ').substring(0, 19) : '';
            }
            return '';
        } }
    ];
    options={ data: data, columns: columns };
    grid = new GridManager('grid-container', options);

	// 검색 실행 함수
    function executeSearch() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchForm').serialize()));
        const extraParams = {
        	//sortDir: $('#sortDir').val(),
            perPage: parseInt($('#perPage').val())
        };
        finalParams = { ...formData, ...extraParams };
        
        grid.grid.readData(1, finalParams, false);
    }

    // 이벤트 바인딩
    $('#btnSearch').on('click', executeSearch);

    // 정렬/개수 변경 시 자동 재조회
    $('#sortDir, #perPage').on('change', function() {
        if(this.id === 'perPage') {
            grid.grid.setPerPage(parseInt(this.value));
        }else{
        	executeSearch();
        }
    });

    //그리드 성공후 처리
    grid.grid.on('successResponse', (ev) => {
		if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1 && ev.xhr.status === 200) {
			const responseData = JSON.parse(ev.xhr.response);
            if (responseData && responseData.result === true) {
                alert('저장되었습니다.');
                grid.grid.readData(1, finalParams, false);
            }
        }else{
        	const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCnt').text(total);
        }
    });

    // 그리드 클릭 이벤트
    grid.grid.on('click', (ev) => {
        const { columnName, rowKey } = ev;
        
        // 편집 버튼 클릭 시 팝업 오픈
        if (columnName === 'editBtn' && rowKey !== undefined) {
            const rowData = grid.grid.getRow(rowKey);
            openNoticePopup(rowKey, rowData);
        }
    });
    
	// 행 추가 (새로운 데이터 입력용)
    $('#btnAddRow').on('click', function() {
        grid.appendRow({
            ntitle: '새 공지사항 제목',
            ncontent: '',
            startDate: new Date().toISOString().slice(0, 19).replace('T', ' '),
            origin: '0' // 신규 행 구분용 (필요시)
        });
    });

    // 행 삭제 (그리드 상에서 선택한 행 제거)
    $('#btnRemoveRow').on('click', function() {
        grid.rowDel();
    });

    // 저장 (서버로 modifyData 호출)
    $('#btnSaveGrid').on('click', function() {
        grid.save();
    });

    // 서버 삭제 (체크된 행들을 서버에서 삭제)
    $('#btnServerDelete').on('click', function() {
       grid.deleteCheckedRows('/admin/notice/ajaxDelete', 'nno', finalParams);
    });
    
    executeSearch();	//첫 검색
});
</script>
</body>
</html>