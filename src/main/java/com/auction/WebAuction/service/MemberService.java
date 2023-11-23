package com.auction.WebAuction.service;


import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.Role;
import com.auction.WebAuction.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public Member save(Member member){
        String encodedPassword = passwordEncoder.encode(member.getUserpass());
        member.setUserpass(encodedPassword);
        member.setEnabled(true);
        Role role = new Role();
        role.setId(1L);
        member.getRoles().add(role);
        return memberRepository.save(member);
    }
}