<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <link href="<c:url value='/css/profile/list.css?v=1.1'/>" rel="stylesheet">
    <title>연습생 프로필 & 미디어 관리</title>
</head>
<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

<div class="admin-container">
    <div class="page-header-title">
        <div class="control-left">
            <h2>연습생 상세 관리</h2>
        </div>
        <div class="control-right">
            <button type="button" id="btnCreate" class="btn-search">신규 프로필 등록</button>
        </div>        
    </div>

    <div class="dashboard-card form-section active" id="createFormSection">
        <form action="/admin/idol/save" method="post" enctype="multipart/form-data">
            <input type="hidden" name="profileId" id="f-profileId"/>
            <div class="card-header"><span class="title" id="formTitle">연습생 등록/수정</span></div>
            
            <div class="filter-container">
                <div class="filter-row">
                    <div class="filter-group">
                        <label>이름 (KOR/EN)</label>
                        <div class="input-combined">
                            <input type="text" name="name" id="f-name" placeholder="재민" required>
                            <input type="text" name="nameEn" id="f-nameEn" placeholder="Na Jae-min">
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>생년월일 / MBTI</label>
                        <div class="input-combined">
                            <input type="date" name="birth" id="f-birth">
                            <input type="text" name="mbti" id="f-mbti" placeholder="ISFJ">
                        </div>
                    </div>
                    <div class="filter-group">
                        <label>키 / 나이</label>
                        <div class="input-combined">
                            <input type="number" name="height" id="f-height" placeholder="177">
                            <input type="number" name="age" id="f-age" placeholder="26">
                        </div>
                    </div>
                </div>

                <div class="filter-row">
                    <div class="filter-group">
                        <label>나를 나타내는 키워드</label>
                        <input type="text" name="keyword" id="f-keyword" placeholder="나나, 공주왕자">
                    </div>
                    <div class="filter-group">
                        <label>취미</label>
                        <input type="text" name="hobby" id="f-hobby" placeholder="사진 찍기, 요리">
                    </div>
                </div>

                <div class="filter-row" style="background: #f9f9f9; padding: 15px; border-radius: 8px;">
                    <div class="filter-group">
                        <label style="color: #4f46e5;">★ 메인 프로필 사진 (상단 노출)</label>
                        <input type="file" name="mainImgFile">
                        <small id="mainImgPathDisplay" style="display:block; margin-top:5px; color:#666;"></small>
                    </div>
                    <div class="filter-group">
                        <label>갤러리 추가 사진</label>
                        <input type="file" name="subImgFiles" multiple>
                    </div>
                </div>

                <div id="mediaPreviewSection" style="display:none; margin-top:10px; padding:10px; border:1px dashed #ccc;">
                    <label>기존 등록된 미디어</label>
                    <div id="mediaThumbnails"></div>
                </div>

                <div class="filter-btns" style="text-align:right; margin-top: 10px;">
                    <button type="submit" class="btn-action btn-save-main">데이터 저장하기</button>
                    <button type="button" onclick="location.reload()" class="btn-action btn-reset" style="background:#666; color:#fff;">취소</button>
                </div>
            </div>
        </form>
    </div>

    <div class="dashboard-card grid-area">
        <div class="card-header"><span class="title">연습생 목록</span></div>
        <div id="grid-container">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>이름</th>
                        <th>MBTI</th>
                        <th>키</th>
                        <th style="width: 15%;">취미</th>
                        <th style="width: 15%;">키워드</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty profileList.list}">
                            <c:forEach var="idol" items="${profileList.list}">
                                <tr>
                                    <td>${idol.profileId}</td>
                                    <td>
                                        <div style="display: flex; align-items: center;">
                                            <img src="/upload/${idol.mainImgUrl}" 
                                                 onerror="this.src='/img/default-profile.png'" 
                                                 style="width:40px; height:40px; border-radius:50%; margin-right:10px; object-fit: cover; border: 1px solid #eee;">
                                            <div style="text-align: left;">
                                                <strong style="display: block; font-size: 14px;">${idol.name}</strong>
                                                <small style="color: #888; font-size: 11px;">${idol.nameEn}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-info" style="padding: 4px 8px; font-size: 11px;">${idol.mbti}</span></td>
                                    <td>${idol.height}cm</td>
                                    <td style="font-size: 13px; color: #666;">${idol.hobby}</td> 
                                    <td style="color: #4f46e5; font-weight: 600;">#${idol.keyword}</td> 
                                    <td>
                                        <div class="btn-group" style="display: flex; gap: 5px; justify-content: center;">
                                            <button type="button" class="btn btn-sm btn-outline-primary btn-update"
                                                    data-id="${idol.profileId}"
                                                    data-name="${idol.name}"
                                                    data-nameen="${idol.nameEn}"
                                                    data-birth="${idol.birth}"
                                                    data-mbti="${idol.mbti}"
                                                    data-height="${idol.height}"
                                                    data-age="${idol.age}"
                                                    data-hobby="${idol.hobby}"
                                                    data-keyword="${idol.keyword}"
                                                    data-mainurl="${idol.mainImgUrl}">
                                                수정
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger btn-delete" 
                                                    data-id="${idol.profileId}">
                                                삭제
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" style="padding: 50px 0; color: #999; text-align:center;">
                                    조회된 연습생이 없습니다.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    // 수정 버튼 클릭 이벤트
    $(document).on("click", ".btn-update", function() {
        const d = $(this).data();
        
        // 폼 필드 채우기
        $("#f-profileId").val(d.id);
        $("#f-name").val(d.name);
        $("#f-nameEn").val(d.nameen);
        $("#f-birth").val(d.birth);
        $("#f-mbti").val(d.mbti);
        $("#f-height").val(d.height);
        $("#f-age").val(d.age);
        $("#f-hobby").val(d.hobby);
        $("#f-keyword").val(d.keyword);
        
        // 메인 이미지 경로 표시
        $("#mainImgPathDisplay").text("현재 파일: " + (d.mainurl || "없음"));
        $("#formTitle").text("연습생 정보 수정 (ID: " + d.id + ")");
        
        // 미디어 미리보기 처리 (d.media 데이터가 있을 경우만)
        const $thumbContainer = $("#mediaThumbnails");
        $thumbContainer.empty(); 
        
        if (d.media && Array.isArray(d.media) && d.media.length > 0) {
            $("#mediaPreviewSection").show();
            d.media.forEach(media => {
                if (media.url) {
                    const thumbHtml = `
                        <div style="display:inline-block; margin:5px; text-align:center;">
                            <img src="/upload/` + media.url + `" style="width:80px; height:80px; object-fit:cover; border:1px solid #ccc;">
                        </div>
                    `;
                    $thumbContainer.append(thumbHtml);
                }
            });
        } else {
            $("#mediaPreviewSection").hide();
        }

        window.scrollTo({ top: 0, behavior: 'smooth' });
    });

    // 삭제 처리
    $(document).on("click", ".btn-delete", function () {
        const id = $(this).data("id");
        if(confirm("이 연습생의 데이터가 삭제됩니다. 진행하시겠습니까?")) {
            $.ajax({
                url: "/admin/idol/delete?profileId=" + id,
                type: "POST",
                success: function () {
                    alert("성공적으로 삭제되었습니다.");
                    location.reload();
                },
                error: function() {
                    alert("삭제 요청 중 오류가 발생했습니다.");
                }
            });
        }
    });

    // 신규 등록 버튼 (폼 리셋)
    $("#btnCreate").click(function() {
        $("form")[0].reset();
        $("#f-profileId").val("");
        $("#formTitle").text("신규 연습생 등록");
        $("#mainImgPathDisplay").text("");
        $("#mediaPreviewSection").hide();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
});
</script>
</body>
</html>