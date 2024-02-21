package com.auction.WebAuction.controller;


import com.auction.WebAuction.dto.MemberRegisterDTO;
import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItemBackup;
import com.auction.WebAuction.repository.MemberItemBackupRepository;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.MemberService;
import com.auction.WebAuction.validator.CheckUserIdValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberItemBackupRepository memberItemBackupRepository;
    private final CheckUserIdValidator checkUserIdValidator;
    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    // 커스텀 유효성 검증을 위해 추가
    @InitBinder
    public void validatorBinder(WebDataBinder binder){
        binder.addValidators(checkUserIdValidator);

    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("memberRegisterDTO", new MemberRegisterDTO());

        return "member/signup";
    }
    @GetMapping("/signup2")
    public String signup2(){
        return "/member/signup2";
    }
    @PostMapping("/signup")
    public String signup(@Valid MemberRegisterDTO memberRegisterDTO, Errors errors,Member member,Model model){
        if(errors.hasErrors()){
            // 회원가입 실패시 입력 데이터 값을 유지
            model.addAttribute("memberRegisterDTO", memberRegisterDTO);

            // 유효성 통과 못한 필드와 메시지를 핸들링
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            for(String key : validatorResult.keySet()){
                model.addAttribute(key, validatorResult.get(key));
            }
            // 회원가입 페이지로 다시 리턴
            return "/member/signup";
        }


        memberService.save(member);
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(Model model,Authentication authentication) {
        List<Item> items = memberService.getMyItems();
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);
        List<Long> remainingMillisList = items.stream()
                .map(item -> Duration.between(LocalDateTime.now(), item.getDate()).toMillis())
                .collect(Collectors.toList());

        model.addAttribute("remainingMillisList", remainingMillisList);
        model.addAttribute("item", items);
        model.addAttribute("point", member.getPoint());
        return "member/mypage";
    }

    @GetMapping("list")
    public String memberList(Model model){
        List<Member> member = memberRepository.findAll() ;

        model.addAttribute("member",member);
        return "member/list";
    }

}
