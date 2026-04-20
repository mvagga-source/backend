function initGoodsGrid() {
	
	if (goodsGridInitialized) return;
	goodsGridInitialized = true;

	const goodsStatusOptions = [
        { text: '판매중', value: '판매중' },
        { text: '품절', value: '품절' },
        { text: '판매중지', value: '판매중지' }
    ];
	
	// 상단 옵션 배열에 배너 옵션 추가
	const bannerOptions = [
	    { text: '노출', value: 'y' },
	    { text: '미노출', value: 'n' }
	];

    const goodsColumns = [
        { header: '상품번호', name: 'gno', align: 'center', width: 80, hidden: true },
        { header: '참가자명', name: 'idolName', width: 100 },
        { header: '상품명', name: 'gname', editor: 'text' },
        { header: '판매자', name: 'sellerName', width: 120 }, // sellerNickname -> sellerName 확인 필요
        { header: '판매가', name: 'price', align: 'right', formatter: priceFormatter, editor: 'text', sortable: true },
        { header: '재고', name: 'stockCnt', align: 'right', formatter: countFormatter, width: 70, editor: 'text', sortable: true },
        { 
            header: '판매상태', 
            name: 'status', 
            align: 'center',
            width: 100,
            formatter: 'listItemText',
            editor: {
                type: 'select',
                options: { listItems: goodsStatusOptions }
            }
        },
        { header: '평점', name: 'avgRating', align: 'center', width: 60, sortable: true }, // 추가
        { header: '도움돼요개수', name: 'helpfulCnt', align: 'right', width: 120, sortable: true }, // 추가
        { header: '총리뷰수', name: 'reviewCnt', align: 'right', width: 120, sortable: true }, // 추가
		{ 
	        header: '배너노출', 
	        name: 'isBanner', 
	        align: 'center', 
	        width: 90,
	        formatter: 'listItemText',
	        editor: {
	            type: 'select',
	            options: { listItems: bannerOptions }
	        }
	    },
	    { 
	        header: '배너순서', 
	        name: 'bannerSort', 
	        align: 'center', 
	        width: 80, 
	        editor: 'text', // 숫자 입력
	        sortable: true 
	    },
        { header: '등록일', name: 'orderDate', align: 'center', width: 120, sortable: true }
    ];

    // 그리드 매니저 인스턴스 (ID 확인: goods-grid-container)
    goodsGridManager = new GridManager('goods-grid-container', { 
        data: {
            api: {
                readData: { url: '/admin/goods/ajaxList', method: 'GET' },
                modifyData: { url: '/admin/goods/ajaxModify', method: 'POST', contentType: 'application/json' }
            },
            initialRequest: false
        }, 
        columns: goodsColumns,
    });

    // 검색 함수 수정 (Form 필드와 정확히 매칭)
    function executeGoodsSearch() {
        // serialize()를 사용하여 폼 내의 모든 필드(category, search, status, stockStatus 등)를 가져옴
        const formData = Object.fromEntries(new URLSearchParams($('#goodsSearchForm').serialize()));
        const extraParams = {
            sortDir: $('#sortDirGoods').val(),
            perPage: parseInt($('#perPageGoods').val())
        };
        goodsFinalParams = { ...formData, ...extraParams };
        
        goodsGridManager.grid.readData(1, goodsFinalParams, false);
    }

    // 이벤트 바인딩 (ID 확인)
    $('#btnGoodsSearch').on('click', executeGoodsSearch);
    
    // 정렬/개수 변경 시 자동 재조회 (굿즈 전용)
    $('#sortDirGoods, #perPageGoods').on('change', executeGoodsSearch);

    $('#btnGoodsSave').on('click', function() {
        //if(confirm('상품 정보를 수정하시겠습니까?')) {
            goodsGridManager.save();
        //}
    });

    // 전체 건수 업데이트 (ID: totalCntGoods)
    goodsGridManager.grid.on('successResponse', (ev) => {
    	// 응답 URL에 ajaxModify가 포함되어 있는지 확인
        if (ev.xhr.responseURL.indexOf('ajaxModify') !== -1) {
            const res = JSON.parse(ev.xhr.responseText);
            if (res.result || res.success) { // 서버 응답 구조에 맞게 체크
                alert('저장되었습니다.');
                grid.grid.readData(1, finalParams, true); // 재조회
            } else {
                alert(res.message || '저장 중 오류가 발생했습니다.');
            }
        } else {
            // 기존 조회 성공 처리
            const res = JSON.parse(ev.xhr.responseText);
            const total = res?.data?.pagination?.totalCount ?? 0;
            $('#totalCntGoods').text(total);
        }
    });

    // 초기 로딩
    executeGoodsSearch();
}