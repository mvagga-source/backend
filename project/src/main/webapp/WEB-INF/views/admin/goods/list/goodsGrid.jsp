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
<%-- <script src="<c:url value='/js/gridutils.js'/>"></script> --%><!-- 공통기능은 클래스 형태로 축약 -->
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
                <div class="filter-group">
				    <label>배너 노출 여부</label>
				    <select name="isBanner">
				        <option value="">전체</option>
				        <option value="y">노출</option>
				        <option value="n">미노출</option>
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
</script>