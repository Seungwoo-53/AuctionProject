package com.auction.WebAuction.service;


import com.auction.WebAuction.error.InsufficientPointsException;
import com.auction.WebAuction.model.*;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemBackupRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberItemBackupRepository memberItemBackupRepository;
    public Member save(Member member){
        String encodedPassword = passwordEncoder.encode(member.getUserpass());
        member.setUserpass(encodedPassword);
        member.setEnabled(true);
        Role role = new Role();
        role.setId(1L);
        member.getRoles().add(role);
        member.setPoint(10000);
        return memberRepository.save(member);
    }



    @Transactional
    public void deductPoints(Long memberId, int points) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found"));

        int currentPoints = member.getPoint();
        if (currentPoints >= points) {
            member.setPoint(currentPoints - points);
            memberRepository.save(member);
        } else {
            throw new InsufficientPointsException("Insufficient points to make the bid.");
        }
    }

    public List<Item> getMyItems() {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);

        List<Long> itemIds = memberItemBackupRepository.findDistinctItemIdsByMember_Id(member.getId());

        // itemIds로 아이템 데이터 조회
        return itemRepository.findAllById(itemIds);
    }

    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();
        for(FieldError error : errors.getFieldErrors()){
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
}
