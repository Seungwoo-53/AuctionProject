package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Service
public class ItemUpdateService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ItemService itemService;

    // 아이템 가격을 갱신 | 낙찰 할때 도는 로직 | 현재는 사용X POST방식 입찰할 때 사용하던 코드.
    public String updateItemPrice(long itemId, int newPrice, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the item
            Optional<Item> itemOptional = itemRepository.findById(itemId);
            if (itemOptional.isPresent()) {
                Item item = itemOptional.get();

                // Retrieve the current user
                String username = authentication.getName();
                Member member = memberRepository.findByUsername(username);
                model.addAttribute("member", member);

                // Check if the new price is higher than the current price
                if (newPrice > item.getPrice()) {
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

                        // Set model attributes
                        model.addAttribute("price", item.getPrice());
                        model.addAttribute("points", member.getPoint());

                        // Success message
                        redirectAttributes.addFlashAttribute("successMessage", "성공적으로 처리되었습니다!");
                    } else {
                        // Insufficient points
                        redirectAttributes.addFlashAttribute("errorMessage", "너무 많은 가격을 입력하셨습니다.");
                    }
                } else {
                    // Lower bid than the current price
                    redirectAttributes.addFlashAttribute("errorMessage", "너무 낮은 가격을 입력하셨습니다.");
                }

                // Redirect to item detail page
                return "redirect:/item/detail/" + itemId;
            } else {
                // Item not found
                return "Item not found!";
            }
        } catch (Exception e) {
            // Handle other exceptions
            return "An error occurred: " + e.getMessage();
        }
    }
}

