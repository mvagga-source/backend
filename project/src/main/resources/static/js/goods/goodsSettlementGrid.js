function initGoodsSettlementGrid() {
	if (settlementInit) return;
		settlementInit = true;
	function orderStatusFormatter({value}) {
	    const map = {
	        PAID: '결제완료',
	        READY: '결제대기',
	        CANCEL: '결제취소',
	        FAILED: '결제실패'
	    };
	    return map[value] || value;
	}
	// 정산여부 매핑
	function settleYnFormatter({value}) {
	    const map = {
	        y: '정산완료',
	        n: '정산대기'
	    };
	    return map[value] || value;
	}
	
    // 그리드 생성
    var data = {
    	api: {
	    	readData: { url: '/admin/settlement/ajaxList', method: 'GET' },
	    	modifyData: { url: '/admin/settlement/ajaxModify', method: 'POST', contentType: 'application/json' }
	    }
    	,initialRequest: false
    };
	const columns = [
		{ header: '번호', name: 'gono', align: 'center', hidden: true },
	    { header: '주문번호', name: 'orderId', align: 'center', width:180, sortable: true },
	    { header: '상품명', name: 'gname', width:180 },
	    { header: '판매자', name: 'sellerName', width:100, align: 'center' },
	    { header: '구매자', name: 'buyerName', width:100, align: 'center' },
	    { header: '결제금액', name: 'totalPrice', formatter: priceFormatter, width:180, align: 'right', sortable: true },
	    { header: '배송비 페널티', name: 'penaltyFee', formatter: priceFormatter, width:180, align: 'right', sortable: true },
		{ header: '최종결제금액', name: 'finalTotalPrice', formatter: priceFormatter, width:180, align: 'right', sortable: true },
        { header: '예상수수료', name: 'fee', formatter: priceFormatter, width:150, align: 'right', sortable: true },
		{ header: '결제상태', name: 'orderStatus', formatter: orderStatusFormatter, width:120, align: 'center' },
		{ header: '배송상태', name: 'delivStatus', align: 'center', width:120 },
	    { header: '정산금액', name: 'settleAmount', formatter: priceFormatter, width:150, align: 'right', sortable: true },
	    { 
	        header: '정산여부', 
	        name: 'settleYn', 
	        formatter: settleYnFormatter, 
	        align: 'center',
	        editor: { 
	            type: 'select', 
	            options: { 
	                listItems: [
	                    { text: '정산대기', value: 'n' },
	                    { text: '정산완료', value: 'y' }
	                ] 
	            } 
	        },
			width:120,
	    },
	    { header: '주문일시', name: 'orderDate', width:180, align: 'center', sortable: true },
	    { header: '정산일시', name: 'settleDate', width:180, align: 'center', sortable: true }
	];
    options={ data: data, columns: columns };
    settlementGrid  = new GridManager('settlement-grid-container', options);

	// 검색 실행 함수
	function executeSettlementSearch() {

	    const formData = Object.fromEntries(
	        new URLSearchParams($('#settlementSearchForm').serialize())
	    );

	    const extraParams = {
	        sortDir: $('#sortDirSettlement').val(),
	        perPage: parseInt($('#perPageSettlement').val())
	    };

	    settlementFinalParams = { ...formData, ...extraParams };

	    settlementGrid.grid.readData(1, settlementFinalParams, false);
	}

    // 이벤트 바인딩
    $('#btnSearchSettlement').on('click', executeSettlementSearch);

    // 정렬/개수 변경 시 자동 재조회
    $('#sortDirSettlement, #perPageSettlement').on('change', function() {
        if(this.id === 'perPageSettlement') {
            settlementGrid.grid.setPerPage(parseInt(this.value));
        }else{
        	executeSettlementSearch();
        }
    });
    
	// 저장 버튼 이벤트 연결
    $('#btnSaveSettlement').on('click', function() {
        //if(confirm('변경사항을 저장하시겠습니까?')) {
            settlementGrid.save();
        //}
    });

    //그리드 성공후 처리
    settlementGrid.grid.on('successResponse', (ev) => {
		// 응답 URL에 ajaxModify가 포함되어 있는지 확인
        if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1) {
            const res = JSON.parse(ev.xhr.responseText);
            if (res.result || res.success) { // 서버 응답 구조에 맞게 체크
                alert('저장되었습니다.');
                settlementGrid.grid.readData(1, settlementFinalParams, false); // 재조회
            } else {
                alert(res.message || '저장 중 오류가 발생했습니다.');
            }
        } else {
            // 기존 조회 성공 처리
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#settlementTotalCnt').text(total);
        }
    });
    
    executeSettlementSearch();
}