<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>오디션 회차 관리 — ACTION 101</title>
  <link href="<c:url value='/css/audition/round.css'/>" rel="stylesheet">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
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
        <div style="float:right; display:flex; gap:8px;">
          <button class="btn btn-secondary" style="font-size:12px;"
        		  onclick="openRoundTemplateModal()">📋 Excel 양식 안내</button>
		  <input type="file" id="excel-round-input" accept=".xlsx,.xls"
		         style="display:none;" onchange="handleRoundExcelUpload()">
		  <button class="btn btn-secondary" style="font-size:12px;"
		          onclick="document.getElementById('excel-round-input').click()">회차 일괄 등록(Excel)</button>
		  <button class="btn btn-primary" style="font-size:12px;"
		          onclick="openCreateForm()">+ 회차 개별 등록</button>
		</div>
      </div>

  	  <!-- Excel 미리보기 모달 -->
	  <div id="excel-round-preview" style="display:none; background:#f8faff; border:1px solid #d0d9f0; border-radius:8px; padding:20px; margin-bottom:20px;">
	    <p style="font-size:13px; font-weight:700; color:#1a2c4e; margin-bottom:4px;">Excel 파싱 결과 확인</p>
	    <p style="font-size:11px; color:#888; margin-bottom:12px;">아래 내용으로 회차가 등록됩니다. 확인 후 "일괄 등록" 버튼을 눌러주세요.</p>
	    <div style="overflow-x:auto; margin-bottom:12px;">
	      <table class="at-table" id="excel-round-preview-table">
	        <thead>
	          <tr>
	            <th>회차</th><th>제목</th><th>시작일</th><th>마감일</th>
	            <th>최대투표</th><th>커트라인</th><th>팀경연</th><th>가산점</th><th>검증</th>
	          </tr>
	        </thead>
	        <tbody id="excel-round-preview-tbody"></tbody>
	      </table>
	    </div>
	    <div style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:8px;">
	      <span id="excel-round-summary" style="font-size:12px; color:#555;"></span>
	      <div style="display:flex; gap:8px;">
	        <button class="btn btn-secondary btn-sm" onclick="closeRoundExcelPreview()">취소</button>
	        <button class="btn btn-success btn-sm" id="excel-round-confirm-btn" onclick="confirmRoundExcelUpload()">일괄 등록</button>
	      </div>
	    </div>
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
		      <th>차수관리</th>
		      <th>참가자</th>
		      <th>생존자 이관</th>
		      <th>수정 / 삭제</th>
		    </tr>
		  </thead>
		  <tbody>
		    <c:forEach var="a" items="${auditionList}">
		      <c:set var="sc" value="0"/>
		      <c:if test="${a['survivorCount'] != null}">
		        <c:set var="sc" value="${a['survivorCount']}"/>
		      </c:if>
		      
		      <%--
			  lockFlag: 현재 차수보다 round 번호가 큰 차수 중
			  ongoing 또는 ended 가 하나라도 있으면 true.
			  --%>
			  <c:set var="lockFlag" value="false"/>
		  	    <c:forEach var="other" items="${auditionList}">
			      <c:if test="${other['round'] > a['round'] and (other['status'] == 'ongoing' or other['status'] == 'ended')}">
				    <c:set var="lockFlag" value="true"/>
				  </c:if>
			    </c:forEach>
		      <tr>
		        <td>${a['round']}차</td>
				<td>
				  ${a['title']}
				  <span class="badge badge-${a['status']}" style="margin-left:6px;">
				    <c:choose>
				      <c:when test="${a['status'] == 'ongoing'}">진행중</c:when>
				      <c:when test="${a['status'] == 'ended'}">종료</c:when>
				      <c:otherwise>예정</c:otherwise>
				    </c:choose>
				  </span>
				</td>
		        <td>${a['startDate']} ~ ${a['endDate']}</td>
		        <td>${a['maxVoteCount']}명</td>
		        <td>
		          <c:choose>
		            <c:when test="${a['survivorCount'] != null}">${a['survivorCount']}명</c:when>
		            <c:otherwise><span style="color:#bbb">미설정</span></c:otherwise>
		          </c:choose>
		        </td>
				<%-- 팀경연 여부 + 관리 버튼 통합 --%>
				<td>
				  <c:choose>
				    <c:when test="${a['hasTeamMatch'] == 'true'}">
				      <c:choose>
				        <c:when test="${lockFlag == 'true'}">
				          <button class="btn btn-purple btn-sm" disabled
				                  title="이후 차수가 진행 중이거나 종료돼 변경할 수 없어요.">팀경연</button>
				        </c:when>
				        <c:otherwise>
				          <button class="btn btn-purple btn-sm"
                  				  onclick="location.href='/admin/audition/team?auditionId=${a['auditionId']}'">
                  				  팀경연
                  		  </button>
				        </c:otherwise>
				      </c:choose>
				    </c:when>
				    <c:otherwise>
				      <span style="color:#bbb; font-size:11px">없음</span>
				    </c:otherwise>
				  </c:choose>
				</td>
		        <td>${a['bonusVotes']}표</td>
				<%-- 상태변경 --%>
				<td>
				  <c:choose>
				    <c:when test="${a['status'] == 'upcoming'}">
				      <c:choose>
				        <c:when test="${lockFlag == 'true'}">
				          <button class="btn btn-success btn-sm" disabled
				                  title="이후 차수가 진행 중이거나 종료돼 변경할 수 없어요.">시작</button>
				        </c:when>
				        <c:otherwise>
				          <button class="btn btn-success btn-sm"
				                  onclick="updateStatus(${a['auditionId']}, 'ongoing')">시작</button>
				        </c:otherwise>
				      </c:choose>
				    </c:when>
				    <c:when test="${a['status'] == 'ongoing'}">
				      <button class="btn btn-secondary btn-sm"
				              onclick="updateStatus(${a['auditionId']}, 'ended')">종료</button>
				    </c:when>
				    <c:otherwise>
				      <span style="color:#bbb; font-size:11px">완료</span>
				    </c:otherwise>
				  </c:choose>
				</td>
				<%-- 참가자 생존·탈락 관리 --%>
				<td>
				  <c:choose>
				    <c:when test="${lockFlag == 'true'}">
				      <button class="btn btn-primary btn-sm" disabled
				              title="이후 차수가 진행 중이거나 종료돼 변경할 수 없어요.">
				        생존·탈락
				      </button>
				    </c:when>
				    <c:otherwise>
				      <button class="btn btn-primary btn-sm"
				              onclick="loadIdols(${a['auditionId']}, '${a['title']}', ${sc})">
				        생존·탈락
				      </button>
				    </c:otherwise>
				  </c:choose>
				</td>
				<%-- 다음 차수로 생존자 이관 --%>
				<td>
				  <c:choose>
				    <c:when test="${a['status'] == 'ended' and lockFlag == 'false'}">
				      <button class="btn btn-primary btn-sm"
				              onclick="openNextRoundModal(${a['auditionId']}, '${a['title']}')">
				        이관
				      </button>
				    </c:when>
				    <c:when test="${a['status'] == 'ended' and lockFlag == 'true'}">
				      <button class="btn btn-primary btn-sm" disabled
				              title="이후 차수가 진행 중이거나 종료돼 변경할 수 없어요.">
				        이관
				      </button>
				    </c:when>
				    <c:otherwise>
				      <span style="color:#bbb; font-size:11px">–</span>
				    </c:otherwise>
				  </c:choose>
				</td>
		        <%-- 수정 / 삭제 --%>
		        <td>
		          <c:choose>
		            <c:when test="${lockFlag == 'false'}">
			          <div style="display:flex; gap:4px; justify-content:center;">
			            <button class="btn btn-warning btn-sm"
			                    onclick="openUpdateForm(${a['auditionId']}, '${a['round']}', '${a['title']}',
			                             '${a['startDate']}', '${a['endDate']}', ${a['maxVoteCount']},
			                             '${sc}', '${a['hasTeamMatch']}', '${a['bonusVotes']}')">
			              수정
			            </button>
			            <button class="btn btn-danger btn-sm"
			                    onclick="deleteAudition(${a['auditionId']}, '${a['title']}')">
			              삭제
			            </button>
			          </div>
			        </c:when>
			        <c:otherwise>
			          <div style="display:flex; gap:4px; justify-content:center;">
			            <button class="btn btn-warning btn-sm" disabled>수정</button>
			            <button class="btn btn-danger btn-sm" disabled>삭제</button>
			          </div>
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
	<!-- ══ Excel 양식 안내 모달 (회차) ══ -->
	<div id="modal-round-template" style="
	  display:none; position:fixed; inset:0;
	  background:rgba(0,0,0,0.45);
	  z-index:9999;
	  align-items:center; justify-content:center;">
	  <div style="background:white; border-radius:12px; padding:28px 32px; width:780px; max-width:95vw;
	              max-height:90vh; overflow-y:auto; box-shadow:0 8px 32px rgba(0,0,0,0.18);">
	
	    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
	      <p style="font-size:15px; font-weight:700; color:#1a2c4e; margin:0;">📋 오디션 회차 일괄 등록 — Excel 양식 안내</p>
	      <button onclick="closeRoundTemplateModal()" style="background:none;border:none;font-size:18px;cursor:pointer;color:#888;">✕</button>
	    </div>
	
	    <p style="font-size:12px; color:#666; margin-bottom:16px; line-height:1.7;">
	      아래 양식을 참고해 Excel 파일을 작성한 뒤 <strong>「회차 일괄 등록(Excel)」</strong> 버튼으로 업로드하세요.<br>
	      날짜는 <strong>YYYYMMDD</strong> 형식, 팀경연 여부는 <strong>있음 / 없음</strong>으로 입력합니다.
	    </p>
	
	    <!-- 샘플 테이블 -->
	    <div style="overflow-x:auto; margin-bottom:16px;">
	      <table class="at-table" style="font-size:12px; min-width:640px;">
	        <thead>
	          <tr>
	            <th>회차 *</th>
	            <th>제목 *</th>
	            <th>시작일 *</th>
	            <th>마감일 *</th>
	            <th>최대투표 수</th>
	            <th>커트라인 (명)</th>
	            <th>팀경연 여부</th>
	            <th>가산점 (표)</th>
	          </tr>
	          <tr style="background:#f0f4ff; font-size:11px; color:#7c8ba0;">
	            <td>숫자 (예: 2)</td>
	            <td>예) 2차 오디션</td>
	            <td>YYYYMMDD (예: 20260501)</td>
	            <td>YYYYMMDD (예: 20260531)</td>
	            <td>기본값: 7</td>
	            <td>예) 32 (미설정시 0)</td>
	            <td>있음 / 없음</td>
	            <td>기본값: 500</td>
	          </tr>
	        </thead>
	        <tbody>
	          <tr>
	            <td>2</td><td>2차 오디션</td><td>20260501</td><td>20260507</td><td>7</td><td>32</td><td>있음</td><td>500</td>
	          </tr>
	          <tr>
	            <td>3</td><td>3차 오디션</td><td>20260508</td><td>20260514</td><td>7</td><td>20</td><td>있음</td><td>500</td>
	          </tr>
	          <tr style="color:#aaa; font-size:11px; font-style:italic; background:#fafafa;">
	            <td colspan="8">↑ 예시 데이터입니다. 실제 입력 시 삭제하고 사용하세요.</td>
	          </tr>
	        </tbody>
	      </table>
	    </div>
	
	    <!-- 작성 규칙 -->
	    <div style="background:#f8faff; border-left:3px solid #7c9fd4; border-radius:0 6px 6px 0;
	                padding:10px 14px; font-size:12px; color:#444; line-height:1.9; margin-bottom:20px;">
	      <strong style="color:#1a2c4e;">작성 규칙</strong><br>
	      · 날짜는 반드시 <strong>YYYYMMDD</strong> 8자리 숫자로 입력 (예: 20260501)<br>
	      · 팀경연 여부: <strong>있음</strong> 또는 <strong>없음</strong> 으로 입력<br>
	      · 커트라인 미설정 시 <strong>0</strong> 입력<br>
	      · 회차·제목·시작일·마감일은 필수, 나머지는 미입력 시 기본값 적용<br>
	      · 1~4행(제목·안내·헤더·설명)은 수정하지 마세요
	    </div>
	
	    <div style="display:flex; justify-content:flex-end; gap:8px;">
	      <button class="btn btn-secondary" onclick="closeRoundTemplateModal()">닫기</button>
	      <button class="btn btn-success" onclick="downloadRoundTemplate()">⬇ 템플릿 다운로드</button>
	    </div>
	  </div>
	</div>
	
  </div><!-- /page-body -->
</body>

<script>
/* ════════ Excel 회차 일괄 등록 ════════ */
var excelRoundRows = [];

function handleRoundExcelUpload() {
  var fileInput = document.getElementById('excel-round-input');
  var file = fileInput.files[0];
  if (!file) return;

  var reader = new FileReader();
  reader.onload = function(e) {
    try {
      var wb   = XLSX.read(e.target.result, { type: 'binary' });
      var ws   = wb.Sheets[wb.SheetNames[0]];
      var rows = XLSX.utils.sheet_to_json(ws, { header: 1 });

      excelRoundRows = [];
      var validRows  = [];
      var errorRows  = [];

      // "YYYYMMDD" → "YYYY-MM-DD"
      function toDateStr(val) {
        var s = String(val).replace(/-/g, '').trim();
        if (s.length === 8) return s.slice(0,4) + '-' + s.slice(4,6) + '-' + s.slice(6,8);
        return s;
      }
   
      rows.forEach(function(row, i) {
    	if (i < 4) return;  // 1행 헤더 건너뜀
        if (!row || row.length === 0 || row[0] === undefined || row[0] === '') return;

        var round         = row[0];
        var title         = row[1] ? String(row[1]).trim() : '';
        var startDate     = row[2] !== undefined ? toDateStr(row[2]) : '';
        var endDate       = row[3] !== undefined ? toDateStr(row[3]) : '';
        var maxVoteCount  = row[4] !== undefined ? parseInt(row[4], 10) : 7;
        var survivorCount = row[5] !== undefined ? parseInt(row[5], 10) : 0;
        var hasTeamMatch  = row[6] !== undefined ? String(row[6]).trim() : 'false';
        var bonusVotes    = row[7] !== undefined ? parseInt(row[7], 10) : 500;

        if (hasTeamMatch === '있음' || hasTeamMatch === '1' || hasTeamMatch === 'true') {
          hasTeamMatch = 'true';
        } else {
          hasTeamMatch = 'false';
        }

        var errors = [];
        if (!round || isNaN(parseInt(round, 10))) errors.push('회차 오류');
        if (!title) errors.push('제목 없음');
        if (!startDate || !startDate.match(/^\d{4}-\d{2}-\d{2}$/)) errors.push('시작일 형식 오류');
        if (!endDate   || !endDate.match(/^\d{4}-\d{2}-\d{2}$/))   errors.push('마감일 형식 오류');

        var rowData = {
          round:         parseInt(round, 10),
          title:         title,
          startDate:     startDate,
          endDate:       endDate,
          maxVoteCount:  isNaN(maxVoteCount)  ? 7   : maxVoteCount,
          survivorCount: isNaN(survivorCount) ? 0   : survivorCount,
          hasTeamMatch:  hasTeamMatch,
          bonusVotes:    isNaN(bonusVotes)    ? 500 : bonusVotes,
          errors:        errors
        };

        if (errors.length === 0) {
          excelRoundRows.push(rowData);
          validRows.push(rowData);
        } else {
          errorRows.push(rowData);
        }
      });

      if (validRows.length === 0 && errorRows.length === 0) {
        showMsg('Excel에 데이터가 없어요.', 'error');
        fileInput.value = '';
        return;
      }

      renderRoundExcelPreview(validRows, errorRows);
      fileInput.value = '';

    } catch (err) {
      showMsg('Excel 파일 읽기 실패', 'error');
      fileInput.value = '';
    }
  };
  reader.readAsBinaryString(file);
}

function renderRoundExcelPreview(validRows, errorRows) {
  var tbody      = document.getElementById('excel-round-preview-tbody');
  var summaryEl  = document.getElementById('excel-round-summary');
  var confirmBtn = document.getElementById('excel-round-confirm-btn');
  var html = '';

  validRows.forEach(function(r) {
    html += '<tr>'
          + '<td>' + r.round + '차</td>'
          + '<td>' + r.title + '</td>'
          + '<td>' + r.startDate + '</td>'
          + '<td>' + r.endDate + '</td>'
          + '<td>' + r.maxVoteCount + '명</td>'
          + '<td>' + (r.survivorCount > 0 ? r.survivorCount + '명' : '미설정') + '</td>'
          + '<td>' + (r.hasTeamMatch === 'true' ? '있음' : '없음') + '</td>'
          + '<td>' + r.bonusVotes + '표</td>'
          + '<td style="color:#2e7d32; font-weight:700;">&#10003;</td>'
          + '</tr>';
  });

  errorRows.forEach(function(r) {
    html += '<tr style="background:#ffebee;">'
          + '<td>' + (r.round || '?') + '</td>'
          + '<td>' + (r.title || '?') + '</td>'
          + '<td>' + (r.startDate || '?') + '</td>'
          + '<td>' + (r.endDate || '?') + '</td>'
          + '<td>' + r.maxVoteCount + '</td>'
          + '<td>' + r.survivorCount + '</td>'
          + '<td>' + r.hasTeamMatch + '</td>'
          + '<td>' + r.bonusVotes + '</td>'
          + '<td style="color:#c62828; font-size:11px;">' + r.errors.join(', ') + '</td>'
          + '</tr>';
  });

  tbody.innerHTML = html;
  summaryEl.textContent = '등록 가능 ' + validRows.length + '건  |  오류 ' + errorRows.length + '건 (오류 행은 건너뜀)';
  confirmBtn.disabled = validRows.length === 0;
  document.getElementById('excel-round-preview').style.display = 'block';
  document.getElementById('excel-round-preview').scrollIntoView({ behavior: 'smooth' });
}

function closeRoundExcelPreview() {
  document.getElementById('excel-round-preview').style.display = 'none';
  excelRoundRows = [];
}

function confirmRoundExcelUpload() {
  if (excelRoundRows.length === 0) { showMsg('등록할 데이터가 없어요.', 'error'); return; }
  if (!confirm(excelRoundRows.length + '개 회차를 일괄 등록할까요?')) return;

  var btn = document.getElementById('excel-round-confirm-btn');
  btn.disabled = true;
  btn.textContent = '등록 중...';

  var success = 0;
  var failed  = 0;

  var chain = Promise.resolve();
  excelRoundRows.forEach(function(r) {
    chain = chain.then(function() {
      var params = new URLSearchParams({
        round:         r.round,
        title:         r.title,
        startDate:     r.startDate,
        endDate:       r.endDate,
        maxVoteCount:  r.maxVoteCount,
        survivorCount: r.survivorCount,
        hasTeamMatch:  r.hasTeamMatch,
        bonusVotes:    r.bonusVotes,
        status:        'upcoming'
      });
      return fetch('/admin/audition/create', { method: 'POST', body: params })
        .then(function(res) { return res.text(); })
        .then(function(res) {
          if (res === 'success') { success++; } else { failed++; }
        })
        .catch(function() { failed++; });
    });
  });

  chain.then(function() {
    closeRoundExcelPreview();
    if (failed === 0) {
      showMsg(success + '개 회차가 등록됐어요.', 'success');
    } else {
      showMsg(success + '개 성공, ' + failed + '개 실패.', 'error');
    }
    setTimeout(reload, 1200);
  });
}
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
  
  /* ════════ 회차 삭제 ════════ */
  function deleteAudition(auditionId, title) {
    if (!confirm('"' + title + '" 회차를 삭제하시겠어요?\n참가자·투표·팀경연 데이터도 함께 삭제될 수 있어요.')) return;
    fetch('/admin/audition/' + auditionId + '/delete', { method: 'POST' })
      .then(r => r.text())
      .then(res => {
        if (res === 'success') { showMsg('삭제됐어요.', 'success'); setTimeout(reload, 1000); }
        else showMsg('삭제 실패: ' + res, 'error');
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
          ▲ 생존 (\${currentSurvivorCount}명) / 탈락 ▼</td>`;
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
 
  /* ════ Excel 양식 안내 모달 (회차) ════ */
  function openRoundTemplateModal() {
    document.getElementById('modal-round-template').style.display = 'flex';
  }
  function closeRoundTemplateModal() {
    document.getElementById('modal-round-template').style.display = 'none';
  }
  function downloadRoundTemplate() {
    var wb = XLSX.utils.book_new();
    var data = [
      ['ACTION 101 — 오디션 회차 등록 양식'],
      ['※ 5행부터 데이터를 입력하세요.  필수항목(*)은 반드시 입력해야 합니다.  날짜는 YYYYMMDD 형식으로 입력하세요. (예: 20260501)'],
      ['회차 *','제목 *','시작일 *','마감일 *','최대투표 수','커트라인 (명)','팀경연 여부','가산점 (표)'],
      ['숫자 (예: 2)','예) 2차 오디션','YYYYMMDD (예: 20260501)','YYYYMMDD (예: 20260531)','기본값: 7','예) 32  (미설정시 0)','있음 / 없음','기본값: 500'],
      [2,'2차 오디션',20260501,20260507,7,32,'있음',500],
      [3,'3차 오디션',20260508,20260514,7,20,'있음',500],
      ['← 이 행은 예시입니다. 실제 입력 시 삭제하고 사용하세요.']
    ];
    var ws = XLSX.utils.aoa_to_sheet(data);
    ws['!cols'] = [8,22,20,20,12,14,12,12].map(function(w){return{wch:w};});
    ws['!merges'] = [
      {s:{r:0,c:0},e:{r:0,c:7}},
      {s:{r:1,c:0},e:{r:1,c:7}},
      {s:{r:6,c:0},e:{r:6,c:7}}
    ];
    XLSX.utils.book_append_sheet(wb, ws, '오디션 회차 등록');
    XLSX.writeFile(wb, '오디션회차_등록폼.xlsx');
  }
</script>

</html>
