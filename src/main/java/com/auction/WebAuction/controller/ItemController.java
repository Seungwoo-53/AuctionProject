package com.auction.WebAuction.controller;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @GetMapping("/detail/{id}")
    public String ItemDetail(@PathVariable Long id, Model model) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        Long views = itemService.incrementViewCount(id);
        model.addAttribute(views);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            model.addAttribute("item", item);
            model.addAttribute("item", itemOptional.get());
            return "/item/detail";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/detail/{id}")
    @ResponseBody
    public String updatePrice(@PathVariable("id") long id, @RequestParam int price,Model model) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setPrice(price);
            itemRepository.save(item);
            model.addAttribute("price",item.getPrice());
            return "Price updated successfully!";
        } else {
            return "Item not found!";
        }
    }

    @GetMapping("register")
    public String register(Model model){
        List<Item> item = itemRepository.findAll();
        model.addAttribute("item", item);
        return "/item/register";
    }
    @GetMapping("newRegister")
    public String newRegister(Model model){
        return "/item/newRegister";
    }
}
