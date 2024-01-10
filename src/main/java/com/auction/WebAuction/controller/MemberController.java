package com.auction.WebAuction.controller;


import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItemBackup;
import com.auction.WebAuction.repository.MemberItemBackupRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemBackupRepository memberItemBackupRepository;

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "member/signup";
    }
    @GetMapping("/signup2")
    public String signup2(){
        return "/member/signup2";
    }
    @PostMapping("/signup")
    public String signup(Member member){
        memberService.save(member);
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(Model model) {
        List<Item> items = memberService.getMyItems();
        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        return "member/mypage";
    }

    @GetMapping("list")
    public String memberList(Model model){
        List<Member> member = memberRepository.findAll() ;

        model.addAttribute("member",member);
        return "member/list";
    }

}
