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
    <form id="searchFormReturn" onsubmit="return false;">
        <div class="card-header">
            <span class="title">반품/교환내역 검색 필터</span>
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
                <div class="filter-group">
			        <label>구분</label>
			        <select name="returnType" id="returnType">
			            <option value="">전체</option>
			            <option value="반품">반품</option>
			            <option value="교환">교환</option>
			        </select>
			    </div>
			
			    <div class="filter-group">
			        <label>반품/교환 사유</label>
			        <select name="returnReason" id="returnReason">
			            <option value="">전체</option>
			            <option value="변심">변심</option>
			            <option value="파손">파손</option>
			            <option value="오배송">오배송</option>
			            <option value="지연">지연</option>
			        </select>
			    </div>
                <div class="filter-group">
                    <label>반품상태</label>
                    <select name="returnStatus" id="status">
                        <option value="">전체</option>
                        <option value="접수">접수</option>
                        <option value="회수중">회수중</option>
                        <option value="검수대기">검수대기</option>
                        <option value="검수중">검수중</option>
                        <option value="완료">완료</option>
                        <option value="거부">거부</option>
                        <option value="취소">취소</option>
                    </select>
                </div>
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
                    <label>금액범위</label>
                    <div class="input-combined">
                        <input type="number" name="minPrice" value="0">
                        <span class="txt-dash">~</span>
                        <input type="number" name="maxPrice" placeholder="무제한">
                    </div>
                </div>
                <div class="filter-btns">
                    <button type="button" id="btnSearchReturn" class="btn-search">검색하기</button>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="dashboard-card grid-area">
    <div class="card-header grid-header">
        <span class="title">반품/교환 내역 목록</span>
        <button type="button" id="btnSaveReturn" class="btn-save">저장</button>
    </div>
    
    <div class="grid-control-bar">
        <div class="control-left">
        	<div class="total-info">전체 <strong id="totalCntReturn">0</strong>건</div>
        </div>
        <div class="control-right">
            <select id="sortDirReturn" class="select-sm">
                <option value="crdt_desc">최신순</option>
                <option value="crdt_asc">오래된순</option>
                <option value="price_desc">금액높은순</option>
				<option value="price_asc">금액낮은순</option>
            </select>
            <select id="perPageReturn" class="select-sm">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
            </select>
        </div>
    </div>

    <div id="grid-container-return"></div>
</div>
<jsp:include page="/WEB-INF/views/admin/goods/list/popup/goodsReturnReasonPop.jsp" />
<script>
</script>