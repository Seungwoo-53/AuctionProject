package com.auction.WebAuction.api;

import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.repository.MemberRepository;
import com.auction.WebAuction.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MemberPointApi {
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/getMemberPoint")
    public ResponseEntity<Map<String, Object>> getMemberPoint(HttpServletRequest request, Authentication authentication) {
        // 현재 로그인한 사용자의 아이디를 가져오는 예시 코드
        String username = authentication.getName();
        Member member = memberRepository.findByUsername(username);
        int memberPoint = member.getPoint();

        // JSON 형태로 사용자의 포인트 정보와 아이디를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("memberPoint", memberPoint);
        return ResponseEntity.ok().body(response);
    }
}
