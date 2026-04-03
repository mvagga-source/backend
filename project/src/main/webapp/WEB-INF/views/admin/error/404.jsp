<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>페이지를 찾을 수 없습니다 - ACTION 101</title>
    <style>
        .error-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: calc(100vh - 200px);
            text-align: center;
            background-color: #f4f6f9;
        }

        .error-code {
            font-size: 120px;
            font-weight: 800;
            color: #1a2c4e;
            margin: 0;
            line-height: 1;
            opacity: 0.8;
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
            display: inline-block;
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
    </style>
</head>
<body>
    <c:import url="/WEB-INF/views/admin/layout/header.jsp" />

    <div class="error-container">
        <h1 class="error-code">404</h1>
        <p class="error-title">요청하신 페이지를 찾을 수 없습니다.</p>
        <p class="error-msg">
            방문하시려는 페이지의 주소가 잘못 입력되었거나,<br>
            페이지의 주소가 변경 혹은 삭제되어 요청하신 페이지를 찾을 수 없습니다.
        </p>

        <div class="btn-group">
            <a href="javascript:history.back();" class="btn btn-outline">이전 페이지로</a>
            <a href="<c:url value='/admin/main'/>" class="btn btn-primary">관리자 메인으로</a>
        </div>
    </div>
</body>
</html>