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
    <title>신고 내역 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
    <div class="admin-container">
        <div class="page-header-title">
            <h2>신고 내역 관리</h2>
            <p>사용자들이 접수한 신고 내역을 검토하고 처리 상태를 변경합니다.</p>
        </div>

        <div class="dashboard-card search-area">
            <form id="searchForm" onsubmit="return false;">
                <div class="card-header">
                    <span class="title">신고 검색 필터</span>
                </div>
                <div class="filter-container">
                    <div class="filter-row">
                        <div class="filter-group" style="flex: 1.5;">
                            <label>검색어</label>
                            <div class="input-combined">
                                <select name="category" style="width: 130px;">
                                    <option value="">전체</option>
                                    <option value="reason">신고사유</option>
                                    <option value="mid">신고자</option>
                                </select>
                                <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
                            </div>
                        </div>
                        <div class="filter-group">
                            <label>신고 유형</label>
                            <select name="reportType" id="reportType" style="width: 100%;">
                                <option value="">전체</option>
                                <option value="비방/욕설">비방/욕설</option>
                                <option value="명예훼손/루머">명예훼손/루머</option>
                                <option value="사생활 침해">사생활 침해</option>
                                <!-- <option value="도배/스팸">도배/스팸</option> -->
                                <option value="부적절한 콘텐츠">부적절한 콘텐츠</option>
                                <option value="성희롱">성희롱</option>
                                <option value="기타">기타</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>처리 상태</label>
                            <select name="status" id="status" style="width: 100%;">
                                <option value="">전체</option>
                                <option value="신고대기">신고대기</option>
                                <option value="처리완료">처리완료</option>
                                <option value="신고반려">신고반려</option>
                            </select>
                        </div>
                    </div>
                    <div class="filter-row">
                        <div class="filter-group" style="flex: 2;">
                            <label>신고일자</label>
                            <div class="input-combined">
                                <input type="date" name="startDate" id="startDate">
                                <span class="txt-dash">~</span>
                                <input type="date" name="endDate" id="endDate">
                            </div>
                        </div>
                        <div class="filter-btns" style="flex: 1; text-align: right; padding-top: 25px;">
                            <button type="button" id="btnSearch" class="btn-search">검색하기</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div class="dashboard-card grid-area">
            <div class="card-header grid-header">
                <span class="title">신고 목록</span>
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

<script>
let grid;

$(document).ready(function() {
    const dataSource = {
        api: {
            readData: { 
                url: '/admin/community/report/ajaxList', method: 'GET' 
            }
        },
        initialRequest: false
    };

    const columns = [
        { header: '번호', name: 'repono', width: 60, align: 'center', sortable: true, hidden: true },
        { header: '유형', name: 'reportType', width: 120, align: 'center', sortable: true },
        { header: '대상구분', name: 'targetType', width: 100, align: 'center' },
        { header: '신고 사유', name: 'reason', minWidth: 250, sortable: true },
        { 
            header: '신고자', 
            name: 'member', 
            width: 120, 
            align: 'center',
            formatter: (ev) => ev.value ? (ev.value.nickname || ev.value.id) : '미확인'
        },
        { 
            header: '상태', 
            name: 'status', 
            width: 100, 
            align: 'center',
            formatter: (ev) => {
                let color = '#909399';
                if(ev.value === '신고대기') color = '#f56c6c';
                if(ev.value === '처리완료') color = '#67c23a';
                return `<span style="color: \${color}; font-weight: bold;">\${ev.value}</span>`;
            }
        },
        { 
            header: '신고일', 
            name: 'crdt', 
            width: 160, 
            align: 'center', 
            sortable: true,
            formatter: (ev) => ev.value ? new Date(ev.value).toISOString().replace('T', ' ').substring(0, 19) : ''
        },
        { 
            header: '상세보기', 
            name: 'detailBtn', 
            width: 100, 
            align: 'center',
            formatter: () => '<button type="button" class="btn-grid-edit" style="cursor: pointer;">상세보기</button>'
        }
    ];

    grid = new GridManager('grid-container', { 
        data: dataSource, 
        columns: columns,
    });

    function executeSearch() {
        // 폼 데이터를 객체로 명시적 변환
        const params = {
            category: $('select[name="category"]').val(),
            search: $('#search').val(),
            reportType: $('#reportType').val(),
            status: $('#status').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val(),
            perPage: parseInt($('#perPage').val())
        };
        grid.grid.readData(1, params, true);
    }

    $('#btnSearch').on('click', executeSearch);
    
    $('#perPage').on('change', function() {
        grid.grid.setPerPage(parseInt(this.value));
        executeSearch();
    });
    
    grid.grid.on('successResponse', (ev) => {
        const res = JSON.parse(ev.xhr.responseText);
        $('#totalCnt').text(res?.data?.pagination?.totalCount ?? 0);
    });

    grid.grid.on('click', (ev) => {
        const { columnName, rowKey } = ev;
        if (columnName === 'detailBtn' && rowKey !== undefined) {
            const rowData = grid.grid.getRow(rowKey);
            location.href = '/admin/community/report/view?repono=' + rowData.repono;
        }
    });

    executeSearch(); 
});
</script>
</body>
</html>