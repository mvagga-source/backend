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
<script src="<c:url value='/js/chart.js'/>"></script>
<link href="<c:url value='/css/goods/list.css'/>" rel="stylesheet">
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>굿즈 관리</title>
    </head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

<div class="admin-wrapper">
<div class="admin-container">
    <div class="page-header-title">
        <h2>굿즈 관리</h2>
    </div>
    <%@ include file="/WEB-INF/views/admin/goods/list/summary.jsp" %>
    <div class="dashboard-card search-area">
        <form id="searchForm" onsubmit="return false;">
            <div class="card-header">
                <span class="title">상세 검색 필터</span>
            </div>
            <div class="filter-container">
                <div class="filter-row">
                	<div class="filter-group">
                        <label>검색어</label>
                        <div class="input-combined">
                            <select name="category" id="category" style="width: 130px;">
                                <option value="">전체</option>
                                <option value="orderId">주문번호</option>
                                <option value="gname">상품명</option>
                                <option value="buyerName">구매자명</option>
                                <option value="sellerName">판매자명</option>
                            </select>
                            <input type="text" id="search" name="search" placeholder="검색어를 입력하세요">
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>결제상태</label>
                        <select name="status" id="status">
                            <option value="">전체</option>
                            <option value="PAID">결제완료</option>
                            <option value="READY">결제대기</option>
                            <option value="CANCEL">결제취소</option>
                            <option value="FAILED">결제실패</option>
                        </select>
                    </div>
                    <div class="filter-group">
					    <label>정산 여부</label>
					    <select name="settleYn" id="settleYn">
					        <option value="">전체</option>
					        <option value="n">정산대기</option>
					        <option value="y">정산완료</option>
					    </select>
					</div>
					<div class="filter-group">
					    <label>배송상태</label>
					    <select name="delivStatus" id="delivStatus">
					        <option value="">전체</option>
					        <option value="배송대기">배송대기</option>
					        <option value="배송준비중">배송준비중</option>
					        <option value="배송중">배송중</option>
					        <option value="배송완료">배송완료</option>
					        <option value="구매확정">구매확정</option>
					        <option value="반품/교환">반품/교환</option>
					    </select>
					</div>
                </div>
                <div class="filter-row">
                    <div class="filter-group">
                        <label>조회기간</label>
                        <div class="input-combined">
                            <input type="date" name="startDate" id="startDate">
                            <span class="txt-dash">~</span>
                            <input type="date" name="endDate" id="endDate">
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>금액범위</label>
                        <div class="input-combined">
                            <input type="number" name="minPrice" value="0">
                            <span class="txt-dash">~</span>
                            <input type="number" name="maxPrice" placeholder="무제한">
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
            <span class="title">주문 내역 목록</span>
            <button type="button" id="btnSave" class="btn-save">저장</button>
        </div>
        
        <div class="grid-control-bar">
            <div class="control-left">
            	<div class="total-info">전체 <strong id="totalCnt">0</strong>건</div>
            </div>
            <div class="control-right">
                <select id="sortDir" class="select-sm">
                    <option value="crdt_desc">최신순</option>
                    <option value="crdt_asc">오래된순</option>
                    <option value="price_desc">금액높은순</option>
					<option value="price_asc">금액낮은순</option>
                    <option value="rating_desc">평점높은순</option>
                    <option value="rating_asc">평점낮은순</option>
					<option value="helpful_desc">도움돼요많은순</option>
					<option value="helpful_asc">도움돼요적은순</option>
                </select>
                <select id="perPage" class="select-sm">
                    <option value="10">10</option>
                    <option value="20">20</option>
                    <option value="50">50</option>
                    <option value="100">100</option>
                </select>
            </div>
        </div>

        <div id="grid-container"></div>
    </div>

    <%-- <%@ include file="/WEB-INF/views/admin/goods/list/banner.jsp" %> --%>
</div>
</div>
</body>
</html>
<script>
var finalParams = {};
function priceFormatter({value}) {
    return (value || 0).toLocaleString() + '원';
}
$(document).ready(function() {
	function orderStatusFormatter({value}) {
	    const map = {
	        PAID: '결제완료',
	        READY: '결제대기',
	        CANCEL: '결제취소',
	        FAILED: '결제실패'
	    };
	    return map[value] || value;
	}
	
	// 정산여부 매핑
	function settleYnFormatter({value}) {
	    const map = {
	        y: '정산완료',
	        n: '정산대기'
	    };
	    return map[value] || value;
	}
	
    // 그리드 생성
    var data = {
    	api: {
	    	readData: { url: '/admin/orders/ajaxList', method: 'GET' },
	    	modifyData: { url: '/admin/orders/ajaxModify', method: 'POST', contentType: 'application/json' }
	    }
    	,initialRequest: false
    };
    const columns = [
        { header: '번호', name: 'gono', align: 'center', hidden:true, },
        { header: '주문번호', name: 'orderId', align: 'center' },
        { header: '상품명', name: 'gname' },
        { header: '판매자', name: 'sellerName' },
        { header: '구매자', name: 'buyerName' },
        { header: '결제금액', name: 'totalPrice', formatter: priceFormatter },
        { header: '수수료', name: 'fee', formatter: priceFormatter },
        { header: '정산금액', name: 'settleAmount', formatter: priceFormatter },
        { header: '결제상태', name: 'orderStatus', formatter: orderStatusFormatter },
        { header: '배송상태', name: 'delivStatus', editor: { 
            type: 'select', 
            options: { 
                listItems: [
                    { text: '배송대기', value: '배송대기' },
                    { text: '배송준비중', value: '배송준비중' },
                    { text: '배송중', value: '배송중' },
                    { text: '배송완료', value: '배송완료' },
                    { text: '구매확정', value: '구매확정' },
                    { text: '반품/교환', value: '반품/교환' }
                ] 
            } 
        } },
        { header: '정산여부', name: 'settleYn', formatter: settleYnFormatter, editor: { 
            type: 'select', 
            options: { 
                listItems: [
                    { text: '정산대기', value: 'n' },
                    { text: '정산완료', value: 'y' },
                ] 
            } 
        } },
        { header: '평점', name: 'avgRating' },
        { header: '리뷰수', name: 'reviewCnt' },
        { header: '주문일시', name: 'orderDate' }
    ];
    options={ data: data, columns: columns };
    const grid = new GridManager('grid-container', options);

	// 검색 실행 함수
    function executeSearch() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchForm').serialize()));
        const extraParams = {
        	sortDir: $('#sortDir').val(),
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
    
	// 저장 버튼 이벤트 연결
    $('#btnSave').on('click', function() {
        //if(confirm('변경사항을 저장하시겠습니까?')) {
            grid.save();
        //}
    });

    //그리드 성공후 처리
    grid.grid.on('successResponse', (ev) => {
		// 응답 URL에 ajaxModify가 포함되어 있는지 확인
        if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1) {
            const res = JSON.parse(ev.xhr.responseText);
            if (res.result || res.success) { // 서버 응답 구조에 맞게 체크
                alert('저장되었습니다.');
                grid.grid.readData(1, finalParams, false); // 재조회
            } else {
                alert(res.message || '저장 중 오류가 발생했습니다.');
            }
        } else {
            // 기존 조회 성공 처리
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCnt').text(total);
        }
    });
    
    executeSearch();
});
</script>