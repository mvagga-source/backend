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
    <form id="settlementSearchForm" onsubmit="return false;">
        <div class="card-header">
            <span class="title">정산 내역 검색 필터</span>
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
                            <!-- <option value="buyerName">구매자명</option> -->
                            <option value="sellerName">판매자명</option>
                        </select>
                        <input type="text" id="search" name="search" placeholder="검색어를 입력하세요">
                    </div>
                </div>
                <!-- <div class="filter-group">
                    <label>결제상태</label>
                    <select name="status" id="status">
                        <option value="">전체</option>
                        <option value="PAID">결제완료</option>
                        <option value="READY">결제대기</option>
                        <option value="CANCEL">결제취소</option>
                        <option value="FAILED">결제실패</option>
                    </select>
                </div> -->
                <!-- <div class="filter-group">
				    <label>정산 여부</label>
				    <select name="settleYn" id="settleYn">
				        <option value="">전체</option>
				        <option value="n">정산대기</option>
				        <option value="y">정산완료</option>
				    </select>
				</div> -->
				<!-- <div class="filter-group">
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
				</div> -->
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
                    <label>정산 금액</label>
                    <div class="input-combined">
                        <input type="number" name="minAmount" value="0">
                        <span class="txt-dash">~</span>
                        <input type="number" name="maxAmount" placeholder="무제한">
                    </div>
                </div>
                <div class="filter-btns">
                    <button type="button" id="btnSearchSettlement" class="btn-search">검색하기</button>
                </div>

            </div>

        </div>
    </form>
</div>

<div class="dashboard-card grid-area">

    <div class="card-header grid-header">
        <span class="title">정산 내역 목록</span>
        <!-- <button type="button" id="btnSaveSettlement" class="btn-save">저장</button> -->
    </div>

    <div class="grid-control-bar">

        <div class="control-left">
            <div class="total-info">전체 <strong id="settlementTotalCnt">0</strong>건</div>
        </div>

        <div class="control-right">
            <select id="sortDirSettlement" class="select-sm">
                <option value="crdt_desc">최신순</option>
                <option value="crdt_asc">오래된순</option>
                <option value="amount_desc">정산금액 높은순</option>
                <option value="amount_asc">정산금액 낮은순</option>
            </select>

            <select id="perPageSettlement" class="select-sm">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </div>

    </div>

    <div id="settlement-grid-container"></div>
</div>
<script>
</script>