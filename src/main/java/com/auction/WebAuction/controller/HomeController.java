package com.auction.WebAuction.controller;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ItemRepository itemRepository;
    @GetMapping("/")
    public String index(Model model){
        List<Item> item = itemRepository.findAll();
        model.addAttribute("item", item);
        return "index";
    }
}
