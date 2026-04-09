<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>${audition['round']}차 팀경연 관리 — ACTION 101</title>
  <link href="<c:url value='/css/audition/team.css'/>" rel="stylesheet">
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
      <button class="btn btn-purple btn-sm" style="float:right;"
              onclick="openCreateMatchForm()">+ 대결 등록</button>
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
    <div class="form-group" style="margin-bottom:16px;">
      <label>참가자 선택</label>
      <select id="add-member-idol-select"
              style="padding:8px 12px; border:1px solid #d0d0d0; border-radius:6px; font-size:13px; width:100%;">
        <option value="">-- 참가자를 선택하세요 --</option>
        <%-- JSP EL로 서버 데이터 출력 → 백틱 밖이라 이스케이프 불필요 --%>
        <c:forEach var="row" items="${idols}">
          <c:if test="${row[0].status == 'active'}">
            <option value="${row[0].idolId}">${row[2]}</option>
          </c:if>
        </c:forEach>
      </select>
    </div>
    <div id="add-member-current" style="margin-bottom:8px;"></div>
    <div class="modal-btns">
      <button class="btn btn-secondary" onclick="closeAddMemberModal()">닫기</button>
      <button class="btn btn-purple" onclick="addMemberToTeam()">배정</button>
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
        : '<div class="tm-img-placeholder">🔵</div>';
      const imgB = m.teamBImgUrl
        ? '<img src="' + m.teamBImgUrl + '" class="tm-img-preview" alt="' + m.teamBName + '">'
        : '<div class="tm-img-placeholder">🔴</div>';

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
    fetch('/admin/audition/' + AUDITION_ID + '/match/create', {
      method: 'POST',
      body: new URLSearchParams({ matchName: matchName, teamAName: teamAName, teamAImgUrl: teamAImgUrl, teamBName: teamBName, teamBImgUrl: teamBImgUrl })
    })
    .then(function(r) { return r.text(); })
    .then(function(res) {
      if (res === 'success') {
        showMsg('대결이 등록됐어요.', 'success');
        closeCreateMatchForm();
        refreshMatches();
      } else {
        showMsg('등록 실패: ' + res, 'error');
      }
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
  function addMemberToTeam() {
    const idolId = document.getElementById('add-member-idol-select').value;
    if (!idolId) { showMsg('참가자를 선택해 주세요.', 'error'); return; }
    fetch('/admin/team/' + addMemberTeamId + '/member/add', {
      method: 'POST', body: new URLSearchParams({ idolId: idolId })
    })
    .then(function(r) { return r.text(); })
    .then(function(res) {
      if (res === 'success') {
        showMsg('팀원이 배정됐어요.', 'success');
        document.getElementById('add-member-idol-select').value = '';
        refreshCurrentMembers(addMemberTeamId);
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
</script>

</body>
</html>
