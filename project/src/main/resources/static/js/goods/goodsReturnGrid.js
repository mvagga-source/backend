function initGoodsReturnGrid() {
	if (returnInit) return;
		returnInit = true;
	//--------정산
	/*function orderStatusFormatter({value}) {
	    const map = {
	        PAID: '결제완료',
	        READY: '결제대기',
	        CANCEL: '결제취소',
	        FAILED: '결제실패'
	    };
	    return map[value] || value;
	}*/
	
	function returnStatusFormatter({value}) {
	    const map = {
	        접수: '접수',
	        회수중: '회수중',
	        검수중: '검수중',
	        완료: '완료',
	        거부: '거부',
	        취소: '취소',
	    };
	    return map[value] || value;
	}
	
	// 정산여부 매핑
	/*function settleYnFormatter({value}) {
	    const map = {
	        y: '정산완료',
	        n: '정산대기'
	    };
	    return map[value] || value;
	}*/
	
    // 그리드 생성
    var dataReturn = {
    	api: {
	    	readData: { url: '/admin/return/ajaxList', method: 'GET' },
	    	modifyData: { url: '/admin/return/ajaxModify', method: 'POST', contentType: 'application/json' }
	    }
    	,initialRequest: false
    };
    const columnsReturn = [
        { header: '번호', name: 'gono', align: 'center', hidden:true, align: 'center' },
        { header: '반품번호', name: 'rno', align: 'center', hidden:true, align: 'center' },
        { header: '주문번호', name: 'orderId', align: 'center', align: 'center', sortable: true },
        { header: '상품명', name: 'gname' },
        { header: '판매자', name: 'sellerName', align: 'center' },
        { header: '구매자', name: 'buyerName', align: 'center' },
		{ header: '구분', name: 'returnType', align: 'center', width: 100 },
		{ header: '사유', name: 'returnReason', align: 'center', width: 100 },
		{ 
		    header: '사유 상세보기', 
		    name: 'reasonBtn', 
		    align: 'center', 
		    width: 120,
		    formatter: () => '<button type="button" class="btn-grid-detail">상세보기</button>' 
		},
		{ header: '구매자상세사유', name: 'returnReasonDetail', hidden: true },
		{ header: '판매자거부사유', name: 'returnSaleReasonDetail', hidden: true },
        { header: '주문수량', name: 'orderCnt', align: 'center' },
        { header: '반품/교환 수량', name: 'returnCnt', align: 'center', className: 'txt-red', editor: 'text' }, // 반품은 강조
        { header: '반품/교환 가능수량', name: 'realCnt', align: 'center' },
        { header: '결제금액', name: 'totalPrice', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '반품상태', name: 'returnStatus', align: 'center', formatter: returnStatusFormatter, 
		editor: { 
            type: 'select', 
            options: { 
                listItems: [
                    { text: '접수', value: '접수' },
                    { text: '회수중', value: '회수중' },
                    { text: '검수중', value: '검수중' },
                    { text: '완료', value: '완료' },
                    { text: '거부', value: '거부' },
                    { text: '취소', value: '취소' },
                ] 
            } 
        }, },
        /*{ header: '최종결제금액', name: 'finalTotalPrice', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '수수료', name: 'fee', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '배송비 페널티', name: 'penaltyFee', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '정산금액', name: 'settleAmount', formatter: priceFormatter, align: 'right', sortable: true },*/
        //{ header: '결제상태', name: 'orderStatus', formatter: orderStatusFormatter, align: 'center' },
        { header: '반품/교환 배송상태', name: 'delivStatus', align: 'center', editor: { 
            type: 'select', 
            options: {
                listItems: [
                    { text: '배송대기', value: '배송대기' },
                    { text: '배송준비중', value: '배송준비중' },
                    { text: '배송중', value: '배송중' },
                    { text: '배송완료', value: '배송완료' },
                ]
            } 
        }, },
        //{ header: '정산여부', name: 'settleYn', formatter: settleYnFormatter, align: 'center' },
        { header: '반품일시', name: 'returnDate', align: 'center', sortable: true },
        { header: '최종변경일시', name: 'returnUpDate', align: 'center', sortable: true },
        { header: '반품완료일시', name: 'completeDate', align: 'center', sortable: true },
    ];
    optionsReturn={ data: dataReturn, columns: columnsReturn };
    returnGridManager = new GridManager('grid-container-return', optionsReturn);

	// 검색 실행 함수
    function executeSearchReturn() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchFormReturn').serialize()));
        const extraParams = {
        	sortDir: $('#sortDirReturn').val(),
            perPage: parseInt($('#perPageReturn').val())
        };
        returnFinalParams = { ...formData, ...extraParams };
        
        returnGridManager.grid.readData(1, returnFinalParams, false);
    }

    // 이벤트 바인딩
    $('#btnSearchReturn').on('click', executeSearchReturn);

    // 정렬/개수 변경 시 자동 재조회
    $('#sortDirReturn, #perPageReturn').on('change', function() {
        if(this.id === 'perPageReturn') {
            returnGridManager.grid.setPerPage(parseInt(this.value));
        }else{
        	executeSearchReturn();
        }
    });
    
	// 저장 버튼 이벤트 연결
    $('#btnSaveReturn').on('click', function() {
        //if(confirm('변경사항을 저장하시겠습니까?')) {
            returnGridManager.save();
        //}
    });

    //그리드 성공후 처리
    returnGridManager.grid.on('successResponse', (ev) => {
		// 응답 URL에 ajaxModify가 포함되어 있는지 확인
        if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1) {
            const res = JSON.parse(ev.xhr.responseText);
            if (res.result || res.success) { // 서버 응답 구조에 맞게 체크
                alert('저장되었습니다.');
                returnGridManager.grid.readData(1, returnFinalParams, false); // 재조회
            } else {
                alert(res.message || '저장 중 오류가 발생했습니다.');
            }
        } else {
            // 기존 조회 성공 처리
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCntReturn').text(total);
        }
    });
	
	// 1. 그리드 클릭 이벤트
	returnGridManager.grid.on('click', (ev) => {
	    const { targetType, rowKey } = ev;
	    
	    // 버튼 클릭 시에만 작동
	    if (targetType === 'cell' && $(ev.nativeEvent.target).hasClass('btn-grid-detail')) {
	        const rowData = returnGridManager.grid.getRow(rowKey);
	        targetRowKey = rowKey;

	        // 모달에 데이터 세팅
	        $('#modalReturnReasonDetail').val(rowData.returnReasonDetail || '사유 없음');
	        $('#modalReturnSaleReasonDetail').val(rowData.returnSaleReasonDetail || '');
	        
	        $('#reasonModal').show();
	    }
	});

	// 2. 모달 내 [임시저장] 버튼 클릭 시
	$('#btnModalSave').on('click', function() {
	    const saleReason = $('#modalReturnSaleReasonDetail').val();
	    
	    // 그리드 데이터 업데이트 (그리드의 '저장' 버튼을 눌러야 최종 서버 전송)
	    returnGridManager.grid.setValue(targetRowKey, 'returnSaleReasonDetail', saleReason);
	    
	    alert('상세 사유가 그리드에 반영되었습니다. 최종 저장을 위해 목록의 [저장] 버튼을 눌러주세요.');
	    $('#reasonModal').hide();
	});
    
    executeSearchReturn();
}