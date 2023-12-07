package com.auction.WebAuction.controller;


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

import java.util.List;

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
        return "/member/login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "/member/signup";
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
    public String mypage(Model model){
        // 현재 로그인한 유저의 username을 가져옵니다.
        String username = getLoggedInUsername();

        // username을 기반으로 Member 엔터티를 조회하여 해당 유저의 id를 가져옵니다.
        Member loggedInMember = memberRepository.findByUsername(username);
        Long loggedInUserId = loggedInMember.getId();

        // 현재 로그인한 유저의 id를 기반으로 MemberItemBackup을 조회하여 해당 유저의 참여 경매 목록을 가져옵니다.
        List<MemberItemBackup> auctionList = memberItemBackupRepository.findByMember_Id(loggedInUserId);

        // 모델에 경매 목록을 추가하여 뷰로 전달합니다.
        model.addAttribute("auctionList", auctionList);


        // 다른 필요한 정보들도 모델에 추가
        return "/member/mypage";
    }

    // 현재 로그인한 유저의 id를 가져오는 메서드
    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
