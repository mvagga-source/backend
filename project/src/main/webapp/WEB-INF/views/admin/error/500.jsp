<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>시스템 오류 - ACTION 101</title>
    <style>
        .error-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: calc(100vh - 200px); /* 헤더 높이 제외 */
            text-align: center;
            background-color: #f4f6f9;
        }

        .error-code {
            font-size: 120px;
            font-weight: 800;
            color: #1a2c4e; /* 기존 헤더 색상 */
            margin: 0;
            line-height: 1;
        }

        .error-title {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin-top: 20px;
        }

        .error-msg {
            font-size: 16px;
            color: #666;
            margin-top: 10px;
            line-height: 1.6;
        }

        .btn-group {
            margin-top: 40px;
            display: flex;
            gap: 12px;
        }

        .btn {
            padding: 12px 24px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            transition: 0.2s;
        }

        .btn-primary {
            background-color: #1a2c4e;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background-color: #2c3e5e;
        }

        .btn-outline {
            background-color: transparent;
            border: 1px solid #1a2c4e;
            color: #1a2c4e;
        }

        .btn-outline:hover {
            background-color: #e9ecef;
        }

        /* 상세 에러 내용 (관리자용 개발 모드에서만 출력 권장) */
        .debug-info {
            margin-top: 50px;
            max-width: 800px;
            text-align: left;
            background: #fff;
            padding: 20px;
            border-left: 4px solid #d9534f;
            font-family: 'Courier New', Courier, monospace;
            font-size: 12px;
            color: #d9534f;
            display: none; /* 기본은 숨김, 필요시 노출 */
        }
    </style>
</head>
<body>
    <c:import url="/WEB-INF/views/admin/layout/header.jsp" />

    <div class="error-container">
        <h1 class="error-code">500</h1>
        <p class="error-title">시스템에 일시적인 오류가 발생했습니다.</p>
        <p class="error-msg">
            요청을 처리하는 과정에서 예상치 못한 문제가 발생하였습니다.<br>
            잠시 후 다시 시도해 주시고, 문제가 지속되면 관리자에게 문의해 주세요.
        </p>

        <div class="btn-group">
            <a href="javascript:history.back();" class="btn btn-outline">이전 페이지로</a>
            <a href="<c:url value='/admin/main'/>" class="btn btn-primary">관리자 메인으로</a>
        </div>

        <%-- 개발 환경에서 에러 로그를 확인하고 싶을 때 사용 --%>
        <c:if test="${not empty errorMsg}">
            <div class="debug-info" style="display: block;">
                <strong>Error Detail:</strong><br>
                ${errorMsg}
            </div>
        </c:if>
    </div>
</body>
</html>