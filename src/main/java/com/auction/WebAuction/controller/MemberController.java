package com.auction.WebAuction.controller;

import com.auction.WebAuction.dto.MemberLoginDto;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String login(){
        return "/member/login";
    }
//    @PostMapping("/loginAction")
//    public String login(MemberLoginDto dto) {
//        boolean isValidMember = memberService.isValidMember(dto.getUsername(), dto.getUserpass());
//        if (isValidMember)
//            return "dashboard";
//        return "/member/login";
//    }
    @GetMapping("/signup")
    public String signup(){
        return "/member/signup";
    }
    @PostMapping("/signup")
    public String signup(Member member){
        memberService.save(member);
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(){
        return "/member/mypage";
    }
}
