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
<script src="<c:url value='/js/goods/goodsGrid.js'/>"></script>
<script src="<c:url value='/js/goods/goodsOrderGrid.js'/>"></script>
<script src="<c:url value='/js/goods/goodsSettlementGrid.js'/>"></script>
<script src="<c:url value='/js/goods/goodsReturnGrid.js'/>"></script>
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
    <div class="tab-menu">
	    <button class="tab-btn active" data-tab="goods">굿즈 관리</button>
	    <button class="tab-btn" data-tab="order">주문 관리</button>
	    <button class="tab-btn" data-tab="settlement">정산 관리</button>
	    <button class="tab-btn" data-tab="return">반품/교환 관리</button>
	</div>
	<div id="tab-goods" class="tab-content active">
    	<%@ include file="/WEB-INF/views/admin/goods/list/goodsGrid.jsp" %>
    </div>
	<div id="tab-order" class="tab-content">
    	<%@ include file="/WEB-INF/views/admin/goods/list/goodsOrderGrid.jsp" %>
    </div>
	<div id="tab-settlement" class="tab-content">
    	<%@ include file="/WEB-INF/views/admin/goods/list/goodsSettlementGrid.jsp" %>
    </div>
	<div id="tab-return" class="tab-content">
    	<%@ include file="/WEB-INF/views/admin/goods/list/goodsReturnGrid.jsp" %>
    </div>
    

    <%-- <%@ include file="/WEB-INF/views/admin/goods/list/banner.jsp" %> --%>
</div>
</div>
</body>
</html>
<script>
function priceFormatter({value}) {
    return countFormatter({value}) + '원';
}
function countFormatter({value}) {
	return (value || 0).toLocaleString();
}
var finalParams = {};
let goodsFinalParams = {};
let settlementFinalParams = {};
let returnFinalParams = {};
let goodsGridManager = null;		//굿즈
let goodsGridInitialized = false;
let grid = null;					//주문내역
let GridInitialized = false;
let settlementGridManager = null;	//정산관리
let settlementInit = false;
let returnGridManager = null;	//정산관리
let returnInit = false;
//탭 전환
$('.tab-btn').on('click', function() {
    const tab = $(this).data('tab');

    // 버튼 active
    $('.tab-btn').removeClass('active');
    $(this).addClass('active');

    // 컨텐츠 active
    $('.tab-content').removeClass('active');
    $('#tab-' + tab).addClass('active');
    
    if (tab === 'goods') {
        initGoodsGrid();
    }
    if (tab === 'order') {
        initGoodsOrderGrid();
    }
    if (tab === 'settlement') {
        initGoodsSettlementGrid();
    }
    if (tab === 'return') {
    	initGoodsReturnGrid();
    }
});
initGoodsGrid();
</script>