package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.repository.FinalItemRepository;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemDetailService {
    @Autowired
    private ItemService itemService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private FinalItemRepository finalItemRepository;

    public String handleItemDetails(Long itemId, Model model, Authentication authentication) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        List<Long> remainingMillisList = optionalItem.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("itemPrice",itemRepository.findById(itemId).get().getPrice());
        return optionalItem.map(item -> {
            model.addAttribute("item", item);

            if (authentication != null) {
                handleAuthenticationDetails(itemId, model, authentication);
            }

            return handleAdditionalDetails(itemId, model);
        }).orElse("redirect:/");
    }

    private void handleAuthenticationDetails(Long itemId, Model model, Authentication authentication) {
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);

        List<MemberItem> memberItems = memberItemRepository.findByMemberAndItemEnabledFalseAndItemId(member, itemId);
        boolean isItemInFinalTable = finalItemRepository.existsByItemId(itemId);

        model.addAttribute("showAuctionConfirmationButton", !(memberItems.isEmpty() || isItemInFinalTable));
    }

    private String handleAdditionalDetails(Long itemId, Model model) {
        Long views = itemService.incrementViewCount(itemId);
        model.addAttribute("views", views);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);

        int point = member.getPoint();
        model.addAttribute("member", member);
        model.addAttribute("point", point);

        String memberUsername = memberItemRepository.findMemberUsernameByItemId(itemId);
        model.addAttribute("memberUsername", memberUsername);

        return "item/detail";
    }
}
