package com.auction.WebAuction.controller;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.ItemService;
import com.auction.WebAuction.service.TimeRemainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private TimeRemainingService timeRemainingService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @GetMapping("/")
    public String index(Model model, Authentication authentication) {

        List<Item> items = itemRepository.findAll();
        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        return "index";
    }
}
