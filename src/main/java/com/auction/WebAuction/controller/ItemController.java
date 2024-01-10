package com.auction.WebAuction.controller;


import com.auction.WebAuction.error.InsufficientPointsException;
import com.auction.WebAuction.error.ValidationException;
import com.auction.WebAuction.model.*;
import com.auction.WebAuction.repository.FinalItemRepository;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemRepository memberItemRepository;
    @Autowired
    private FinalItemRepository finalItemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TimeRemainingService timeRemainingService;
    @Autowired
    private ItemUpdateService itemUpdateService;
    @Autowired
    private ItemRegisterService itemRegisterService;
    @Autowired
    private ItemDetailService itemDetailService;

    @GetMapping("/detail/{id}")
    public String itemDetail(@PathVariable Long id, Model model, Authentication authentication) {
        return itemDetailService.handleItemDetails(id, model, authentication);
    }
    @PostMapping("/detail/{id}")
    public String updatePrice(@PathVariable("id") long id, @RequestParam int price,
                              Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        return itemUpdateService.updateItemPrice(id, price, authentication, model, redirectAttributes);
    }

    @GetMapping("/final/{id}")
    public String finalItem(@PathVariable Long id,Model model){
        Optional<Item> finalItem = itemRepository.findById(id);
        if(finalItem.isPresent()) {
            Item item = finalItem.get();
            model.addAttribute(item);
        }
        return "item/final";
    }

    @PostMapping("/final/{id}")
    public String finalItemSave(@PathVariable("id") long id, @ModelAttribute FinalItem finalItem){
        itemService.finalItemSave(id,finalItem);
        return "redirect:/";
    }
    @GetMapping("/register")
    public String register(Model model){
        List<Item> item = itemRepository.findAll();
        model.addAttribute("item", item);
        return "item/register";
    }
    @GetMapping("/newRegister")
    public String newRegister(Model model){

        return "item/newRegister";
    }




    @PostMapping("/newRegister")
    public String newRegister(@ModelAttribute("item") Item item, BindingResult result) {
        try {
            itemRegisterService.registerNewItem(item, result);
            return "redirect:/";
        } catch (ValidationException e) {
            return "/";
        }
    }

    @GetMapping("/editRegister/{id}")
    public String editRegister(@PathVariable("id") long id,Model model){
        Optional<Item> itemOptional = itemService.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            model.addAttribute("item", item);
            model.addAttribute("item", itemOptional.get());
            return "item/editRegister";
        } else {
            return "redirect:/";
        }
    }
    @Transactional
    @PutMapping("/editRegister/{id}")
    public String updateRegister(@PathVariable("id") long id, @ModelAttribute Item item) {
        // 기존 아이템을 데이터베이스에서 가져옴
        Optional<Item> itemOptional = itemRepository.findById(id);
        if(itemOptional.isPresent()) {
            itemService.updateItem(id, item);
        }
        return "redirect:/item/register";
    }

    @Transactional
    @DeleteMapping("/delRegister/{id}")
    public String deleteRegister(@PathVariable long id){
        itemService.deleteItem(id);
        return "redirect:/item/register";
    }

}
