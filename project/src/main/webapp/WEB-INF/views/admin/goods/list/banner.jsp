<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link href="<c:url value='/css/goods/list/banner.css'/>" rel="stylesheet">

<div class="dashboard-section banner-management-section">
    <div class="section-title">
        <span>메인 홍보 배너 관리</span>
        <button class="btn btn-primary btn-sm" onclick="openBannerModal()">+ 배너 등록</button>
    </div>

    <div class="banner-grid" id="bannerList">
        <c:forEach var="banner" items="${bannerList}">
            <div class="banner-item">
                <div class="banner-img-wrapper">
                    <c:choose>
                        <c:when test="${not empty banner.imgUrl}">
                            <img src="${banner.imgUrl}" alt="배너 이미지">
                        </c:when>
                        <c:otherwise>
                            <span style="color:#ccc;">이미지 없음</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="banner-info">
                    <span class="banner-status ${banner.active == 1 ? 'status-on' : 'status-off'}">
				        ${banner.active == 1 ? '노출 중' : '중단/종료'}
				    </span>
                    <p><strong>연결:</strong> ${banner.linkUrl}</p>
                    <p><strong>기간:</strong> ${banner.startDate} ~ ${banner.endDate}</p>
                    <div class="banner-actions">
                        <button class="btn btn-sm btn-secondary" onclick="editBanner(${banner.id})">수정</button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteBanner(${banner.id})">삭제</button>
                    </div>
                </div>
            </div>
        </c:forEach>
        
        <%-- 데이터가 없을 때 표시 --%>
        <c:if test="${empty bannerList}">
            <div style="grid-column: 1/-1; text-align: center; padding: 40px; color: #999;">
                등록된 배너가 없습니다.
            </div>
        </c:if>
    </div>
</div>

<script>
function openBannerModal() {
    // 배너 등록 모달창 띄우기 로직
    alert('배너 등록 폼으로 이동하거나 모달을 띄웁니다.');
}

function editBanner(id) {
    location.href = '/admin/banner/edit/' + id;
}

function deleteBanner(id) {
    if(confirm('정말 삭제하시겠습니까?')) {
        // 삭제 Ajax 로직
    }
}
</script>