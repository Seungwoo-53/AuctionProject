package com.auction.WebAuction.service;

import com.auction.WebAuction.model.*;
import com.auction.WebAuction.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Duration;
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
    private FinalItemRepository finalItemRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private TimeRemainingService timeRemainingService;
    @Autowired
    private MemberService memberService;

    @Transactional
    public void deleteItem(Long id){
        memberItemBackupRepository.deleteByItemId(id);
        memberItemRepository.deleteByItemId(id);
        itemRepository.deleteById(id);
    }
    private static final String LAST_VIEWED_TIME_MAP_KEY = "lastViewedTimeMap";
    // 타임 스탬프 찍기
    public void updateItemValidity() {
        List<Item> items = itemRepository.findAll();
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Item item : items) {
            if (item.getDate().isBefore(currentDateTime)) {
                item.setEnabled(false);
            }
        }
        itemRepository.saveAll(items);
    }
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
    public void backupAndNonDeleteMemberItem(Long itemId) {
        List<MemberItem> memberItemsToDelete = memberItemRepository.findByItem_Id(itemId);

        for (MemberItem memberItem : memberItemsToDelete) {
            // 백업 테이블에 데이터 이동
            MemberItemBackup memberItemBackup = new MemberItemBackup();
            memberItemBackup.setMember(memberItem.getMember());
            memberItemBackup.setItem(memberItem.getItem());
            memberItemBackup.setPrice(memberItem.getPrice());
            memberItemBackupRepository.save(memberItemBackup);
        }
    }
    @Transactional
    public void addPoints(Long itemId) {
        int price = memberItemRepository.findPriceByItemId(itemId);
                MemberItem memberItem = memberItemRepository.findByItemId(itemId);
                if (memberItem != null) {
                    Member member = memberItem.getMember();
                    if (member != null) {
                        member.setPoint(member.getPoint() + price);
                        memberRepository.save(member);
                        System.out.println("Member ID: " + member.getId());
                        System.out.println("Member Username: " + member.getUsername());
                        System.out.println("Member Point: " + member.getPoint());
                    }
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

        // Member_Item 테이블에 연결 추가
        memberItem.setMember(member);
        memberItem.setItem(item);
        memberItem.setPrice(newPrice);
        memberItemRepository.save(memberItem);

        // 2024-02-11 사용하면 memberitembackup에 두개가 생겨서 주석 처리하였음
        // backupAndNonDeleteMemberItem(itemId);

    }
    public Optional<Item> findById(Long itemId) {
        // 아이템을 아이디로 찾아옴
        return itemRepository.findById(itemId);
    }
    public void updateItem(Long id, Item newItem) {
        // 아이템 업데이트 로직 수행
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item  = itemOptional.get();
            item.setTitle(newItem.getTitle());
            item.setInfo(newItem.getInfo());
            item.setPrice(newItem.getPrice());
            item.setUrl(newItem.getUrl());
            item.setDate(LocalDateTime.now());
            item.setEnabled(true);
            // 다른 필요한 필드도 업데이트

            itemRepository.save(item);
        }

    }
    // 최종입찰자 정보 전달
    public void finalItemSave(long itemId, FinalItem finalItem){
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item not found"));
        finalItem.setItem(item);
        finalItemRepository.save(finalItem);
    }
    // 최종입찰자 정보 전달 끝
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