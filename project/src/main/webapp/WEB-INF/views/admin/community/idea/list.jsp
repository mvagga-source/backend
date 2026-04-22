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
    <title>아이디어 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>
<div class="admin-wrapper">
    <div class="admin-container">
        <div class="page-header-title">
            <h2>아이디어 관리</h2>
            <p>제안된 아이디어를 검토하고 관리합니다.</p>
        </div>

        <div class="dashboard-card search-area">
            <form id="searchForm" onsubmit="return false;">
                <div class="card-header">
                    <span class="title">아이디어 검색 필터</span>
                </div>
                <div class="filter-container">
                    <div class="filter-row">
                        <div class="filter-group" style="flex: 1.5;">
                            <label>검색어</label>
                            <div class="input-combined">
                                <select name="category" style="width: 130px;">
                                    <option value="">전체</option>
                                    <option value="ideatitle">제목</option>
                                    <option value="ideacontent">내용</option>
                                    <option value="member">작성자</option>
                                </select>
                                <input type="text" name="search" id="search" placeholder="검색어를 입력하세요">
                            </div>
                        </div>
                        <div class="filter-group">
                            <label>카테고리</label>
                            <select name="ideacategory" id="ideacategory" style="width: 100%;">
                                <option value="">전체</option>
                                <option value="기능 제안">기능 제안</option>
                                <option value="신규 컨텐츠">신규 컨텐츠</option>
                                <option value="UI/UX 개선">UI/UX 개선</option>
                                <option value="이벤트 아이디어">이벤트 아이디어</option>
                                <option value="기타">기타</option>
                            </select>
                        </div>
                        <div class="filter-group" style="flex: 2;">
                            <label>등록일자</label>
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
                <span class="title">아이디어 목록</span>
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
var finalParams = {};

$(document).ready(function() {
    // 1. 그리드 데이터 소스 설정
    const dataSource = {
        api: {
            readData: { 
                url: '/admin/community/idea/ajaxList', // 컨트롤러 URL 확인 필요
                method: 'GET' 
            }
        },
        initialRequest: false
    };

    // 2. 컬럼 정의 (IdeaDto 필드 매핑)
    const columns = [
        { header: '번호', name: 'ideano', width: 60, align: 'center', sortable: true, hidden: true },
        { header: '카테고리', name: 'ideacategory', width: 100, align: 'center', sortable: true },
        { header: '제목', name: 'ideatitle', minWidth: 300, sortable: true },
        { 
            header: '작성자', 
            name: 'member', 
            width: 120, 
            align: 'center',
            formatter: (ev) => {
                if (ev.value && ev.value.name) {
                    return ev.value.name; // MemberDto의 이름 필드명에 맞춰 수정 (nickname 등)
                }
                return ev.value ? ev.value.nickname : '미확인';
            } 
        },
        { 
            header: '등록일', 
            name: 'crdt', 
            width: 150, 
            align: 'center', 
            sortable: true,
            formatter: (ev) => {
                if(ev.value) {
                	return ev.value ? new Date(ev.value).toISOString().replace('T', ' ').substring(0, 19) : '';
                }
                return '';
            }
        },
        { 
            header: '상세보기', 
            name: 'detailBtn', 
            width: 100, 
            align: 'center',
            formatter: () => '<button type="button" class="btn-grid-edit" style="cursor: pointer;">상세보기</button>'
        }
    ];

    // 3. 그리드 초기화
    grid = new GridManager('grid-container', { 
        data: dataSource, 
        columns: columns,
    });

    // 4. 검색 로직
    function executeSearch() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchForm').serialize()));
        finalParams = { ...formData, perPage: parseInt($('#perPage').val()) };
        grid.grid.readData(1, finalParams, false);
    }

    $('#btnSearch').on('click', executeSearch);
    
    $('#perPage').on('change', function() {
        grid.grid.setPerPage(parseInt(this.value));
    });
    
    // 5. 서버 응답 처리 (전체 개수 업데이트)
    grid.grid.on('successResponse', (ev) => {
        const res = JSON.parse(ev.xhr.responseText);
        const total = res?.data?.pagination?.totalCount ?? 0;
        $('#totalCnt').text(total);
    });

    // 6. 상세 페이지 이동 이벤트
    grid.grid.on('click', (ev) => {
        const { columnName, rowKey } = ev;
        if (columnName === 'detailBtn' && rowKey !== undefined) {
            const rowData = grid.grid.getRow(rowKey);
            // 상세 페이지 이동 URL (ideano 전달)
            location.href = '/admin/community/idea/view?ideano=' + rowData.ideano;
        }
    });

    executeSearch(); // 페이지 진입 시 첫 데이터 로드
});
</script>
</body>
</html>