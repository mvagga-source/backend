<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link href="<c:url value='/css/goods/list/summary.css'/>" rel="stylesheet">

<div class="summary-grid">
    <div class="stat-card">
        <!-- 로딩 -->
	    <div class="loading-overlay" id="loading-todaySales">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
	    <div class="card-content">
        	<h3>오늘의 매출</h3>
        	<p><span class="currency-symbol">₩</span><span id="todaySales">0</span></p>
        </div>
    </div>
    <div class="stat-card">
    	<!-- 로딩 -->
	    <div class="loading-overlay" id="loading-readyCount">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
	    <div class="card-content">
        	<h3>배송대기</h3>
        	<p><span id="readyCount">0</span><span class="unit">건</span></p>
        </div>
    </div>
    <div class="stat-card">
    	<!-- 로딩 -->
	    <div class="loading-overlay" id="loading-activeGoods">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
	    <div class="card-content">
	        <h3>판매중 굿즈</h3>
	        <p><span id="activeGoodsCount">0</span><span class="unit">개</span></p>
        </div>
    </div>
    <div class="stat-card">
		<!-- 로딩 -->
	    <div class="loading-overlay" id="loading-cancel">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
	    <div class="card-content">
	        <h3>신규 취소요청</h3>
	        <p style="color: #c62828;"><span id="cancelRequestCount">0</span><span class="unit">건</span></p>
        </div>
    </div>
    <div class="stat-card">
		<!-- 로딩 -->
	    <div class="loading-overlay" id="loading-rating">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
	    <div class="card-content">
	        <h3>사이트 평균 별점</h3>
	        <p>
	            <span style="color: #fbc02d; margin-right:5px;">★</span>
	            <span id="avgRating">0.0</span>
	            <span class="unit">/ 5.0</span>
	        </p>
        </div>
    </div>
</div>

<div class="charts-row">
    <div class="chart-container">
        <div class="section-title">주간 매출 추이</div>
        <!-- 로딩 -->
	    <div class="loading-overlay" id="loading-revenue">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
        <canvas id="revenueChart"></canvas>
    </div>
    <div class="chart-container">
        <div class="section-title">참가자별 판매 비중</div>
        <!-- 로딩 -->
	    <div class="loading-overlay" id="loading-idol">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
        <canvas id="idolPieChart"></canvas>
    </div>
    <div class="chart-container">
        <div class="section-title">리뷰 별점 분포</div>
        <!-- 로딩 -->
	    <div class="loading-overlay" id="loading-ratingChart">
	        <%@ include file="/WEB-INF/views/admin/goods/list/summary/loading.jsp" %>
	    </div>
        <canvas id="ratingBarChart"></canvas>
    </div>
</div>

<script>
function showLoading() {
    // 모든 로딩 레이어 표시
    $('.loading-overlay').show();
    // 모든 실제 콘텐츠 숨김
    $('.card-content, canvas, .section-title').css('visibility', 'hidden');
}

function hideLoading() {
    // 로딩 레이어 부드럽게 제거
    $('.loading-overlay').fadeOut(300, function() {
        // 제거 완료 후 콘텐츠 보이기
        $('.card-content, canvas, .section-title').css('visibility', 'visible');
    });
}
$(document).ready(function() {
    loadSummaryData();
});
let revenueChart, idolPieChart, ratingBarChart;

function loadSummaryData() {
	showLoading();
    $.ajax({
        url: '/admin/goods/api/summary',
        method: 'GET',
        success: function(data) {
            // 카드 데이터 업데이트
            $('#todaySales').text(data.todaySales.toLocaleString());
            $('#readyCount').text(data.readyCount);
            $('#activeGoodsCount').text(data.activeGoodsCount);
            $('#cancelRequestCount').text(data.cancelRequestCount);
            $('#avgRating').text(data.avgRating);
            
            // 차트 초기화 및 데이터 주입
            initCharts(data);
        },
        error: function(err) {
	        console.log(err);
        },
        complete: function() {
            hideLoading();
        }
    });
}

function initCharts(data) {
	// 기존 차트가 있다면 없애기
    if (revenueChart) revenueChart.destroy();
    if (idolPieChart) idolPieChart.destroy();
    if (ratingBarChart) ratingBarChart.destroy();
    // 1. 주간 매출 (Line)
    revenueChart = new Chart(document.getElementById('revenueChart'), {
        type: 'line',
        data: {
            labels: data.weeklyLabels || ['월', '화', '수', '목', '금', '토', '일'],
            datasets: [{
                label: '매출액(원)',
                data: data.weeklySales || [0, 0, 0, 0, 0, 0, 0],
                borderColor: '#1a2c4e',
                backgroundColor: 'rgba(26, 44, 78, 0.05)',
                fill: true,
                tension: 0.4
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    // 2. 아이돌 비중 (Doughnut)
    idolPieChart = new Chart(document.getElementById('idolPieChart'), {
        type: 'doughnut',
        data: {
            labels: data.idolLabels || ['기타'],
            datasets: [{
                data: data.idolValues || [100],
                backgroundColor: ['#1a2c4e', '#34495e', '#5dade2', '#bdc3c7', '#ebedef']
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });

    // 3. 별점 분포 (Horizontal Bar)
    ratingBarChart = new Chart(document.getElementById('ratingBarChart'), {
        type: 'bar',
        data: {
            labels: ['5점', '4점', '3점', '2점', '1점'],
            datasets: [{
                label: '리뷰 수',
                data: data.ratingDistribution || [0, 0, 0, 0, 0],
                backgroundColor: '#fbc02d'
            }]
        },
        options: {
            indexAxis: 'y', // 가로 막대
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });
}
</script>