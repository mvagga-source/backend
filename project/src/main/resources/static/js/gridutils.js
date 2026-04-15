function codeGet(codeGrpId){
	$.ajax({
        url: '/admin/code/ajaxCodeList.do',
        method: 'GET',
		data:{'codeGrpId':codeGrpId, 'active':'a001'},
		async: false,
        success: function(data) {
			if (data.success) {
				console.log(data.list);
				const list = data.list || [];
	            result = list.map(item => ({
	                text: item.codeNm,  // 사용자에게 보일 텍스트
	                value: item.codeId  // DB에 저장될 실제 값
	            }));
				return result;
            } else {
            	alert(data.message);
            }
			
        }
    });
	return result; 
}

/**
 * ajax로 selectbox 구성
 */
function ajaxApiSelBox(url, datas, textKey = 'codeNm', valueKey = 'codeId'){
	$.ajax({
        url: url,
        method: 'GET',
		data:datas,
		async: false,
        success: function(data) {
			if (data.success) {
				console.log(data.list);
				const list = data.list || [];
	            result = list.map(item => ({
	                text: item[textKey],  // 사용자에게 보일 텍스트
	                value: item[valueKey]  // DB에 저장될 실제 값
	            }));
				return result;
            } else {
            	alert(data.message);
            }
			
        }
    });
	return result; 
}

class GridManager {
	//자바스크립트는 클래스 잘 사용안함. 함수를 주로 사용
	
    constructor(containerId, options={}) {
        this.containerId = containerId;
        this.grid = new tui.Grid({
            el: document.getElementById(containerId),
            //data: data,
            //columns: columns,
            bodyHeight: 400,
            //rowHeaders: ['checkbox', 'rowNum'],
            rowHeaders: ['checkbox'],
            pageOptions: { perPage: 10 },
            columnOptions: { resizable: true },
			...options
            // 테마 등 공통 설정...(기본 default값으로 몇개 설정후 지우고 싶으면 객체생성쪽에서 option에 undefined를 넣거나 옵션 변경가능하도록->리액트 ...props와 비슷하게 유연할지는 테스트)
        });
		this.initGridEvents();
    }

    // 행 추가
    appendRow(defaultData) {
        this.grid.appendRow(defaultData);
    }
	
	// 선택된 행 삭제 (체크박스 사용 안함)
	/*rowDel() {
	    const focusedCell = this.grid.getFocusedCell();
	    const rowKey = focusedCell?.rowKey;
	    
	    if (rowKey == null) {
	        return;
	    }

	    // 1. 현재 포커스된 행의 데이터를 가져옴
	    const rowData = this.grid.getRow(rowKey);

	    // 2. origin이 '0'인 경우(새로 추가된 행 등)만 삭제 실행
	    if (rowData.origin === '0') {
	        this.grid.removeRow(rowKey);
	    } else {
	        alert('이미 저장된 원본 데이터는 삭제할 수 없습니다.');
	        // 또는 아무 반응 없게 하려면 alert 생략
	    }
	}*/
	
	// 체크된 행 삭제 (조건: origin === '0'인 데이터만)
	rowDel() {
	    const checkedRows = this.grid.getCheckedRows();

	    if (checkedRows.length === 0) {
	        alert('삭제할 행을 선택하세요.');
	        return;
	    }

	    // 1. origin이 '0'인 행들만 필터링
	    const targetRows = checkedRows.filter(row => row.origin === '0');

	    if (targetRows.length === 0) {
	        alert('추가된 행(저장 전)만 삭제 가능합니다.');
	        return;
	    }

	    // 2. 필터링된 행들만 삭제 실행
	    targetRows.forEach(row => {
	        this.grid.removeRow(row.rowKey);
	    });
	}

    // 체크된 데이터 서버 삭제 (Ajax)
    deleteCheckedRows(url, pkNames, finalParams) {
        const checkedRows = this.grid.getCheckedRows();
        if (checkedRows.length === 0) return alert('삭제할 행을 선택하세요.');

		// pkNames가 단일 문자열로 들어올 경우를 대비해 배열로 변환
		const pkList = Array.isArray(pkNames) ? pkNames : [pkNames];
		
		// Original이 true인 행만 삭제 대상
	    const deleteList = checkedRows
	        .filter(row => row.origin !== '0')  // 서버 삭제 가능 여부
			.map(row => {		//['codeGrpId','codeId']->복합키인 경우,	'codeId'->한개인 경우
	            // pkList에 정의된 모든 키를 row에서 추출하여 하나의 객체로 합침
	            return pkList.reduce((acc, pk) => {
	                acc[pk] = row[pk];
	                return acc;
	            }, {});
	        });

	    if (deleteList.length === 0) {
	        alert('서버에서 삭제 가능한 원본 데이터만 선택하세요.');
	        return;
	    }
        if (confirm(`${checkedRows.length}건을 삭제하시겠습니까?`)) {
            $.ajax({
                url: url,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ deletedRows: deleteList }),
                success: (data) => {
					if (data.success) {
	                    alert(data.resultCnt +'건의 데이터가 삭제되었습니다.');
	                    this.grid.readData(1, finalParams, false);
	                } else {
	                	alert(data.message);
	                }
                }
            });
        }
    }

    // 서버 저장 (modifyData)
    save() {
        this.grid.request('modifyData');
    }
	
	// 그리드 공통 이벤트 (중복 방지)
    initGridEvents() {
        /*this.grid.on('successResponse', (ev) => {
			if (ev.xhr.responseURL.indexOf('ajaxModify.do') !== -1 || ev.xhr.status === 200) {
	            // 조회가 아닌 경우에만 메시지 표시 (필요 시 URL 체크 등으로 정밀 제어)
	            // 보통 조회는 'readData'를 쓰므로, 요청 타입을 확인해야 합니다.
	            
	            // 더 정확한 방법: 단순히 alert만 띄우지 말고 데이터 유무나 요청 성격을 확인
	            const responseData = ev.xhr.responseJSON;
	            if (responseData && responseData.result === true) {
	                // 단순히 모든 성공에 alert을 띄우면 조회 때도 뜹니다.
	                // 서버에서 저장 성공 시에만 특정 플래그(예: isSaveSuccess)를 내려주면 좋습니다.
	                if(ev.requestType === 'modifyData') { // TUI Grid 제공 프로퍼티 확인
	                     alert('저장되었습니다.');
	                     this.grid.readData(1);
	                }
	            }
	        }
        });*/

        this.grid.on('failResponse', (ev) => {
            // 서버에서 에러 메시지를 보냈다면 해당 메시지 표시
			var error = JSON.parse(ev.xhr.responseText);
            const errorMsg = error?.message || '저장 중 오류가 발생했습니다.';
            alert(errorMsg);
			if(error.code==="401"){
				location.href=window.location.protocol+"//"+window.location.hostname+":3000/UserLogin";
			} else if(error.code==="127"){
				history.back();
			}
        });
    }

}