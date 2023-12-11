package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

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

    public String handleItemDetails(Long itemId, Model model) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            model.addAttribute("item", item);
            handleAdditionalDetails(itemId, model);
            return "item/detail";
        } else {
            return "redirect:/";
        }
    }
    private void handleAdditionalDetails(Long itemId, Model model) {
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
    }
}
