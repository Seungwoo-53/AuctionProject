package com.auction.WebAuction.controller;


import com.auction.WebAuction.error.InsufficientPointsException;
import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.model.MemberItemBackup;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.ItemService;
import com.auction.WebAuction.service.MemberService;
import com.auction.WebAuction.service.TimeRemainingService;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private ItemService itemService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TimeRemainingService timeRemainingService;

   @GetMapping("/detail/{id}")
    public String ItemDetail(@PathVariable Long id, Model model) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        Long views = itemService.incrementViewCount(id);
        System.out.println("Received ID: " + id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

       Member member = memberRepository.findByUsername(username);
       int point = member.getPoint();
       model.addAttribute("member",member);
       // 이제 'point' 변수에 해당 사용자의 포인트가 들어있음
       model.addAttribute("point", point);

       String memberUsername = memberItemRepository.findMemberUsernameByItemId(id);
       model.addAttribute("memberUsername", memberUsername);
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
    public String updatePrice(@PathVariable("id") long id, @RequestParam int price, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 아이템 조회
            Optional<Item> itemOptional = itemRepository.findById(id);
            if (itemOptional.isPresent()) {
                Item item = itemOptional.get();

                // 현재 사용자 정보 조회
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                Member member = memberRepository.findByUsername(username);
                model.addAttribute("member",member);
                // 현재 가격보다 높은 경우에만 업데이트
                if (price > item.getPrice()) {
                    // 포인트 차감 전에 사용자가 가진 포인트와 비교
                    int currentPoints = member.getPoint();

                    if (price <= currentPoints) {
                        // 아이템의 가격 업데이트
                        item.setPrice(price);
                        // 포인트 차감
                        memberService.deductPoints(member.getId(), price);

                        itemService.addPoints(id);

                        // 입찰 내역 업데이트
                        itemService.updateItemPriceAndLinkToMember(member.getId(), item.getId(), price);


                        // 모델에 결과 값 추가
                        itemRepository.save(item);
                        model.addAttribute("price", item.getPrice());
                        model.addAttribute("points", member.getPoint());
                        // 성공 메시지 반환
                        //return "Price updated successfully! Points deducted: " + price;
                        redirectAttributes.addFlashAttribute("successMessage", "성공적으로 처리되었습니다!");
                    } else {
                        // 사용자가 가진 포인트보다 많은 포인트 입력은 거부
//                        return "너무 많은 가격을 입력하셨습니다. 본인의 point : "+ currentPoints+"입니다.";
                        redirectAttributes.addFlashAttribute("errorMessage", "너무 많은 가격을 입력하셨습니다.");
                    }
                } else {
                    // 현재 가격보다 낮은 입찰은 거부
//                    return "너무 낮은 가격을 입력하셨습니다. 해당 가격은 : "+item.getPrice()+"입니다.";
                    redirectAttributes.addFlashAttribute("errorMessage", "너무 낮은 가격을 입력하셨습니다..");
                }
                return "redirect:/item/detail/" + id;
            }

            else {
                // 아이템이 존재하지 않을 경우
                return "Item not found!";
            }
        } catch (Exception e) {
            // 그 외 예외 처리
            return "An error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/register")
    public String register(Model model){
        List<Item> item = itemRepository.findAll();
        model.addAttribute("item", item);
        return "/item/register";
    }
    @GetMapping("/newRegister")
    public String newRegister(){
        return "/item/newRegister";
    }

    @PostMapping("/newRegister")
    public String newRegister(Item item, BindingResult result){
        if(result.hasErrors()){
           return "/";
        }
        // 현재 날짜와 시간
        LocalDateTime registrationDate = LocalDateTime.now();
        // 만료 시간 설정 (등록한 날짜부터 24시간 뒤)
        LocalDateTime expirationDate = registrationDate.plus(24, ChronoUnit.HOURS);
        // 만료 시간 설정
        item.setDate(expirationDate);

        // 기타 필요한 설정
        item.setEnabled(true);

        // 서비스를 통한 저장 등의 로직 수행
        itemService.save(item);
        return "redirect:/";
    }

    @GetMapping("/editRegister/{id}")
    public String editRegister(@PathVariable("id") long id,Model model){
        Optional<Item> itemOptional = itemService.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            model.addAttribute("item", item);
            model.addAttribute("item", itemOptional.get());
            return "/item/editRegister";
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
