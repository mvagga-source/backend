<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>ACTION 101 관리자</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: 'Malgun Gothic', sans-serif; background: #f4f6f9; color: #333; }

    /* ── 헤더 ── */
    .admin-header {
      background: #1a2c4e;
      color: white;
      padding: 16px 32px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .admin-header h1 { font-size: 18px; letter-spacing: 2px; }
    .admin-header span { font-size: 13px; color: rgba(255,255,255,0.6); }

    /* ── 탭 메뉴 ── */
    .tab-bar {
      background: white;
      border-bottom: 2px solid #e0e0e0;
      padding: 0 32px;
      display: flex;
      gap: 4px;
    }
    .tab-btn {
      padding: 14px 24px;
      border: none;
      background: none;
      font-size: 14px;
      font-weight: 600;
      color: #888;
      cursor: pointer;
      border-bottom: 3px solid transparent;
      margin-bottom: -2px;
      transition: all 0.2s;
    }
    .tab-btn:hover { color: #1a2c4e; }
    .tab-btn.active {
      color: #1a2c4e;
      border-bottom-color: #1a2c4e;
    }

    /* ── 탭 컨텐츠 ── */
    .tab-content { display: none; padding: 32px; }
    .tab-content.active { display: block; }
  </style>
</head>
<body>

  <!-- 헤더 -->
  <div class="admin-header">
    <h1>ACTION 101 — 관리자</h1>
    <span>Admin Page</span>
  </div>

  <!-- 탭 메뉴 -->
  <div class="tab-bar">
    <button class="tab-btn active" onclick="showTab('audition')">오디션 관리</button>
    <button class="tab-btn" onclick="showTab('test')">test탭</button>
    <!-- 탭 추가할 때 여기에 버튼 추가하면 됩니다. -->
    <!-- <button class="tab-btn" onclick="showTab('schedule')">스케줄 관리</button> -->
  </div>

  <!-- 탭 컨텐츠 영역 -->
  <div id="tab-audition" class="tab-content active">
    <%@ include file="tabs/auditionTab.jsp" %>
  </div>
  <div id="tab-audition" class="tab-content">
    <%@ include file="tabs/testTab.jsp" %>
  </div>

  <!-- 탭 추가할 때 여기에 div 추가하면 됩니다. -->
  <%-- 
  <div id="tab-schedule" class="tab-content">
    <%@ include file="tabs/scheduleTab.jsp" %>
  </div>
   --%>
 
  <script>
    function showTab(tabId) {
      // 모든 탭 컨텐츠 숨기기
      document.querySelectorAll('.tab-content').forEach(el => {
        el.classList.remove('active');
      });
      // 모든 탭 버튼 비활성화
      document.querySelectorAll('.tab-btn').forEach(el => {
        el.classList.remove('active');
      });
      // 선택한 탭만 활성화
      document.getElementById('tab-' + tabId).classList.add('active');
      event.target.classList.add('active');
    }
  </script>

</body>
</html>