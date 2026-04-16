<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>오디션 회차 관리 — ACTION 101</title>
  <link href="<c:url value='/css/audition/round.css'/>" rel="stylesheet">
</head>

<body>
<%@ include file="/WEB-INF/views/admin/layout/header.jsp" %>

  <div class="page-body">
    <!-- ── 알림 메시지 ── -->
    <div id="at-msg" class="at-msg"></div>

    <!-- ══════════════════════════════════════
         ① 회차 목록
    ══════════════════════════════════════ -->
    <div class="at-section">
      <div class="at-section-title">
        회차 목록
        <button class="btn btn-primary" style="float:right; font-size:12px;"
                onclick="openCreateForm()">+ 회차 등록</button>
      </div>

      <!-- 등록 폼 -->
      <div id="form-create" class="at-form">
        <div style="background:#f8faff; border:1px solid #d0d9f0; border-radius:8px; padding:20px; margin-bottom:20px;">
          <p style="font-size:13px; font-weight:700; color:#1a2c4e; margin-bottom:14px;">신규 회차 등록</p>
          <div class="form-grid">
            <div class="form-group">
              <label>회차 번호</label>
              <input type="number" id="c-round" placeholder="예) 1">
            </div>
            <div class="form-group">
              <label>제목</label>
              <input type="text" id="c-title" placeholder="예) 1차 오디션">
            </div>
            <div class="form-group">
              <label>투표 시작일</label>
              <input type="date" id="c-startDate">
            </div>
            <div class="form-group">
              <label>투표 마감일</label>
              <input type="date" id="c-endDate">
            </div>
            <div class="form-group">
              <label>1인 최대 투표 수</label>
              <input type="number" id="c-maxVoteCount" value="7">
            </div>
            <div class="form-group">
              <label>생존자 커트라인 (명)</label>
              <input type="number" id="c-survivorCount" placeholder="예) 30">
            </div>
            <div class="form-group">
              <label>팀경연 여부</label>
              <select id="c-hasTeamMatch">
                <option value="false">없음</option>
                <option value="true">있음</option>
              </select>
            </div>
            <div class="form-group">
              <label>팀경연 가산점 (표)</label>
              <input type="number" id="c-bonusVotes" value="500" step="1">
            </div>
          </div>
          <div class="form-btns">
            <button class="btn btn-secondary" onclick="closeCreateForm()">취소</button>
            <button class="btn btn-success" onclick="createAudition()">등록</button>
          </div>
        </div>
      </div>

      <!-- 수정 폼 -->
      <div id="form-update" class="at-form">
        <div style="background:#fff8e1; border:1px solid #ffe082; border-radius:8px; padding:20px; margin-bottom:20px;">
          <p style="font-size:13px; font-weight:700; color:#e65100; margin-bottom:14px;">회차 수정</p>
          <input type="hidden" id="u-auditionId">
          <div class="form-grid">
            <div class="form-group">
              <label>회차 번호</label>
              <input type="number" id="u-round">
            </div>
            <div class="form-group">
              <label>제목</label>
              <input type="text" id="u-title">
            </div>
            <div class="form-group">
              <label>투표 시작일</label>
              <input type="date" id="u-startDate">
            </div>
            <div class="form-group">
              <label>투표 마감일</label>
              <input type="date" id="u-endDate">
            </div>
            <div class="form-group">
              <label>1인 최대 투표 수</label>
              <input type="number" id="u-maxVoteCount">
            </div>
            <div class="form-group">
              <label>생존자 커트라인 (명)</label>
              <input type="number" id="u-survivorCount">
            </div>
            <div class="form-group">
              <label>팀경연 여부</label>
              <select id="u-hasTeamMatch">
                <option value="false">없음</option>
                <option value="true">있음</option>
              </select>
            </div>
            <div class="form-group">
              <label>팀경연 가산점 (표)</label>
              <input type="number" id="u-bonusVotes" step="1">
            </div>
          </div>
          <div class="form-btns">
            <button class="btn btn-secondary" onclick="closeUpdateForm()">취소</button>
            <button class="btn btn-warning" onclick="updateAudition()">수정 저장</button>
          </div>
        </div>
      </div>

      <!-- 회차 목록 테이블 -->
      <table class="at-table">
        <thead>
          <tr>
            <th>회차</th>
            <th>제목</th>
            <th>투표기간</th>
            <th>최대투표</th>
            <th>커트라인</th>
            <th>팀경연</th>
            <th>가산점</th>
            <th>상태</th>
            <th>상태변경</th>
            <th>다음회차</th>
            <th>수정</th>
            <th>참가자</th>
            <th>팀경연관리</th><%-- ✅ 추가 --%>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="a" items="${auditionList}">
            <c:set var="sc" value="0"/>
            <c:if test="${a['survivorCount'] != null}">
              <c:set var="sc" value="${a['survivorCount']}"/>
            </c:if>
            <tr>
              <td>${a['round']}차</td>
              <td>${a['title']}</td>
              <td>${a['startDate']} ~ ${a['endDate']}</td>
              <td>${a['maxVoteCount']}명</td>
              <td>
                <c:choose>
                  <c:when test="${a['survivorCount'] != null}">${a['survivorCount']}명</c:when>
                  <c:otherwise><span style="color:#bbb">미설정</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${a['hasTeamMatch'] == 'true'}">있음</c:when>
                  <c:otherwise>없음</c:otherwise>
                </c:choose>
              </td>
              <td>${a['bonusVotes']}표</td>
              <td>
                <span class="badge badge-${a['status']}">
                  <c:choose>
                    <c:when test="${a['status'] == 'ongoing'}">진행중</c:when>
                    <c:when test="${a['status'] == 'ended'}">종료</c:when>
                    <c:otherwise>예정</c:otherwise>
                  </c:choose>
                </span>
              </td>
              <td>
                <c:if test="${a['status'] == 'upcoming'}">
                  <button class="btn btn-success btn-sm"
                          onclick="updateStatus(${a['auditionId']}, 'ongoing')">시작</button>
                </c:if>
                <c:if test="${a['status'] == 'ongoing'}">
                  <button class="btn btn-secondary btn-sm"
                          onclick="updateStatus(${a['auditionId']}, 'ended')">종료</button>
                </c:if>
                <c:if test="${a['status'] == 'ended'}">
                  <span style="color:#bbb; font-size:11px">완료</span>
                </c:if>
              </td>
              <td>
                <c:if test="${a['status'] == 'ended'}">
                  <button class="btn btn-primary btn-sm"
                          onclick="openNextRoundModal(${a['auditionId']}, '${a['title']}')">
                    다음회차
                  </button>
                </c:if>
              </td>
              <td>
                <button class="btn btn-warning btn-sm"
                        onclick="openUpdateForm(${a['auditionId']}, '${a['round']}', '${a['title']}',
                                 '${a['startDate']}', '${a['endDate']}', ${a['maxVoteCount']},
                                 '${sc}', '${a['hasTeamMatch']}', '${a['bonusVotes']}')">
                  수정
                </button>
              </td>
              <td>
                <button class="btn btn-primary btn-sm"
                        onclick="loadIdols(${a['auditionId']}, '${a['title']}', ${sc})">
                  관리
                </button>
              </td>
              <%-- 팀경연 있는 회차만 버튼 표시, 클릭 시 team 페이지로 이동 --%>
              <td>
                <c:choose>
                  <c:when test="${a['hasTeamMatch'] == 'true'}">
                    <a href="/admin/audition/team?auditionId=${a['auditionId']}"
                       class="btn btn-purple btn-sm">팀경연</a>
                  </c:when>
                  <c:otherwise>
                    <span style="color:#bbb; font-size:11px">–</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

	<!-- ══════════════════════════════════════
	     ② 슈퍼계정 투표 배율 설정
	══════════════════════════════════════ -->
	<div class="at-section">
	  <div class="at-section-title">슈퍼계정 투표 배율</div>
	  <div style="display:flex; align-items:center; gap:12px; padding:12px 0;">
	    <span style="font-size:13px; color:#555;">현재 배율:</span>
	    <strong id="multiplier-display" style="font-size:18px; color:#1a2c4e;">로딩중...</strong>
	    <span style="font-size:13px; color:#555;">표 / 1회 투표</span>
	  </div>
	  <div style="display:flex; align-items:center; gap:8px;">
	    <input type="number" id="multiplier-input" min="1" max="1000" value="100"
	           style="width:100px; padding:7px 10px; border:1px solid #d0d0d0;
	                  border-radius:6px; font-size:14px;">
	    <button class="btn btn-primary" onclick="setMultiplier()">변경</button>
	    <button class="btn btn-secondary" onclick="resetMultiplier()">100으로 초기화</button>
	  </div>
	</div>
	
    <!-- ══════════════════════════════════════
         ③ 참가자 관리
    ══════════════════════════════════════ -->
    <div id="section-idols" class="at-section" style="display:none;">
      <div class="at-section-title">
        <span id="idol-section-title">참가자 관리</span>
        <div style="float:right; display:flex; gap:8px;">
          <button class="btn btn-danger btn-sm" onclick="eliminateByRank()">커트라인 일괄 탈락</button>
          <button class="btn btn-secondary btn-sm" onclick="closeIdolSection()">닫기</button>
        </div>
      </div>
      <div id="idol-table-wrap" class="idol-table-wrap">
        <table class="at-table">
          <thead>
            <tr><th>순위</th><th>이름</th><th>득표수</th><th>상태</th><th>처리</th></tr>
          </thead>
          <tbody id="idol-tbody"></tbody>
        </table>
      </div>
    </div>

    <!-- ══════════════════════════════════════
         ③ 다음 회차 참가자 생성 모달
    ══════════════════════════════════════ -->
    <div id="modal-next-round" style="
      display:none; position:fixed; inset:0;
      background:rgba(0,0,0,0.45);
      z-index:9999;
      align-items:center; justify-content:center;">
      <div style="background:white; border-radius:12px; padding:28px 32px; width:380px;
                  box-shadow:0 8px 32px rgba(0,0,0,0.18);">
        <p style="font-size:15px; font-weight:700; color:#1a2c4e; margin:0 0 6px;">
          다음 회차 참가자 등록
        </p>
        <p id="next-round-desc" style="font-size:13px; color:#888; margin:0 0 20px;"></p>
        <div class="form-group" style="margin-bottom:20px;">
          <label>다음 회차 선택</label>
          <select id="next-round-select" style="padding:8px 12px; border:1px solid #d0d0d0;
                  border-radius:6px; font-size:13px; width:100%;">
            <option value="">-- 회차를 선택하세요 --</option>
            <c:forEach var="opt" items="${auditionList}">
              <option value="${opt['auditionId']}">
                ${opt['round']}차 — ${opt['title']} (${opt['status']})
              </option>
            </c:forEach>
          </select>
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end;">
          <button class="btn btn-secondary" onclick="closeNextRoundModal()">취소</button>
          <button class="btn btn-primary" onclick="submitNextRound()">등록</button>
        </div>
      </div>
    </div>

  </div><!-- /page-body -->
</body>

<script>
  /* ════════ 슈퍼계정 배율 ════════ */
  function loadMultiplier() {
    fetch('/admin/super/multiplier')
      .then(r => r.json())
      .then(v => {
        document.getElementById('multiplier-display').textContent = v;
        document.getElementById('multiplier-input').value = v;
      });
  }
  function setMultiplier() {
    const v = document.getElementById('multiplier-input').value;
    if (!v || v < 1 || v > 1000) { showMsg('1~1000 사이 값을 입력해 주세요.', 'error'); return; }
    if (!confirm('슈퍼계정 배율을 ' + v + '표로 변경할까요?')) return;
    fetch('/admin/super/multiplier', {
      method: 'POST', body: new URLSearchParams({ value: v })
    })
    .then(r => r.text())
    .then(res => {
      if (res === 'success') { showMsg('배율이 ' + v + '표로 변경됐어요.', 'success'); loadMultiplier(); }
      else showMsg('변경 실패: ' + res, 'error');
    });
  }
  function resetMultiplier() {
    document.getElementById('multiplier-input').value = 100;
    setMultiplier();
  }

  loadMultiplier(); // 페이지 로드 시 현재값 자동 조회

  let currentAuditionId    = null;
  let currentSurvivorCount = 0;
  let currentRoundId       = null;

  function showMsg(msg, type) {
    const el = document.getElementById('at-msg');
    el.textContent = msg;
    el.className = 'at-msg ' + type;
    setTimeout(() => { el.className = 'at-msg'; }, 3000);
  }

  function reload() { location.href = '/admin/audition/round'; }

  /* ════════ 회차 등록 ════════ */
  function openCreateForm() {
    document.getElementById('form-create').classList.add('open');
    document.getElementById('form-update').classList.remove('open');
  }
  function closeCreateForm() { document.getElementById('form-create').classList.remove('open'); }
  function createAudition() {
    const data = new URLSearchParams({
      round:         document.getElementById('c-round').value,
      title:         document.getElementById('c-title').value,
      startDate:     document.getElementById('c-startDate').value,
      endDate:       document.getElementById('c-endDate').value,
      maxVoteCount:  document.getElementById('c-maxVoteCount').value,
      survivorCount: document.getElementById('c-survivorCount').value,
      hasTeamMatch:  document.getElementById('c-hasTeamMatch').value,
      bonusVotes:    document.getElementById('c-bonusVotes').value,
      status:        'upcoming'
    });
    fetch('/admin/audition/create', { method: 'POST', body: data })
      .then(r => r.text())
      .then(res => {
        if (res === 'success') { showMsg('회차가 등록됐어요.', 'success'); setTimeout(reload, 1000); }
        else showMsg('등록 실패: ' + res, 'error');
      });
  }

  /* ════════ 회차 수정 ════════ */
  function openUpdateForm(id, round, title, startDate, endDate,
                          maxVoteCount, survivorCount, hasTeamMatch, bonusVotes) {
    document.getElementById('form-update').classList.add('open');
    document.getElementById('form-create').classList.remove('open');
    document.getElementById('u-auditionId').value    = id;
    document.getElementById('u-round').value         = round;
    document.getElementById('u-title').value         = title;
    document.getElementById('u-startDate').value     = startDate;
    document.getElementById('u-endDate').value       = endDate;
    document.getElementById('u-maxVoteCount').value  = maxVoteCount;
    document.getElementById('u-survivorCount').value = (survivorCount === 'null' ? '' : survivorCount);
    document.getElementById('u-hasTeamMatch').value  = hasTeamMatch;
    document.getElementById('u-bonusVotes').value    = bonusVotes;
    document.getElementById('form-update').scrollIntoView({ behavior: 'smooth' });
  }
  function closeUpdateForm() { document.getElementById('form-update').classList.remove('open'); }
  function updateAudition() {
    const id = document.getElementById('u-auditionId').value;
    const data = new URLSearchParams({
      round:         document.getElementById('u-round').value,
      title:         document.getElementById('u-title').value,
      startDate:     document.getElementById('u-startDate').value,
      endDate:       document.getElementById('u-endDate').value,
      maxVoteCount:  document.getElementById('u-maxVoteCount').value,
      survivorCount: document.getElementById('u-survivorCount').value,
      hasTeamMatch:  document.getElementById('u-hasTeamMatch').value,
      bonusVotes:    document.getElementById('u-bonusVotes').value,
    });
    fetch('/admin/audition/' + id + '/update', { method: 'POST', body: data })
      .then(r => r.text())
      .then(res => {
        if (res === 'success') { showMsg('수정됐어요.', 'success'); setTimeout(reload, 1000); }
        else showMsg('수정 실패: ' + res, 'error');
      });
  }

  /* ════════ 상태 변경 ════════ */
  function updateStatus(auditionId, status) {
    const label = status === 'ongoing' ? '진행중으로 변경' : '종료';
    if (!confirm(label + ' 하시겠어요?')) return;
    fetch('/admin/audition/' + auditionId + '/status', {
      method: 'POST', body: new URLSearchParams({ status })
    })
    .then(r => r.text())
    .then(res => {
      if (res === 'success') { showMsg('상태가 변경됐어요.', 'success'); setTimeout(reload, 1000); }
      else showMsg('변경 실패: ' + res, 'error');
    });
  }

  /* ════════ 다음 회차 모달 ════════ */
  function openNextRoundModal(auditionId, title) {
    currentRoundId = auditionId;
    document.getElementById('next-round-desc').textContent =
      '"' + title + '" 생존자를 이동할 다음 회차를 선택하세요.';
    document.getElementById('next-round-select').value = '';
    document.getElementById('modal-next-round').style.display = 'flex';
  }
  function closeNextRoundModal() {
    document.getElementById('modal-next-round').style.display = 'none';
    currentRoundId = null;
  }
  function submitNextRound() {
    const nextId = document.getElementById('next-round-select').value;
    if (!nextId) { alert('다음 회차를 선택해 주세요.'); return; }
    const sel = document.getElementById('next-round-select');
    if (!confirm(sel.options[sel.selectedIndex].text.trim() + '으로 생존자를 등록할까요?')) return;
    fetch('/admin/audition/' + currentRoundId + '/nextRound?nextAuditionId=' + nextId, { method: 'POST' })
      .then(r => r.text())
      .then(res => {
        closeNextRoundModal();
        if (res === 'success') { showMsg('다음 회차 참가자가 등록됐어요.', 'success'); setTimeout(reload, 1000); }
        else showMsg('실패: ' + res, 'error');
      });
  }

  /* ════════ 참가자 관리 ════════ */
  function loadIdols(auditionId, title, survivorCount) {
    currentAuditionId    = auditionId;
    currentSurvivorCount = survivorCount;
    document.getElementById('idol-section-title').textContent =
      title + ' — 참가자 관리 (커트라인: ' + (survivorCount || '미설정') + '명)';
    document.getElementById('section-idols').style.display = 'block';
    document.getElementById('section-idols').scrollIntoView({ behavior: 'smooth' });
    fetch('/admin/audition/' + auditionId + '/idols')
      .then(r => r.json()).then(data => renderIdolTable(data));
  }
  function renderIdolTable(data) {
    const tbody = document.getElementById('idol-tbody');
    tbody.innerHTML = '';
    data.forEach((row, idx) => {
      const idol      = row[0];
      const voteCount = row[1];
      const name      = row[2] ?? '이름없음';
      const rank      = idx + 1;
      const isCutline = currentSurvivorCount > 0 && rank === currentSurvivorCount + 1;
      const isElim    = idol.status === 'eliminated';
      if (isCutline) {
        const cutTr = document.createElement('tr');
        cutTr.innerHTML = `<td colspan="5" style="background:#ff6f00;color:white;font-size:11px;font-weight:700;padding:4px;text-align:center;">
          ▲ 생존 (${currentSurvivorCount}명) / 탈락 ▼</td>`;
        tbody.appendChild(cutTr);
      }
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>\${rank}</td><td>\${name}</td>
        <td>\${Number(voteCount).toLocaleString()}</td>
        <td class="\${isElim ? 'status-eliminated' : 'status-active'}">\${isElim ? '탈락' : '생존'}</td>
        <td>\${isElim
          ? `<button class="btn btn-success btn-sm" onclick="restoreIdol(\${idol.idolId})">복구</button>`
          : `<button class="btn btn-danger btn-sm" onclick="eliminateIdol(\${idol.idolId})">탈락</button>`
        }</td>`;
      tbody.appendChild(tr);
    });
  }
  function closeIdolSection() { document.getElementById('section-idols').style.display = 'none'; }
  function eliminateIdol(idolId) {
    if (!confirm('탈락 처리하시겠어요?')) return;
    fetch('/admin/idol/' + idolId + '/eliminate', { method: 'POST' })
      .then(r => r.text()).then(res => {
        if (res === 'success') { showMsg('탈락 처리됐어요.', 'success'); loadIdols(currentAuditionId, document.getElementById('idol-section-title').textContent, currentSurvivorCount); }
        else showMsg('처리 실패: ' + res, 'error');
      });
  }
  function restoreIdol(idolId) {
    if (!confirm('탈락을 취소하시겠어요?')) return;
    fetch('/admin/idol/' + idolId + '/restore', { method: 'POST' })
      .then(r => r.text()).then(res => {
        if (res === 'success') { showMsg('복구됐어요.', 'success'); loadIdols(currentAuditionId, document.getElementById('idol-section-title').textContent, currentSurvivorCount); }
        else showMsg('처리 실패: ' + res, 'error');
      });
  }
  function eliminateByRank() {
    if (currentSurvivorCount === 0) { showMsg('커트라인이 설정되지 않았어요.', 'error'); return; }
    if (!confirm('커트라인(' + currentSurvivorCount + '명) 기준으로 일괄 탈락 처리하시겠어요?')) return;
    fetch('/admin/audition/' + currentAuditionId + '/eliminateByRank', { method: 'POST' })
      .then(r => r.text()).then(res => {
        if (res === 'success') { showMsg('일괄 탈락 처리됐어요.', 'success'); loadIdols(currentAuditionId, document.getElementById('idol-section-title').textContent, currentSurvivorCount); }
        else showMsg('처리 실패: ' + res, 'error');
      });
  }
</script>

</html>
