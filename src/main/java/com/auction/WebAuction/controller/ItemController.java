package com.auction.WebAuction.controller;


import com.auction.WebAuction.error.ValidationException;
import com.auction.WebAuction.model.*;
import com.auction.WebAuction.repository.FinalItemRepository;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @Autowired
    private MemberItemService memberItemService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/detail/{id}")
    public String itemDetail(@PathVariable Long id, Model model, Authentication authentication) {
        return itemDetailService.info(id, model, authentication);
    }
    @MessageMapping("/item/detail/{itemId}")
    @SendTo("/item/detail/{itemId}")
    public void updatePrice(PriceUpdateMessage message,Authentication authentication) {
        // 가격 업데이트 로직 구현
        // 예를 들어, 데이터베이스에서 경매 상품을 가져와 가격을 업데이트하고 저장합니다.
        System.out.println("WebSocket Success");
        Optional<Item> itemOptional = itemRepository.findById(message.getItemId());
        int prePrice, newPrice;
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            Long itemId = item.getId();
            prePrice = item.getPrice();
            newPrice = message.getPrice();

            if (newPrice > prePrice) {
                // Check if the user has enough points
                int currentPoints = member.getPoint();
                if (newPrice <= currentPoints) {
                    // Update item price and deduct points
                    item.setPrice(newPrice);
                    memberService.deductPoints(member.getId(), newPrice);
                    itemService.addPoints(itemId);
                    itemService.updateItemPriceAndLinkToMember(member.getId(), item.getId(), newPrice);

                    // Save changes
                    itemRepository.save(item);

                    // Success message
                    messagingTemplate.convertAndSend("/topic/item/detail/" + item.getId(), message);

                    System.out.println("Server Success");
                } else {
                    // Insufficient points
                    System.out.println("No Have Money");
                }
            } else {
                // Lower bid than the current price
                System.out.println("Lose Money");
            }
        } else {
            System.out.println("-- Error --");
        }

    }
//    @PostMapping("/detail/{id}")
//    public String updatePrice(@PathVariable("id") long id, @RequestParam int price,
//                              Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
//        return itemUpdateService.updateItemPrice(id, price, authentication, model, redirectAttributes);
//    }

    @GetMapping("/final/{id}")
    public String finalItem(@PathVariable Long id,Model model,Authentication authentication){
        String currentUsername = authentication.getName();
        Member member = memberRepository.findByUsername(currentUsername);
        long currentUserId = member.getId(); // 현재 로그인한 유저의 id

        MemberItem memberItem = memberItemRepository.findByItemId(id);
        Long memberId = memberItem.getMember().getId();

        // 로그인한 유저랑 최종낙찰자가 다를 때 에러페이지
        if (currentUserId != memberId) {
            // 접근 권한이 없는 경우 에러 페이지를 보여줌
            return "/item/error";
        }

        Optional<Item> finalItem = itemRepository.findById(id);
        if(finalItem.isPresent()) {
            Item item = finalItem.get();
            model.addAttribute("item",item);
        }
        String username = memberItemService.finalItemPageGetUsername(id);
        model.addAttribute("username",username);

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

    @GetMapping("/error")
    public String errorPage(){
        return "item/error";
    }
}
