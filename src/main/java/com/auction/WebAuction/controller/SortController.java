package com.auction.WebAuction.controller;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sort")
public class SortController {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/view")
    public String sortView(Model model, Authentication authentication) {

        List<Item> items = itemRepository.findAllByOrderByViewCountDesc();

        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        return "sort/view";
    }
    @GetMapping("/price")
    public String sortPrice(Model model, Authentication authentication) {

        List<Item> items = itemRepository.findAllByOrderByPriceDesc();

        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        return "sort/price";
    }
    @GetMapping("/time")
    public String sortTime(Model model, Authentication authentication) {

        List<Item> items = itemRepository.findAllByOrderByDateDesc();

        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        return "sort/time";
    }
}
