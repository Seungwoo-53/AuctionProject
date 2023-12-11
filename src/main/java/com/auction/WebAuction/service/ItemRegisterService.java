package com.auction.WebAuction.service;

import com.auction.WebAuction.error.ValidationException;
import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ItemRegisterService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @Autowired
    private ItemService itemService;

    public void registerNewItem(Item item, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException("Validation failed for item.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);

        LocalDateTime registrationDate = LocalDateTime.now();
        LocalDateTime expirationDate = registrationDate.plus(24, ChronoUnit.HOURS);

        item.setDate(expirationDate);
        item.setEnabled(true);
        itemService.save(item);

        MemberItem memberItem = new MemberItem();
        memberItem.setItem(item);
        memberItem.setMember(member);
        memberItem.setPrice(item.getPrice());

        memberItemRepository.save(memberItem);
    }
}
