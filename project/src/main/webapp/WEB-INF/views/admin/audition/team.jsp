<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>${audition['round']}차 팀경연 관리 — ACTION 101</title>
  <link href="<c:url value='/css/audition/team.css'/>" rel="stylesheet">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
</head>

<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

<div class="page-body">

  <!-- ── 알림 메시지 ── -->
  <div id="at-msg" class="at-msg"></div>

  <!-- ── 페이지 헤더 ── -->
  <div class="tm-page-header">
    <div>
      <h2 class="tm-page-title">${audition['round']}차 — 팀경연 관리</h2>
      <p class="tm-page-sub">${audition['title']} / ${audition['startDate']} ~ ${audition['endDate']}</p>
    </div>
    <a href="/admin/audition/round" class="btn btn-secondary btn-sm">← 회차 목록으로</a>
  </div>

  <!-- ══════════════════════════════════════
       ① 대결 목록
  ══════════════════════════════════════ -->
  <div class="at-section">
    <div class="at-section-title">
      대결 목록
      <div style="float:right; display:flex; gap:8px;">
        <button class="btn btn-secondary btn-sm"
        		onclick="openTeamTemplateModal()">📋 Excel 양식 안내</button>
        <input type="file" id="excel-round-input" accept=".xlsx,.xls"
               style="display:none;" onchange="handleRoundExcelUpload()">
        <button class="btn btn-secondary btn-sm"
                onclick="document.getElementById('excel-round-input').click()">대결 일괄 등록(Excel)</button>
        <button class="btn btn-purple btn-sm"
                onclick="openCreateMatchForm()">+ 대결 개별 등록</button>
      </div>
    </div>

    <!-- Excel 시트 선택 -->
    <div id="excel-sheet-select-box" style="display:none; background:#f0f4ff; border:1px solid #c5d0f0; border-radius:8px; padding:14px 20px; margin-bottom:12px;">
      <p style="font-size:12px; font-weight:700; color:#1a2c4e; margin-bottom:8px;">📋 시트 선택</p>
      <div style="display:flex; align-items:center; gap:10px; flex-wrap:wrap;">
        <select id="excel-sheet-select"
                style="font-size:13px; padding:6px 10px; border:1px solid #c5d0f0; border-radius:6px; background:#fff; color:#1a2c4e; cursor:pointer;"
                onchange="parseSelectedSheet()">
        </select>
        <span style="font-size:11px; color:#888;">원하는 시트를 선택하면 바로 미리보기가 갱신돼요.</span>
        <button class="btn btn-secondary btn-sm" onclick="closeRoundExcelPreview()">취소</button>
      </div>
    </div>

    <!-- Excel 미리보기 -->
    <div id="excel-round-preview" style="display:none; background:#f8faff; border:1px solid #d0d9f0; border-radius:8px; padding:20px; margin-bottom:16px;">
      <p style="font-size:13px; font-weight:700; color:#1a2c4e; margin-bottom:4px;">Excel 파싱 결과 확인</p>
      <p style="font-size:11px; color:#888; margin-bottom:12px;">아래 내용으로 팀 대결이 등록됩니다. 이름이 잘못됐으면 취소 후 Excel을 수정하세요.</p>
      <div id="excel-round-preview-table" style="overflow-x:auto;"></div>
      <div style="margin-top:12px; display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:8px;">
        <span id="excel-round-preview-summary" style="font-size:12px; color:#555;"></span>
        <div style="display:flex; gap:8px;">
          <button class="btn btn-secondary btn-sm" onclick="closeRoundExcelPreview()">취소</button>
          <button class="btn btn-purple btn-sm" onclick="confirmRoundExcelUpload()">일괄 등록</button>
        </div>
      </div>
    </div>

    <!-- 대결 등록 폼 -->
    <div id="form-create-match" class="at-form">
      <div class="form-create-box">
        <p class="form-create-title">새 팀 대결 등록</p>
        <div class="form-grid">

          <div class="form-group form-row-full">
            <label>대결 이름 (예: A조, 1조)</label>
            <input type="text" id="m-matchName" placeholder="예) A조">
          </div>

          <!-- A팀 -->
          <div class="form-group">
            <label>A팀 이름</label>
            <input type="text" id="m-teamAName" placeholder="예) BLUE팀">
            <div class="tm-img-upload-box">
              <label>A팀 대표 이미지 (선택)</label>
              <div class="tm-img-upload-row">
                <input type="file" id="m-teamAImg" accept="image/*"
                       onchange="uploadTeamImg('A')">
                <span id="m-teamAImg-status" class="tm-upload-status"></span>
              </div>
              <img id="m-teamAImg-preview" class="tm-img-preview"
                   style="display:none;" alt="A팀 이미지">
            </div>
            <input type="hidden" id="m-teamAImgUrl">
            <!-- A팀 팀원 선택 -->
            <div class="tm-member-select-box">
              <label>A팀 팀원 배정 (선택) <span id="m-teamA-count" style="color:#7c4dff;font-weight:700;"></span></label>
              <div id="m-teamA-checkbox-list" class="tm-member-checkbox-list">
                <p style="color:#aaa;font-size:12px;">참가자 불러오는 중...</p>
              </div>
            </div>
          </div>

          <!-- B팀 -->
          <div class="form-group">
            <label>B팀 이름</label>
            <input type="text" id="m-teamBName" placeholder="예) RED팀">
            <div class="tm-img-upload-box">
              <label>B팀 대표 이미지 (선택)</label>
              <div class="tm-img-upload-row">
                <input type="file" id="m-teamBImg" accept="image/*"
                       onchange="uploadTeamImg('B')">
                <span id="m-teamBImg-status" class="tm-upload-status"></span>
              </div>
              <img id="m-teamBImg-preview" class="tm-img-preview"
                   style="display:none;" alt="B팀 이미지">
            </div>
            <input type="hidden" id="m-teamBImgUrl">
            <!-- B팀 팀원 선택 -->
            <div class="tm-member-select-box">
              <label>B팀 팀원 배정 (선택) <span id="m-teamB-count" style="color:#7c4dff;font-weight:700;"></span></label>
              <div id="m-teamB-checkbox-list" class="tm-member-checkbox-list">
                <p style="color:#aaa;font-size:12px;">참가자 불러오는 중...</p>
              </div>
            </div>
          </div>

        </div>
        <div class="form-btns">
          <button class="btn btn-secondary" onclick="closeCreateMatchForm()">취소</button>
          <button class="btn btn-purple" onclick="createTeamMatch()">등록</button>
        </div>
      </div>
    </div>

    <!-- 대결 카드 목록 -->
    <div id="tm-match-list">
      <c:if test="${empty matches}">
        <div class="tm-empty">등록된 팀 대결이 없어요. "+ 대결 등록"으로 추가하세요.</div>
      </c:if>
    </div>
  </div>

</div><!-- /page-body -->

<!-- ══════════════════════════════════════
     팀원 배정 모달
══════════════════════════════════════ -->
<div id="modal-add-member" class="modal-overlay">
  <div class="modal-box">
    <p class="modal-title">팀원 배정</p>
    <p id="add-member-desc" class="modal-desc"></p>

    <!-- 현재 팀원 -->
    <div id="add-member-current" style="margin-bottom:12px;"></div>

    <!-- 체크박스 목록 -->
    <div class="form-group" style="margin-bottom:8px;">
      <label>참가자 선택 <span id="add-member-count" style="color:#7c4dff;font-weight:700;"></span></label>
      <div id="add-member-checkbox-list"
           style="max-height:260px; overflow-y:auto; border:1px solid #d0d0d0;
                  border-radius:6px; padding:8px;">
        <p style="color:#aaa; font-size:12px;">불러오는 중...</p>
      </div>
    </div>
    
    <div class="modal-btns">
      <button class="btn btn-secondary" onclick="closeAddMemberModal()">닫기</button>
      <button class="btn btn-purple" onclick="addMembersBulk()">배정</button>
    </div>
  </div>
</div>

<!-- 팀 정보 수정 모달 -->
<div id="modal-edit-team" class="modal-overlay">
  <div class="modal-box">
    <p class="modal-title">팀 정보 수정</p>
    <div class="form-group" style="margin-bottom:12px;">
      <label>팀 이름</label>
      <input type="text" id="edit-team-name"
             style="padding:8px 12px; border:1px solid #d0d0d0; border-radius:6px; font-size:13px; width:100%;">
    </div>
    <div class="form-group" style="margin-bottom:12px;">
      <label>대표 이미지 변경 (선택)</label>
      <div class="tm-img-upload-row">
        <input type="file" id="edit-team-img-file" accept="image/*"
               onchange="uploadEditTeamImg()">
        <span id="edit-team-img-status" class="tm-upload-status"></span>
      </div>
      <img id="edit-team-img-preview" class="tm-img-preview"
           style="display:none; margin-top:8px;" alt="팀 이미지">
      <input type="hidden" id="edit-team-img-url">
    </div>
    <div class="modal-btns">
      <button class="btn btn-secondary" onclick="closeEditTeamModal()">취소</button>
      <button class="btn btn-purple" onclick="saveEditTeam()">저장</button>
    </div>
  </div>
</div>

<!-- ══ Excel 양식 안내 모달 (팀경연) ══ -->
<div id="modal-team-template" style="
  display:none; position:fixed; inset:0;
  background:rgba(0,0,0,0.45);
  z-index:9999;
  align-items:center; justify-content:center;">
  <div style="background:white; border-radius:12px; padding:28px 32px; width:820px; max-width:95vw;
              max-height:90vh; overflow-y:auto; box-shadow:0 8px 32px rgba(0,0,0,0.18);">

    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
      <p style="font-size:15px; font-weight:700; color:#1a2c4e; margin:0;">📋 팀경연 대결 일괄 등록 — Excel 양식 안내</p>
      <button onclick="closeTeamTemplateModal()" style="background:none;border:none;font-size:18px;cursor:pointer;color:#888;">✕</button>
    </div>

    <p style="font-size:12px; color:#666; margin-bottom:16px; line-height:1.7;">
      아래 양식을 참고해 Excel 파일을 작성한 뒤 <strong>「대결 일괄 등록(Excel)」</strong> 버튼으로 업로드하세요.<br>
      팀원은 <strong>쉼표(,)로 구분</strong>해 한 셀에 입력하고, 이미지 파일명은 생략 가능합니다.
    </p>

    <!-- 샘플 테이블 -->
    <div style="overflow-x:auto; margin-bottom:16px;">
      <table class="at-table" style="font-size:12px; min-width:680px;">
        <thead>
          <tr>
            <th>조이름</th>
            <th style="border-left:2px solid #b3c8ef;">1팀명</th>
            <th>1팀 이미지파일명</th>
            <th>1팀원 (쉼표 구분)</th>
            <th style="border-left:2px solid #f0b3b3;">2팀명</th>
            <th>2팀 이미지파일명</th>
            <th>2팀원 (쉼표 구분)</th>
          </tr>
          <tr style="background:#f0f4ff; font-size:11px; color:#7c8ba0;">
            <td>예) A조</td>
            <td style="border-left:2px solid #b3c8ef;">예) NOVA</td>
            <td>예) 2-A-N.jpg (생략가능)</td>
            <td>쉼표(,)로 구분</td>
            <td style="border-left:2px solid #f0b3b3;">예) ECLIPSE</td>
            <td>예) 2-A-E.jpg (생략가능)</td>
            <td>쉼표(,)로 구분</td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><strong>A조</strong></td>
            <td style="color:#1a5ca8; border-left:2px solid #b3c8ef;">NOVA</td>
            <td style="color:#888;">2-A-N.jpg</td>
            <td style="font-size:11px;">김지수, 오세진, 박서준, 최유나, 이준혁, 한채원, 정민서, 윤소아</td>
            <td style="color:#a81a1a; border-left:2px solid #f0b3b3;">ECLIPSE</td>
            <td style="color:#888;">2-A-E.jpg</td>
            <td style="font-size:11px;">박민준, 임도현, 송지아, 윤태양, 강민하, 류찬혁, 김하율, 오지혜</td>
          </tr>
          <tr style="color:#aaa; font-size:11px; font-style:italic; background:#fafafa;">
            <td colspan="7">↑ 예시 데이터입니다. 실제 입력 시 삭제하고 사용하세요.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 작성 규칙 -->
    <div style="background:#f8faff; border-left:3px solid #7c9fd4; border-radius:0 6px 6px 0;
                padding:10px 14px; font-size:12px; color:#444; line-height:1.9; margin-bottom:20px;">
      <strong style="color:#1a2c4e;">작성 규칙</strong><br>
      · 이미지 파일명은 <strong>파일명만</strong> 입력 (예: 2-A-N.jpg) — 경로 포함 X, 생략 가능<br>
      · 팀원은 <strong>쉼표(,)</strong>로 구분하여 한 셀에 입력 (예: 김지수, 오세진, 박서준)<br>
      · 팀원 이름은 DB의 <code>idol_profile.name</code>과 정확히 일치해야 함<br>
      · 이미지 파일명 규칙: <strong>(차수)-(조)-(팀이니셜).jpg</strong> (예: 2차 A조 NOVA → 2-A-N.jpg)<br>
      · 1~7행 (헤더 영역)은 수정하지 마세요
    </div>

    <div style="display:flex; justify-content:flex-end; gap:8px;">
      <button class="btn btn-secondary" onclick="closeTeamTemplateModal()">닫기</button>
      <button class="btn btn-purple" onclick="downloadTeamTemplate()">⬇ 템플릿 다운로드</button>
    </div>
  </div>
</div>

<%-- ── 서버 데이터 → JS 변수 전달
     이 블록은 백틱을 사용하지 않으므로 JSP EL 그대로 사용 가능 ── --%>
<script>
  const AUDITION_ID = ${audition['auditionId']};

  const initialMatches = [
    <c:forEach var="m" items="${matches}" varStatus="vs">
    {
      matchId:     ${m.matchId},
      matchName:   "${m.matchName}",
      teamAId:     ${m.teamAId},
      teamAName:   "${m.teamAName}",
      teamAImgUrl: "${m.teamAImgUrl != null ? m.teamAImgUrl : ''}",
      teamBId:     ${m.teamBId},
      teamBName:   "${m.teamBName}",
      teamBImgUrl: "${m.teamBImgUrl != null ? m.teamBImgUrl : ''}",
      teamAScore:  "${m.teamAScore}",
      teamBScore:  "${m.teamBScore}",
      winnerTeamId: ${m.winnerTeamId != null ? m.winnerTeamId : 'null'},
      status:      "${m.status}",
      membersA: [<c:forEach var="name" items="${m.membersA}" varStatus="ms">"${name}"<c:if test="${!ms.last}">,</c:if></c:forEach>],
      membersB: [<c:forEach var="name" items="${m.membersB}" varStatus="ms">"${name}"<c:if test="${!ms.last}">,</c:if></c:forEach>]
    }<c:if test="${!vs.last}">,</c:if>
    </c:forEach>
  ];
</script>

<script>
  let addMemberTeamId = null;

  /* ── 알림 메시지 ── */
  function showMsg(msg, type) {
    const el = document.getElementById('at-msg');
    el.textContent = msg;
    el.className = 'at-msg ' + type;
    el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    setTimeout(() => { el.className = 'at-msg'; }, 3500);
  }

  /* ══════════════════════════════════════
     팀경연 Excel 일괄 등록
     양식: A=조이름 B=1팀명 C=1팀이미지 D=1팀원(쉼표) E=2팀명 F=2팀이미지 G=2팀원(쉼표)
  ══════════════════════════════════════ */
  var roundExcelRows = [];  // 파싱된 행 데이터 보관
  var roundExcelWb   = null; // 로드된 workbook 보관 (시트 전환용)

  function handleRoundExcelUpload() {
    var fileInput = document.getElementById('excel-round-input');
    var file = fileInput.files[0];
    if (!file) return;

    var reader = new FileReader();
    reader.onload = function(e) {
      try {
        roundExcelWb = XLSX.read(e.target.result, { type: 'binary' });
        fileInput.value = '';

        if (roundExcelWb.SheetNames.length === 1) {
          document.getElementById('excel-sheet-select-box').style.display = 'none';
          parseSheetByName(roundExcelWb.SheetNames[0]);
        } else {
          var select = document.getElementById('excel-sheet-select');
          select.innerHTML = '';
          roundExcelWb.SheetNames.forEach(function(name) {
            var opt = document.createElement('option');
            opt.value = name;
            opt.textContent = name;
            select.appendChild(opt);
          });
          document.getElementById('excel-sheet-select-box').style.display = 'block';
          parseSheetByName(roundExcelWb.SheetNames[0]);
        }
      } catch (err) {
        showMsg('Excel 파일 읽기 실패', 'error');
        fileInput.value = '';
      }
    };
    reader.readAsBinaryString(file);
  }

  function parseSelectedSheet() {
    var name = document.getElementById('excel-sheet-select').value;
    parseSheetByName(name);
  }

  function parseSheetByName(sheetName) {
    if (!roundExcelWb) return;
    try {
      var ws   = roundExcelWb.Sheets[sheetName];
      var rows = XLSX.utils.sheet_to_json(ws, { header: 1 });

      var dataRows = rows.slice(3).filter(function(row) {
    	  return row[0] !== undefined && String(row[0]).trim() !== '';
      });

      if (dataRows.length > 0) {
        var firstA = String(dataRows[0][0] || '').trim();
        var firstB = String(dataRows[0][1] || '').trim();
        if (firstA.indexOf('조이름') !== -1 || firstB.indexOf('팀명') !== -1 || firstB.indexOf('팀 이름') !== -1) {
          dataRows.shift();
        }
      }

      roundExcelRows = [];
      var errors = [];

      dataRows.forEach(function(row, i) {
        var matchName  = row[0] !== undefined ? String(row[0]).trim() : '';
        var teamAName  = row[1] !== undefined ? String(row[1]).trim() : '';
        var teamAImg   = row[2] !== undefined ? String(row[2]).trim() : '';
        var teamANames = row[3] !== undefined ? String(row[3]).trim() : '';
        var teamBName  = row[4] !== undefined ? String(row[4]).trim() : '';
        var teamBImg   = row[5] !== undefined ? String(row[5]).trim() : '';
        var teamBNames = row[6] !== undefined ? String(row[6]).trim() : '';

        if (!matchName || !teamAName || !teamBName) {
          errors.push((i + 1) + '행: 조이름 또는 팀 이름이 비어있어요.');
          return;
        }

        var membersA = teamANames ? teamANames.split(',').map(function(n) { return n.trim(); }).filter(function(n) { return n !== ''; }) : [];
        var membersB = teamBNames ? teamBNames.split(',').map(function(n) { return n.trim(); }).filter(function(n) { return n !== ''; }) : [];

        var imgUrlA = '';
        if (teamAImg) { imgUrlA = teamAImg.indexOf('/') === -1 ? '/teamImages/' + teamAImg : teamAImg; }
        var imgUrlB = '';
        if (teamBImg) { imgUrlB = teamBImg.indexOf('/') === -1 ? '/teamImages/' + teamBImg : teamBImg; }

        roundExcelRows.push({ matchName: matchName, teamAName: teamAName, teamAImgUrl: imgUrlA, membersA: membersA, teamBName: teamBName, teamBImgUrl: imgUrlB, membersB: membersB });
      });

      renderRoundExcelPreview(errors);

    } catch (err) {
      showMsg('시트 파싱 실패', 'error');
    }
  }
  function renderRoundExcelPreview(errors) {
    if (roundExcelRows.length === 0 && errors.length === 0) {
      showMsg('데이터가 없어요. Excel 내용을 확인해 주세요.', 'error');
      return;
    }

    var html = '<table style="width:100%; border-collapse:collapse; font-size:12px;">'
      + '<thead><tr style="background:#e8eeff; text-align:left;">'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">조</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">🔵 1팀명</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">1팀 이미지</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">1팀원</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">🔴 2팀명</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">2팀 이미지</th>'
      + '<th style="padding:7px 10px; border:1px solid #d0d9f0;">2팀원</th>'
      + '</tr></thead><tbody>';

    roundExcelRows.forEach(function(r) {
      html += '<tr style="border-bottom:1px solid #eef0f8;">'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff; font-weight:700;">' + r.matchName + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff; color:#1a5ca8;">' + r.teamAName + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff; color:#888;">' + (r.teamAImgUrl || '<span style="color:#ccc;">없음</span>') + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff;">' + (r.membersA.length > 0 ? r.membersA.join(', ') : '<span style="color:#ccc;">없음</span>') + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff; color:#a81a1a;">' + r.teamBName + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff; color:#888;">' + (r.teamBImgUrl || '<span style="color:#ccc;">없음</span>') + '</td>'
        + '<td style="padding:7px 10px; border:1px solid #e8eeff;">' + (r.membersB.length > 0 ? r.membersB.join(', ') : '<span style="color:#ccc;">없음</span>') + '</td>'
        + '</tr>';
    });

    html += '</tbody></table>';

    if (errors.length > 0) {
      html += '<div style="margin-top:10px; padding:8px 12px; background:#fff3f3; border-radius:6px; border:1px solid #ffcdd2;">'
        + '<p style="font-size:12px; font-weight:700; color:#c62828; margin-bottom:4px;">⚠ 오류 행 (등록 제외)</p>';
      errors.forEach(function(e) {
        html += '<p style="font-size:11px; color:#c62828; margin:2px 0;">' + e + '</p>';
      });
      html += '</div>';
    }

    document.getElementById('excel-round-preview-table').innerHTML = html;
    document.getElementById('excel-round-preview-summary').textContent = roundExcelRows.length + '개 대결 등록 예정' + (errors.length > 0 ? '  |  오류 ' + errors.length + '행 제외' : '');
    document.getElementById('excel-round-preview').style.display = 'block';
  }

  function closeRoundExcelPreview() {
    document.getElementById('excel-round-preview').style.display = 'none';
    document.getElementById('excel-sheet-select-box').style.display = 'none';
    roundExcelRows = [];
    roundExcelWb   = null;
  }

  function confirmRoundExcelUpload() {
    if (roundExcelRows.length === 0) { showMsg('등록할 데이터가 없어요.', 'error'); return; }

    var btn = document.querySelector('#excel-round-preview .btn-purple');
    btn.disabled = true;
    btn.textContent = '등록 중...';

    var total = roundExcelRows.length;
    var done  = 0;
    var failed = 0;

    // 행마다 순차 처리: match/create → A팀 add-bulk → B팀 add-bulk
    function processRow(idx) {
      if (idx >= roundExcelRows.length) {
        closeRoundExcelPreview();
        refreshMatches();
        showMsg(done + '개 대결 등록 완료!' + (failed > 0 ? ' (' + failed + '개 실패)' : '') + ' 🎉', done > 0 ? 'success' : 'error');
        btn.disabled = false;
        btn.textContent = '일괄 등록';
        return;
      }

      var r = roundExcelRows[idx];

      // 1. match/create (팀도 함께 생성됨)
      fetch('/admin/audition/' + AUDITION_ID + '/match/create', {
        method: 'POST',
        body: new URLSearchParams({ matchName: r.matchName, teamAName: r.teamAName, teamAImgUrl: r.teamAImgUrl, teamBName: r.teamBName, teamBImgUrl: r.teamBImgUrl })
      })
      .then(function(res) { return res.text(); })
      .then(function(text) {
        if (text !== 'success') { failed++; processRow(idx + 1); return; }

        // match 생성 후 팀 ID를 가져와야 하므로 matches 재조회
        fetch('/admin/audition/' + AUDITION_ID + '/matches')
        .then(function(res) { return res.json(); })
        .then(function(matches) {
          // 방금 등록한 대결 찾기 (matchName + teamAName으로 식별)
          var created = null;
          for (var i = matches.length - 1; i >= 0; i--) {
            if (matches[i].matchName === r.matchName && matches[i].teamAName === r.teamAName) {
              created = matches[i];
              break;
            }
          }

          if (!created) { done++; processRow(idx + 1); return; }

          // 2. 차수 전체 idol 목록으로 이름→idolId 매칭 후 팀원 등록
          // available-idols 대신 전체 idol 목록 사용:
          // available-idols는 이미 배정된 idol을 제외하므로
          // A팀 등록 후 B팀 조회 시 A팀원이 목록에서 사라지는 문제 방지
          fetch('/admin/audition/' + AUDITION_ID + '/idols')
          .then(function(res) { return res.json(); })
          .then(function(allIdols) {

            function assignMembers(teamId, memberNames, callback) {
              if (memberNames.length === 0) { callback(); return; }
              var ids = [];
              memberNames.forEach(function(name) {
                var hit = allIdols.filter(function(row) { return row[2] === name; });
                if (hit.length > 0) ids.push(String(hit[0][0].idolId));
              });
              if (ids.length === 0) { callback(); return; }
              var params = new URLSearchParams();
              ids.forEach(function(id) { params.append('idolIds', id); });
              fetch('/admin/team/' + teamId + '/members/add-bulk', { method: 'POST', body: params })
              .then(function() { callback(); })
              .catch(function() { callback(); });
            }

            assignMembers(created.teamAId, r.membersA, function() {
              assignMembers(created.teamBId, r.membersB, function() {
                done++;
                processRow(idx + 1);
              });
            });
          })
          .catch(function() { done++; processRow(idx + 1); });
        });
      })
      .catch(function() { failed++; processRow(idx + 1); });
    }

    processRow(0);
  }
  
  /* ── 대결 목록 새로고침 ── */
  function refreshMatches() {
    fetch('/admin/audition/' + AUDITION_ID + '/matches')
      .then(r => r.json())
      .then(matches => renderTeamMatches(matches))
      .catch(() => showMsg('목록 조회 실패', 'error'));
  }

  /* ════════════════════════════════════════
     대결 목록 렌더링
     ✅ 백틱(`) 안의 JS 변수 참조는 전부 \${} 로 이스케이프
     ✅ JSP EL은 백틱 밖의 일반 문자열/HTML에서만 사용
  ════════════════════════════════════════ */
  function renderTeamMatches(matches) {
    const container = document.getElementById('tm-match-list');
    if (!matches || matches.length === 0) {
      container.innerHTML = '<div class="tm-empty">등록된 팀 대결이 없어요. "+ 대결 등록"으로 추가하세요.</div>';
      return;
    }

    container.innerHTML = matches.map(function(m) {
      const isDone = m.status === 'done';
      const aWin   = m.winnerTeamId === m.teamAId;
      const aScore = m.teamAScore || '0.00';
      const bScore = m.teamBScore || '0.00';

      // 팀 이미지
      const imgA = m.teamAImgUrl
        ? '<img src="' + m.teamAImgUrl + '" class="tm-img-preview" alt="' + m.teamAName + '">'
        : '<div class="tm-img-placeholder">&#128101;</div>';
      const imgB = m.teamBImgUrl
        ? '<img src="' + m.teamBImgUrl + '" class="tm-img-preview" alt="' + m.teamBName + '">'
        : '<div class="tm-img-placeholder">&#128101;</div>';

      // 팀원 칩
      const membersAHtml = (m.membersA || []).length > 0
        ? m.membersA.map(function(n) {
            return '<span class="tm-member-chip"><span>' + n + '</span></span>';
          }).join('')
        : '<span style="color:#bbb;font-size:11px">팀원 없음</span>';
      const membersBHtml = (m.membersB || []).length > 0
        ? m.membersB.map(function(n) {
            return '<span class="tm-member-chip"><span>' + n + '</span></span>';
          }).join('')
        : '<span style="color:#bbb;font-size:11px">팀원 없음</span>';

      // 결과 영역
      var resultHtml;
      if (isDone) {
   	    resultHtml = '<div class="tm-result-done">'
   		  + '<span class="tm-done-badge">✅ 결과 확정</span>'
   		  + '<div class="tm-score-display">'
   		  + '  <span class="' + (aWin ? 'win' : 'lose') + '">' + aScore + '%</span>'
   		  + '  <span class="vs-sep">vs</span>'
   		  + '  <span class="' + (!aWin ? 'win' : 'lose') + '">' + bScore + '%</span>'
   		  + '</div>'
   		  + '<span class="tm-winner-label">🏆 ' + (aWin ? m.teamAName : m.teamBName) + ' 승리</span>'
   		  + '<button class="btn btn-sm" style="margin-top:8px;background:#f44336;color:#fff;" onclick="resetResult(' + m.matchId + ')">결과 초기화</button>'
   		  + '</div>';
      } else {
        resultHtml = '<div class="tm-result-row">'
          + '  <div class="form-group">'
          + '    <label>' + m.teamAName + ' 득표율 (%)</label>'
          + '    <input type="number" id="score-a-' + m.matchId + '" step="0.01" min="0" max="100" placeholder="예) 55.00">'
          + '  </div>'
          + '  <div class="form-group">'
          + '    <label>' + m.teamBName + ' 득표율 (%)</label>'
          + '    <input type="number" id="score-b-' + m.matchId + '" step="0.01" min="0" max="100" placeholder="예) 45.00">'
          + '  </div>'
          + '  <div class="form-group">'
          + '    <label>승리팀</label>'
          + '    <select id="winner-' + m.matchId + '">'
          + '      <option value="">-- 선택 --</option>'
          + '      <option value="' + m.teamAId + '">' + m.teamAName + '</option>'
          + '      <option value="' + m.teamBId + '">' + m.teamBName + '</option>'
          + '    </select>'
          + '  </div>'
          + '  <div class="tm-result-btn-wrap">'
          + '    <button class="btn btn-success btn-sm" onclick="submitResult(' + m.matchId + ')">'
          + '      결과 확정 + 가산점 자동 생성'
          + '    </button>'
          + '  </div>'
          + '</div>';
      }

      // 팀원 배정 버튼 (done 상태면 숨김)
	  const addBtnA = !isDone
	    ? '<div style="display:flex; gap:4px;">'
	      + '<button class="btn btn-primary btn-sm" onclick="openAddMemberModal(' + m.teamAId + ', \'' + m.teamAName + '\')">+ 팀원</button>'
	      + '<button class="btn btn-secondary btn-sm" onclick="openEditTeamModal(' + m.teamAId + ', \'' + m.teamAName + '\', \'' + (m.teamAImgUrl || '') + '\')">수정</button>'
		  + '</div>'	    
	    : '';
	  const addBtnB = !isDone
	    ? '<div style="display:flex; gap:4px;">'
	      + '<button class="btn btn-primary btn-sm" onclick="openAddMemberModal(' + m.teamBId + ', \'' + m.teamBName + '\')">+ 팀원</button>'
	      + '<button class="btn btn-secondary btn-sm" onclick="openEditTeamModal(' + m.teamBId + ', \'' + m.teamBName + '\', \'' + (m.teamBImgUrl || '') + '\')">수정</button>'
	      + '</div>'
	    : '';

      return '<div class="tm-match-card">'
        + '  <div class="tm-match-header">'
        + '    <span class="tm-match-name">📋 ' + m.matchName + '</span>'
        + '    <span class="badge badge-' + m.status + '">' + (isDone ? '완료' : '대기중') + '</span>'
        + '  </div>'
        + '  <div class="tm-teams-grid">'
        + '    <div class="tm-team-box">'
        + '      <div class="tm-team-title"><span>🔵 ' + m.teamAName + '</span>' + addBtnA + '</div>'
        + '      ' + imgA
        + '      <div class="tm-member-wrap">' + membersAHtml + '</div>'
        + '    </div>'
        + '    <div class="tm-team-box">'
        + '      <div class="tm-team-title"><span>🔴 ' + m.teamBName + '</span>' + addBtnB + '</div>'
        + '      ' + imgB
        + '      <div class="tm-member-wrap">' + membersBHtml + '</div>'
        + '    </div>'
        + '  </div>'
        + '  ' + resultHtml
        + '</div>';
    }).join('');
  }

  /* ── 대결 등록 폼 ── */
  function openCreateMatchForm() {
    document.getElementById('form-create-match').classList.add('open');
    ['m-matchName','m-teamAName','m-teamBName','m-teamAImgUrl','m-teamBImgUrl']
      .forEach(function(id) { document.getElementById(id).value = ''; });
    ['m-teamAImg','m-teamBImg']
      .forEach(function(id) { document.getElementById(id).value = ''; });
    ['m-teamAImg-status','m-teamBImg-status']
      .forEach(function(id) { document.getElementById(id).textContent = ''; });
    ['m-teamAImg-preview','m-teamBImg-preview']
      .forEach(function(id) { document.getElementById(id).style.display = 'none'; });
    ['m-teamA-count','m-teamB-count']
      .forEach(function(id) { document.getElementById(id).textContent = ''; });
    loadCreateFormIdols();
  }

  /* 대결 등록 폼용 참가자 목록 로드 */
  function loadCreateFormIdols() {
    ['m-teamA-checkbox-list','m-teamB-checkbox-list'].forEach(function(id) {
      document.getElementById(id).innerHTML = '<p style="color:#aaa;font-size:12px;">불러오는 중...</p>';
    });

    fetch('/admin/audition/' + AUDITION_ID + '/idols')
      .then(function(r) { return r.json(); })
      .then(function(allIdols) {
        if (allIdols.length === 0) {
          var msg = '<p style="color:#aaa;font-size:12px;">등록된 참가자가 없어요.</p>';
          document.getElementById('m-teamA-checkbox-list').innerHTML = msg;
          document.getElementById('m-teamB-checkbox-list').innerHTML = msg;
          return;
        }

        function buildCheckboxList(listId, countId, prefix) {
          var html = allIdols.map(function(row) {
            var idolId = row[0].idolId;
            var name   = row[2];
            return '<label style="display:flex;align-items:center;gap:8px;padding:5px 4px;cursor:pointer;">'
              + '<input type="checkbox" class="' + prefix + '-member-cb" value="' + idolId + '">'
              + '<span style="font-size:13px;">' + name + '</span>'
              + '</label>';
          }).join('');
          document.getElementById(listId).innerHTML = html;

          document.getElementById(listId).querySelectorAll('.' + prefix + '-member-cb').forEach(function(cb) {
            cb.addEventListener('change', function() {
              var cnt = document.getElementById(listId).querySelectorAll('.' + prefix + '-member-cb:checked').length;
              document.getElementById(countId).textContent = cnt > 0 ? cnt + '명 선택됨' : '';
            });
          });
        }

        buildCheckboxList('m-teamA-checkbox-list', 'm-teamA-count', 'form-a');
        buildCheckboxList('m-teamB-checkbox-list', 'm-teamB-count', 'form-b');
      })
      .catch(function() {
        var msg = '<p style="color:#c62828;font-size:12px;">목록 조회 실패</p>';
        document.getElementById('m-teamA-checkbox-list').innerHTML = msg;
        document.getElementById('m-teamB-checkbox-list').innerHTML = msg;
      });
  }
  function closeCreateMatchForm() {
    document.getElementById('form-create-match').classList.remove('open');
  }

  /* ── 팀 이미지 업로드 ── */
  function uploadTeamImg(team) {
    const fileInput = document.getElementById('m-team' + team + 'Img');
    const statusEl  = document.getElementById('m-team' + team + 'Img-status');
    const previewEl = document.getElementById('m-team' + team + 'Img-preview');
    const hiddenUrl = document.getElementById('m-team' + team + 'ImgUrl');
    if (!fileInput.files || fileInput.files.length === 0) return;
    statusEl.textContent = '업로드 중...';
    statusEl.style.color = '#f57c00';
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    fetch('/admin/team/image/upload', { method: 'POST', body: formData })
      .then(function(r) { return r.text(); })
      .then(function(url) {
        if (url.startsWith('error')) {
          statusEl.textContent = '실패'; statusEl.style.color = '#c62828';
          showMsg(url, 'error');
        } else {
          hiddenUrl.value = url;
          statusEl.textContent = '완료 ✓'; statusEl.style.color = '#2e7d32';
          previewEl.src = url; previewEl.style.display = 'block';
        }
      })
      .catch(function() { statusEl.textContent = '실패'; statusEl.style.color = '#c62828'; });
  }

  /* ── 대결 등록 ── */
  function createTeamMatch() {
    const matchName   = document.getElementById('m-matchName').value.trim();
    const teamAName   = document.getElementById('m-teamAName').value.trim();
    const teamBName   = document.getElementById('m-teamBName').value.trim();
    const teamAImgUrl = document.getElementById('m-teamAImgUrl').value;
    const teamBImgUrl = document.getElementById('m-teamBImgUrl').value;
    if (!matchName || !teamAName || !teamBName) {
      showMsg('대결 이름과 팀 이름을 모두 입력해 주세요.', 'error'); return;
    }
    if (document.getElementById('m-teamAImg-status').textContent === '업로드 중...' ||
        document.getElementById('m-teamBImg-status').textContent === '업로드 중...') {
      showMsg('이미지 업로드가 완료될 때까지 기다려 주세요.', 'error'); return;
    }

    // 체크된 팀원 ID 수집
    var idsA = Array.from(document.getElementById('m-teamA-checkbox-list').querySelectorAll('.form-a-member-cb:checked'))
                   .map(function(cb) { return cb.value; });
    var idsB = Array.from(document.getElementById('m-teamB-checkbox-list').querySelectorAll('.form-b-member-cb:checked'))
                   .map(function(cb) { return cb.value; });

    var btn = document.querySelector('#form-create-match .btn-purple');
    btn.disabled = true;
    btn.textContent = '등록 중...';

    // ① match 생성
    fetch('/admin/audition/' + AUDITION_ID + '/match/create', {
      method: 'POST',
      body: new URLSearchParams({ matchName: matchName, teamAName: teamAName, teamAImgUrl: teamAImgUrl, teamBName: teamBName, teamBImgUrl: teamBImgUrl })
    })
    .then(function(r) { return r.text(); })
    .then(function(res) {
      if (res !== 'success') {
        showMsg('등록 실패: ' + res, 'error');
        btn.disabled = false; btn.textContent = '등록';
        return;
      }

      // ② 방금 등록한 match의 팀 ID 확인
      fetch('/admin/audition/' + AUDITION_ID + '/matches')
      .then(function(r) { return r.json(); })
      .then(function(matches) {
        var created = null;
        for (var i = matches.length - 1; i >= 0; i--) {
          if (matches[i].matchName === matchName && matches[i].teamAName === teamAName) {
            created = matches[i]; break;
          }
        }

        function assignMembers(teamId, ids, callback) {
          if (!teamId || ids.length === 0) { callback(); return; }
          var params = new URLSearchParams();
          ids.forEach(function(id) { params.append('idolIds', id); });
          fetch('/admin/team/' + teamId + '/members/add-bulk', { method: 'POST', body: params })
            .then(function() { callback(); })
            .catch(function() { callback(); });
        }

        function finish() {
          var memberMsg = (idsA.length + idsB.length) > 0
            ? ' (팀원 ' + (idsA.length + idsB.length) + '명 배정됨)' : '';
          showMsg('대결이 등록됐어요.' + memberMsg, 'success');
          closeCreateMatchForm();
          refreshMatches();
          btn.disabled = false; btn.textContent = '등록';
        }

        if (!created) { finish(); return; }

        // ③ A팀 → B팀 순차 배정
        assignMembers(created.teamAId, idsA, function() {
          assignMembers(created.teamBId, idsB, finish);
        });
      })
      .catch(function() {
        showMsg('대결 등록됐지만 팀원 배정 중 오류가 발생했어요.', 'error');
        btn.disabled = false; btn.textContent = '등록';
        refreshMatches();
      });
    })
    .catch(function() {
      showMsg('네트워크 오류가 발생했어요.', 'error');
      btn.disabled = false; btn.textContent = '등록';
    });
  }

  /* ── 결과 확정 → VoteBonus 자동 생성 ── */
  function submitResult(matchId) {
    const scoreA   = document.getElementById('score-a-' + matchId).value;
    const scoreB   = document.getElementById('score-b-' + matchId).value;
    const winnerId = document.getElementById('winner-' + matchId).value;
    if (!scoreA || !scoreB || !winnerId) {
      showMsg('득표율과 승리팀을 모두 입력해 주세요.', 'error'); return;
    }
    if (!confirm('결과를 확정하면 승리팀 멤버 전원에게 가산점이 자동 생성돼요. 진행할까요?')) return;
    fetch('/admin/match/' + matchId + '/result', {
      method: 'POST',
      body: new URLSearchParams({ winnerTeamId: winnerId, teamAScore: scoreA, teamBScore: scoreB })
    })
    .then(function(r) { return r.text(); })
    .then(function(res) {
      if (res === 'success') {
        showMsg('결과가 확정되고 가산점이 자동 생성됐어요! 🎉', 'success');
        refreshMatches();
      } else {
        showMsg('실패: ' + res, 'error');
      }
    });
  }

    /* ── 팀원 배정 모달 ── */
	function openAddMemberModal(teamId, teamName) {
	    addMemberTeamId = teamId;
	    document.getElementById('add-member-desc').textContent =
	        '"' + teamName + '"에 배정할 참가자를 선택하세요.';
	    document.getElementById('modal-add-member').style.display = 'flex';
	    refreshCurrentMembers(teamId);
	    loadAvailableIdols(teamId);
	}
	
	function loadAvailableIdols(teamId) {
	    const listEl = document.getElementById('add-member-checkbox-list');
	    const countEl = document.getElementById('add-member-count');
	    listEl.innerHTML = '<p style="color:#aaa;font-size:12px;">불러오는 중...</p>';
	    countEl.textContent = '';
	
	    fetch('/admin/team/' + teamId + '/available-idols?auditionId=' + AUDITION_ID)
	        .then(function(r) { return r.json(); })
	        .then(function(data) {
	            if (data.length === 0) {
	                listEl.innerHTML = '<p style="color:#aaa;font-size:12px;">배정 가능한 참가자가 없어요.</p>';
	                return;
	            }
	            listEl.innerHTML = data.map(function(row) {
	                // row[0]=IdolDto, row[1]=voteCount, row[2]=name
	                var idolId = row[0].idolId;
	                var name   = row[2];
	                return '<label style="display:flex;align-items:center;gap:8px;padding:6px 4px;cursor:pointer;"><input type="checkbox" class="member-cb" value="' + idolId + '"><span>' + name + '</span></label>';
	            }).join('');
	
	            // 선택 인원 실시간 카운트
	            listEl.querySelectorAll('.member-cb').forEach(function(cb) {
	                cb.addEventListener('change', function() {
	                    var cnt = listEl.querySelectorAll('.member-cb:checked').length;
	                    countEl.textContent = cnt > 0 ? cnt + '명 선택됨' : '';
	                });
	            });
	        })
	        .catch(function() {
	            listEl.innerHTML = '<p style="color:#c62828;font-size:12px;">목록 조회 실패</p>';
	        });
	}
  
	function refreshCurrentMembers(teamId) {
	    fetch('/admin/team/' + teamId + '/members')
	      .then(function(r) { return r.json(); })
	      .then(function(data) {
	        const wrap = document.getElementById('add-member-current');
	        if (data.length === 0) {
	          wrap.innerHTML = '<p style="font-size:12px;color:#aaa;">아직 배정된 팀원이 없어요.</p>';
	          return;
	        }
	        // ✅ 문자열 연결로 작성 → 백틱 / JSP EL 충돌 없음
	        var html = '<p style="font-size:12px;font-weight:700;color:#555;margin-bottom:8px;">현재 팀원</p><div>';
	        data.forEach(function(row) {
	          html += '<span class="tm-member-chip">'
	               +    '<span>' + row[2] + '</span>'
	               +    '<button onclick="removeMember(' + row[0] + ',' + teamId + ')">×</button>'
	               +  '</span>';
	        });
	        html += '</div>';
	        wrap.innerHTML = html;
	      });
	  }
  function closeAddMemberModal() {
    document.getElementById('modal-add-member').style.display = 'none';
    addMemberTeamId = null;
    refreshMatches();
  }
  
	function addMembersBulk() {
	    const listEl = document.getElementById('add-member-checkbox-list');
	    const checked = listEl.querySelectorAll('.member-cb:checked');
	    if (checked.length === 0) { showMsg('참가자를 1명 이상 선택해 주세요.', 'error'); return; }

	    const idolIds = Array.from(checked).map(function(cb) { return cb.value; });
	    const params = new URLSearchParams();
	    idolIds.forEach(function(id) { params.append('idolIds', id); });

	    fetch('/admin/team/' + addMemberTeamId + '/members/add-bulk', {
	        method: 'POST', body: params
	    })
	    .then(function(r) { return r.text(); })
	    .then(function(res) {
	        if (res === 'success') {
	            showMsg(idolIds.length + '명이 배정됐어요. 🎉', 'success');
	            refreshCurrentMembers(addMemberTeamId);
	            loadAvailableIdols(addMemberTeamId);  // 목록 갱신
	        } else {
	            showMsg('실패: ' + res, 'error');
	        }
	    });
	}

  function removeMember(teamMemberId, teamId) {
    if (!confirm('팀원을 제거하시겠어요?')) return;
    fetch('/admin/team/member/' + teamMemberId + '/remove', { method: 'POST' })
      .then(function(r) { return r.text(); })
      .then(function(res) {
        if (res === 'success') { showMsg('팀원이 제거됐어요.', 'success'); refreshCurrentMembers(teamId); }
        else showMsg('실패: ' + res, 'error');
      });
  }

  /* ── 팀 정보 수정 모달 ── */
  let editTeamId = null;

  function openEditTeamModal(teamId, teamName, teamImgUrl) {
    editTeamId = teamId;
    document.getElementById('edit-team-name').value = teamName;
    document.getElementById('edit-team-img-preview').src = teamImgUrl || '';
    document.getElementById('edit-team-img-preview').style.display = teamImgUrl ? 'block' : 'none';
    document.getElementById('edit-team-img-url').value = teamImgUrl || '';
    document.getElementById('edit-team-img-status').textContent = '';
    document.getElementById('modal-edit-team').style.display = 'flex';
  }
  function closeEditTeamModal() {
    document.getElementById('modal-edit-team').style.display = 'none';
    editTeamId = null;
  }
  function uploadEditTeamImg() {
    const fileInput = document.getElementById('edit-team-img-file');
    const statusEl  = document.getElementById('edit-team-img-status');
    const previewEl = document.getElementById('edit-team-img-preview');
    const hiddenUrl = document.getElementById('edit-team-img-url');
    if (!fileInput.files || fileInput.files.length === 0) return;
    statusEl.textContent = '업로드 중...';
    statusEl.style.color = '#f57c00';
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    fetch('/admin/team/image/upload', { method: 'POST', body: formData })
      .then(function(r) { return r.text(); })
      .then(function(url) {
        if (url.startsWith('error')) {
          statusEl.textContent = '실패'; statusEl.style.color = '#c62828';
          showMsg(url, 'error');
        } else {
          hiddenUrl.value = url;
          statusEl.textContent = '완료 ✓'; statusEl.style.color = '#2e7d32';
          previewEl.src = url; previewEl.style.display = 'block';
        }
      })
      .catch(function() { statusEl.textContent = '실패'; statusEl.style.color = '#c62828'; });
  }
  function saveEditTeam() {
    const teamName = document.getElementById('edit-team-name').value.trim();
    const imgUrl   = document.getElementById('edit-team-img-url').value;
    if (!teamName) { showMsg('팀 이름을 입력해 주세요.', 'error'); return; }
    fetch('/admin/team/' + editTeamId + '/update', {
      method: 'POST',
      body: new URLSearchParams({ teamName: teamName, teamImgUrl: imgUrl })
    })
    .then(function(r) { return r.text(); })
    .then(function(res) {
      if (res === 'success') {
        showMsg('팀 정보가 수정됐어요.', 'success');
        closeEditTeamModal();
        refreshMatches();
      } else {
        showMsg('실패: ' + res, 'error');
      }
    });
  }

  /* ── 결과 초기화 ── */
  function resetResult(matchId) {
    if (!confirm('결과를 초기화하면 가산점도 함께 삭제돼요. 진행할까요?')) return;
    fetch('/admin/match/' + matchId + '/reset', { method: 'POST' })
      .then(function(r) { return r.text(); })
      .then(function(res) {
        if (res === 'success') {
          showMsg('결과가 초기화됐어요.', 'success');
          refreshMatches();
        } else {
          showMsg('실패: ' + res, 'error');
        }
      });
  }
  /* ── 페이지 진입 시 초기 렌더링 ── */
  renderTeamMatches(initialMatches);
  
  /* ════ Excel 양식 안내 모달 (팀경연) ════ */
  function openTeamTemplateModal() {
    document.getElementById('modal-team-template').style.display = 'flex';
  }
  function closeTeamTemplateModal() {
    document.getElementById('modal-team-template').style.display = 'none';
  }
  function downloadTeamTemplate() {
    var wb = XLSX.utils.book_new();
    var data = [
      ['🏆  팀경연 대결 등록폼  —  ACTION 101'],
      [''],
      ['','🔵  1 팀','','','🔴  2 팀','',''],
      ['조이름','1팀명','1팀 이미지파일명','1팀원 (쉼표 구분)','2팀명','2팀 이미지파일명','2팀원 (쉼표 구분)'],
      ['※ 이미지파일명은 파일명만 입력 (예: 2-A-N.jpg) — 생략 가능 / 팀원은 쉼표(,)로 구분 / 1~7행은 수정하지 마세요'],
      [''],
      ['A조','NOVA','2-A-N.jpg','김지수, 오세진, 박서준, 최유나, 이준혁, 한채원, 정민서, 윤소아','ECLIPSE','2-A-E.jpg','박민준, 임도현, 송지아, 윤태양, 강민하, 류찬혁, 김하율, 오지혜'],
      ['▲ 예시행(7행)의 내용은 실제 입력 시 삭제하고 사용하세요.']
    ];
    var ws = XLSX.utils.aoa_to_sheet(data);
    ws['!cols'] = [10,14,18,42,14,18,42].map(function(w){return{wch:w};});
    ws['!merges'] = [
      {s:{r:0,c:0},e:{r:0,c:6}},
      {s:{r:2,c:1},e:{r:2,c:3}},
      {s:{r:2,c:4},e:{r:2,c:6}},
      {s:{r:4,c:0},e:{r:4,c:6}},
      {s:{r:7,c:0},e:{r:7,c:6}}
    ];
    XLSX.utils.book_append_sheet(wb, ws, '팀경연_등록폼');
    XLSX.writeFile(wb, '팀경연_등록폼.xlsx');
  }
</script>

</body>
</html>
