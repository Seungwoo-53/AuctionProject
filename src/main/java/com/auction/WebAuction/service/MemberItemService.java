package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberItemService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;

    public String finalItemPageGetUsername(Long memberItemId) {
        MemberItem memberItem = memberItemRepository.findByItemId(memberItemId);
        if (memberItem != null) {
            Long memberId = memberItem.getMember().getId(); // memberItem.getMemberId()로 수정
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member != null) {
                return member.getUsername();
            }
        }
        return null;
    }
}
