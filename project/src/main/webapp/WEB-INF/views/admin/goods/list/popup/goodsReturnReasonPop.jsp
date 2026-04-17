<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link href="<c:url value='/css/goods/list/popup/returnResonPopup.css'/>" rel="stylesheet">
<div id="reasonModal" class="admin-modal" style="display:none;">
    <div class="modal-overlay" onclick="$('#reasonModal').hide()"></div>
    <div class="modal-content">
        <div class="modal-header">
            <h3>반품 및 거부 사유 상세</h3>
            <button type="button" class="btn-modal-close" onclick="$('#reasonModal').hide()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="modal-form-group">
                <label><i class="icon-buyer"></i> 구매자 반품 상세 사유</label>
                <textarea id="modalReturnReasonDetail" readonly placeholder="구매자가 입력한 상세 사유가 없습니다."></textarea>
            </div>
            <div class="modal-form-group">
                <label><i class="icon-seller"></i> 판매자 거부 사유</label>
                <textarea id="modalReturnSaleReasonDetail" readonly  placeholder="거부사유가 입력되지 않았습니다."></textarea>
                <!-- <textarea id="modalReturnSaleReasonDetail"  placeholder="거부 시 사유를 입력하거나 관리자 메모를 남겨주세요."></textarea>
                <p class="helper-text">* 여기서 수정 후 임시저장을 누르면 목록에 반영되며, 목록의 [저장] 버튼을 눌러야 최종 저장됩니다.</p> -->
            </div>
        </div>
        <!-- <div class="modal-footer">
            <button type="button" id="btnModalSave" class="btn-modal-save">임시저장</button>
            <button type="button" class="btn-modal-cancel" onclick="$('#reasonModal').hide()">닫기</button>
        </div> -->
    </div>
</div>