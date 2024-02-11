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

    public String info(Long itemId, Model model, Authentication authentication) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Long views = itemService.incrementViewCount(itemId);
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);
        String memberString = memberRepository.findUsernameStringByUsername(username);
        String memberUsername = memberItemRepository.findMemberUsernameByItemId(itemId);

        //현재 로그인한 유저가 item테이블에서 enabled가 0인 것의 itemId의 memberItem.
        List<MemberItem> memberItems = memberItemRepository.findByMemberAndItemEnabledFalseAndItemId(member, itemId);
        // finalItem테이블에 해당하는 itemId 가 있는지.
        boolean isItemInFinalTable = finalItemRepository.existsByItemId(itemId);

        optionalItem.ifPresent(item -> {
            long remainingMillis = Duration.between(LocalDateTime.now(), item.getDate()).toMillis();
            model.addAttribute("remainingMillis", remainingMillis);
            model.addAttribute("itemPrice", item.getPrice());
            model.addAttribute("item", item);
            model.addAttribute("itemId",itemId);
            // 최종 낙찰자 정보 입력 버튼
            // 경매남은시간이 없고, 낙찰된 유저만 보이게끔, final정보를 입력안했을때만 보이게끔.
            if(!item.getEnabled()){
                if(memberString.equals(memberUsername)){
                   if(!isItemInFinalTable){
                       System.out.println("final Button");
                       model.addAttribute("showAuctionConfirmationButton",true);
                   }
                }
            }
        });

        // 현재 로그인한 유저의 정보들
        model.addAttribute("member", member);
        model.addAttribute("point", member.getPoint());
        model.addAttribute("memberUsername", memberUsername);
        // 조회수
        model.addAttribute("views", views);
        return "item/detail";
    }

//    public String handleItemDetails(Long itemId, Model model, Authentication authentication) {
//       Optional<Item> optionalItem = itemRepository.findById(itemId);
//        String username = authentication.getName();
//
//       optionalItem.ifPresent(item -> {
//           long remainingMillis = Duration.between(LocalDateTime.now(), item.getDate()).toMillis();
//          model.addAttribute("remainingMillis", remainingMillis);
//         model.addAttribute("itemPrice", item.getPrice());
//           model.addAttribute("item", item);
//
//            if (authentication != null) {
//              handleAuthenticationDetails(itemId, model, authentication);
//           }
//        });
//
//        return optionalItem.isPresent() ? handleAdditionalDetails(itemId, model) : "redirect:/";
//   }
//
//   private void handleAuthenticationDetails(Long itemId, Model model, Authentication authentication) {
//       System.out.println("handleAuthenticationDetails 메소드가 실행 되었습니다.");
//       String username = authentication.getName();
//       Member member = memberRepository.findByUsername(username);
//
//       List<MemberItem> memberItems = memberItemRepository.findByMemberAndItemEnabledFalseAndItemId(member, itemId);
//      boolean isItemInFinalTable = finalItemRepository.existsByItemId(itemId);
//
//       model.addAttribute("showAuctionConfirmationButton", !(memberItems.isEmpty() || isItemInFinalTable));
//    }
//
//   private String handleAdditionalDetails(Long itemId, Model model) {
//        Long views = itemService.incrementViewCount(itemId);
//       model.addAttribute("views", views);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        Member member = memberRepository.findByUsername(username);
//
//        int point = member.getPoint();
//        model.addAttribute("member", member);
//        model.addAttribute("point", point);
//
//        String memberUsername = memberItemRepository.findMemberUsernameByItemId(itemId);
//        model.addAttribute("memberUsername", memberUsername);
//
//       return "item/detail";
//    }

}
