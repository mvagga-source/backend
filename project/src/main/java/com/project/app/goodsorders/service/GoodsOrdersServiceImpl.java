package com.project.app.goodsorders.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.repository.GoodsOrdersRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsOrdersServiceImpl implements GoodsOrdersService {

    @Autowired GoodsOrdersRepository goodsOrdersRepository;
    
    @Autowired GoodsRepository goodsRepository; // 상품 재고 확인용

    @Override
    @Transactional
    public GoodsOrdersDto createOrder(GoodsOrdersDto dto) throws BaCdException {
        // 1. 최신 상품 정보 조회 및 재고 체크
        GoodsDto goods = goodsRepository.findById(dto.getGoods().getGno())
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

        if (goods.getStockCnt() < dto.getCnt()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "재고가 부족합니다.");
        }

        // 2. 총 가격 계산 (서버에서 다시 계산하여 위변조 방지)
        long totalPrice = goods.getPrice() * dto.getCnt();
        dto.setTotalPrice(totalPrice);
        
        // 3. 주문 저장
        return goodsOrdersRepository.save(dto);
    }

    @Override
    public Map<String, Object> findMyOrders(MemberDto member, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crdt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 구매자 ID와 삭제되지 않은 주문 조회
        Page<GoodsOrdersDto> pageList = goodsOrdersRepository.findByMemberIdAndDelYn(member.getId(), "n", pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("list", pageList.getContent());
        map.put("page", page);
        map.put("maxPage", pageList.getTotalPages());
        map.put("totalCount", pageList.getTotalElements());

        return map;
    }

    @Override
    public GoodsOrdersDto findByGono(GoodsOrdersDto dto) {
        return goodsOrdersRepository.findById(dto.getGono()).orElse(null);
    }

    @Override
    @Transactional
    public void updateStatus(String orderId, String tid, String status) {
        // 결제 완료 시 호출될 로직
        GoodsOrdersDto order = goodsOrdersRepository.findByOrderId(orderId).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
        
        order.setTid(tid);
        order.setStatus(status);
        
        // 결제 완료(PAID) 시 재고 차감
        if ("PAID".equals(status)) {
            GoodsDto goods = order.getGoods();
            goods.setStockCnt(goods.getStockCnt() - order.getCnt());
        }
    }

	@Override
	public GoodsOrdersDto findByGono(Long gono) throws BaCdException {
		return goodsOrdersRepository.findById(gono).orElse(null);
	}
}