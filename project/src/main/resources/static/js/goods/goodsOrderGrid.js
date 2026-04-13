function initGoodsOrderGrid() {
	if (GridInitialized) return;
		GridInitialized = true;
	//--------정산
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
	    	readData: { url: '/admin/orders/ajaxList', method: 'GET' },
	    	modifyData: { url: '/admin/orders/ajaxModify', method: 'POST', contentType: 'application/json' }
	    }
    	,initialRequest: false
    };
    const columns = [
        { header: '번호', name: 'gono', align: 'center', hidden:true, align: 'center' },
        { header: '주문번호', name: 'orderId', align: 'center', align: 'center', sortable: true },
        { header: '상품명', name: 'gname' },
        { header: '판매자', name: 'sellerName', align: 'center' },
        { header: '구매자', name: 'buyerName', align: 'center' },
        { header: '주문수량', name: 'orderCnt', align: 'center' },
        { header: '반품수량', name: 'returnCnt', align: 'center', className: 'txt-red' }, // 반품은 강조
        { header: '실수량', name: 'realCnt', align: 'center' },
        { header: '결제금액', name: 'totalPrice', formatter: priceFormatter, align: 'right', sortable: true },
        /*{ header: '최종결제금액', name: 'finalTotalPrice', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '수수료', name: 'fee', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '배송비 페널티', name: 'penaltyFee', formatter: priceFormatter, align: 'right', sortable: true },
        { header: '정산금액', name: 'settleAmount', formatter: priceFormatter, align: 'right', sortable: true },*/
        { header: '결제상태', name: 'orderStatus', formatter: orderStatusFormatter, align: 'center' },
        { header: '배송상태', name: 'delivStatus', editor: { 
            type: 'select', 
            options: { 
                listItems: [
                    { text: '배송대기', value: '배송대기' },
                    { text: '배송준비중', value: '배송준비중' },
                    { text: '배송중', value: '배송중' },
                    { text: '배송완료', value: '배송완료' },
                    { text: '구매확정', value: '구매확정' },
                    { text: '반품/교환', value: '반품/교환' }
                ] 
            } 
        }, align: 'center' },
        { header: '정산여부', name: 'settleYn', formatter: settleYnFormatter, align: 'center' },
        { header: '별점', name: 'rating', align: 'center', sortable: true },
        //{ header: '리뷰수', name: 'reviewCnt', align: 'right' },
        { header: '주문일시', name: 'orderDate', align: 'center', sortable: true },
        { header: '정산일시', name: 'settleDate', align: 'center', sortable: true }
    ];
    options={ data: data, columns: columns };
    grid = new GridManager('grid-container', options);

	// 검색 실행 함수
    function executeSearch() {
        const formData = Object.fromEntries(new URLSearchParams($('#searchForm').serialize()));
        const extraParams = {
        	sortDir: $('#sortDir').val(),
            perPage: parseInt($('#perPage').val())
        };
        finalParams = { ...formData, ...extraParams };
        
        grid.grid.readData(1, finalParams, false);
    }

    // 이벤트 바인딩
    $('#btnSearch').on('click', executeSearch);

    // 정렬/개수 변경 시 자동 재조회
    $('#sortDir, #perPage').on('change', function() {
        if(this.id === 'perPage') {
            grid.grid.setPerPage(parseInt(this.value));
        }else{
        	executeSearch();
        }
    });
    
	// 저장 버튼 이벤트 연결
    $('#btnSave').on('click', function() {
        //if(confirm('변경사항을 저장하시겠습니까?')) {
            grid.save();
        //}
    });

    //그리드 성공후 처리
    grid.grid.on('successResponse', (ev) => {
		// 응답 URL에 ajaxModify가 포함되어 있는지 확인
        if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1) {
            const res = JSON.parse(ev.xhr.responseText);
            if (res.result || res.success) { // 서버 응답 구조에 맞게 체크
                alert('저장되었습니다.');
                grid.grid.readData(1, finalParams, false); // 재조회
            } else {
                alert(res.message || '저장 중 오류가 발생했습니다.');
            }
        } else {
            // 기존 조회 성공 처리
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCnt').text(total);
        }
    });
    
    executeSearch();
}