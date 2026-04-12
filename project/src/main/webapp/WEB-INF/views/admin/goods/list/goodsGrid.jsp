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
<div class="dashboard-card search-area">
    <form id="goodsSearchForm" onsubmit="return false;">
        <div class="card-header">
            <span class="title">굿즈 상세 검색</span>
        </div>
        <div class="filter-container">
            <div class="filter-row">
                <div class="filter-group">
                    <label>상품 검색</label>
                    <div class="input-combined">
                        <select name="category" id="searchCategory" style="width: 130px;">
                            <option value="">전체</option>
                            <option value="gname">상품명</option>
                            <option value="idolName">참가자명</option>
                            <option value="sellerName">판매자명</option>
                        </select>
                        <input type="text" name="search" placeholder="검색어를 입력하세요">
                    </div>
                </div>
                <div class="filter-group">
                    <label>판매 상태</label>
                    <select name="status">
                        <option value="">전체</option>
                        <option value="판매중">판매중</option>
                        <option value="품절">품절</option>
                        <option value="판매중지">판매중지</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label>재고 상태</label>
                    <select name="stockStatus">
                        <option value="">전체</option>
                        <option value="low">재고부족(5개 이하)</option>
                        <option value="out">품절상품</option>
                    </select>
                </div>
            </div>
            <div class="filter-row">
                <div class="filter-group">
                    <label>등록 기간</label>
                    <div class="input-combined">
                        <input type="date" name="startDate">
                        <span class="txt-dash">~</span>
                        <input type="date" name="endDate">
                    </div>
                </div>
                <div class="filter-group">
				    <label>금액 범위</label>
				    <div class="input-combined">
				        <input type="number" name="minPrice" value="0">
				        <span class="txt-dash">~</span>
				        <input type="number" name="maxPrice" placeholder="무제한">
				    </div>
				</div>
                <div class="filter-btns">
                    <button type="button" id="btnGoodsSearch" class="btn-search">검색하기</button>
                </div>
            </div>
        </div>
    </form>
</div>
<div class="dashboard-card grid-area">
    <div class="card-header grid-header">
        <span class="title">굿즈 목록</span>
        <button type="button" id="btnGoodsSave" class="btn-save">저장</button>
    </div>
    
    <div class="grid-control-bar">
        <div class="control-left">
        	<div class="total-info">전체 <strong id="totalCntGoods">0</strong>건</div>
        </div>
        <div class="control-right">
            <select id=sortDirGoods class="select-sm">
                <option value="crdt_desc">최신순</option>
                <option value="crdt_asc">오래된순</option>
                <option value="price_desc">금액높은순</option>
				<option value="price_asc">금액낮은순</option>
                <option value="rating_desc">평점높은순</option>
                <option value="rating_asc">평점낮은순</option>
				<option value="helpful_desc">도움돼요많은순</option>
				<option value="helpful_asc">도움돼요적은순</option>
            </select>
            <select id="perPageGoods" class="select-sm">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </div>
    </div>

    <div id="goods-grid-container"></div>
</div>
<script>
let goodsFinalParams = {};
$(document).ready(function() {

    const goodsStatusOptions = [
        { text: '판매중', value: '판매중' },
        { text: '품절', value: '품절' },
        { text: '판매중지', value: '판매중지' }
    ];

    const goodsColumns = [
        { header: '상품번호', name: 'gno', align: 'center', width: 80 },
        { header: '참가자명', name: 'idolName', width: 100 },
        { header: '상품명', name: 'gname', editor: 'text' },
        { header: '판매자', name: 'sellerName', width: 120 }, // sellerNickname -> sellerName 확인 필요
        { header: '판매가', name: 'price', align: 'right', formatter: priceFormatter, editor: 'text' },
        { header: '재고', name: 'stockCnt', align: 'center', width: 70, editor: 'text' },
        { 
            header: '판매상태', 
            name: 'status', 
            align: 'center',
            width: 100,
            formatter: 'listItemText',
            editor: {
                type: 'select',
                options: { listItems: goodsStatusOptions }
            }
        },
        { header: '평점', name: 'avgRating', align: 'center', width: 60 }, // 추가
        { header: '도움수', name: 'helpfulCnt', align: 'center', width: 60 }, // 추가
        { header: '등록일', name: 'orderDate', align: 'center', width: 120 }
    ];

    // 그리드 매니저 인스턴스 (ID 확인: goods-grid-container)
    const goodsGridManager = new GridManager('goods-grid-container', { 
        data: {
            api: {
                readData: { url: '/admin/goods/ajaxList', method: 'GET' },
                modifyData: { url: '/admin/goods/ajaxModify', method: 'POST', contentType: 'application/json' }
            },
            initialRequest: false
        }, 
        columns: goodsColumns,
    });

    // 검색 함수 수정 (Form 필드와 정확히 매칭)
    function executeGoodsSearch() {
        // serialize()를 사용하여 폼 내의 모든 필드(category, search, status, stockStatus 등)를 가져옴
        const formData = Object.fromEntries(new URLSearchParams($('#goodsSearchForm').serialize()));
        const extraParams = {
            sortDir: $('#sortDirGoods').val(),
            perPage: parseInt($('#perPageGoods').val())
        };
        goodsFinalParams = { ...formData, ...extraParams };
        
        goodsGridManager.grid.readData(1, goodsFinalParams, false);
    }

    // 이벤트 바인딩 (ID 확인)
    $('#btnGoodsSearch').on('click', executeGoodsSearch);
    
    // 정렬/개수 변경 시 자동 재조회 (굿즈 전용)
    $('#sortDirGoods, #perPageGoods').on('change', executeGoodsSearch);

    $('#btnGoodsSave').on('click', function() {
        if(confirm('상품 정보를 수정하시겠습니까?')) {
            goodsGridManager.save();
        }
    });

    // 전체 건수 업데이트 (ID: totalCntGoods)
    goodsGridManager.grid.on('successResponse', (ev) => {
        if (ev.requestType === 'readData') {
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCntGoods').text(total);
        } else if (ev.requestType === 'modifyData') {
            alert('저장되었습니다.');
            executeGoodsSearch();
        }
    });

    // 초기 로딩
    executeGoodsSearch();
});
</script>