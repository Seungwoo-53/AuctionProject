package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.model.MemberItemBackup;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemBackupRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @Autowired
    private MemberItemBackupRepository memberItemBackupRepository;
    @Autowired
    private HttpSession httpSession;


    public void deleteItem(Long id){
        itemRepository.deleteById(id);
    }
    private static final String LAST_VIEWED_TIME_MAP_KEY = "lastViewedTimeMap";

    @Transactional
    public void backupAndDeleteMemberItem(Long itemId) {
        List<MemberItem> memberItemsToDelete = memberItemRepository.findByItem_Id(itemId);

        for (MemberItem memberItem : memberItemsToDelete) {
            // 백업 테이블에 데이터 이동
            MemberItemBackup memberItemBackup = new MemberItemBackup();
            memberItemBackup.setMember(memberItem.getMember());
            memberItemBackup.setItem(memberItem.getItem());
            memberItemBackup.setPrice(memberItem.getPrice());
            memberItemBackupRepository.save(memberItemBackup);

            // 기존 테이블에서 삭제
            memberItemRepository.delete(memberItem);
        }
    }
    @Transactional
    public void updateItemPriceAndLinkToMember(Long memberId, Long itemId, int newPrice) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item not found"));
        MemberItem memberItem = new MemberItem();
        // Item의 가격 업데이트
        item.setPrice(newPrice);
        itemRepository.save(item);

        backupAndDeleteMemberItem(itemId);
        memberItemRepository.deleteByItem_Id(itemId);

        // Member_Item 테이블에 연결 추가
        memberItem.setMember(member);
        memberItem.setItem(item);
        memberItem.setPrice(newPrice);
        memberItemRepository.save(memberItem);



    }
    public Optional<Item> findById(Long itemId) {
        // 아이템을 아이디로 찾아옴
        return itemRepository.findById(itemId);
    }
    public void updateItem(Long id, Item item) {
        // 아이템 업데이트 로직 수행
       Optional<Item> Item = itemRepository.findById(id);
        if (item != null) {
            item.setTitle(item.getTitle());
            item.setInfo(item.getInfo());
            item.setPrice(item.getPrice());
            item.setUrl(item.getUrl());
            // 다른 필요한 필드도 업데이트

            itemRepository.save(item);
        }

    }


    // 경매 아이템 등록
    public Item save(Item item){
        return itemRepository.save(item);
    }
    //


    // 조회수 서비스
    public long incrementViewCount(long id) {
        Item item = itemRepository.findById(id).orElse(null);

        if (item != null && canIncrementViewCount(id)) {
            item.setView_count(item.getView_count() + 1);
            itemRepository.save(item);
            updateLastViewedTime(id);
            return item.getView_count();
        }

        return 0L; // or throw an exception, depending on your requirements
    }

    public long getViewCount(long id) {
        Item item = itemRepository.findById(id).orElse(null);

        return (item != null) ? item.getView_count() : 0L;
    }

    private boolean canIncrementViewCount(long id) {
        Map<Long, LocalDateTime> lastViewedTimeMap = getLastViewedTimeMap();

        LocalDateTime lastViewedTime = lastViewedTimeMap.get(id);

        //plusMinutes 분 마다 조회수 를 올릴 수 있음
        if (lastViewedTime == null || lastViewedTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            return true;
        }

        return false;
    }

    private void updateLastViewedTime(long id) {
        Map<Long, LocalDateTime> lastViewedTimeMap = getLastViewedTimeMap();
        lastViewedTimeMap.put(id, LocalDateTime.now());
        httpSession.setAttribute(LAST_VIEWED_TIME_MAP_KEY, lastViewedTimeMap);
    }

    private Map<Long, LocalDateTime> getLastViewedTimeMap() {
        Map<Long, LocalDateTime> lastViewedTimeMap = (Map<Long, LocalDateTime>) httpSession.getAttribute(LAST_VIEWED_TIME_MAP_KEY);

        if (lastViewedTimeMap == null) {
            lastViewedTimeMap = new HashMap<>();
        }

        return lastViewedTimeMap;
    }
    // 조회수 서비스 끝
}