package com.auction.WebAuction.service;


import com.auction.WebAuction.error.InsufficientPointsException;
import com.auction.WebAuction.model.Member;
import com.auction.WebAuction.model.MemberItem;
import com.auction.WebAuction.model.Role;
import com.auction.WebAuction.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        member.setPoint(10000);
        return memberRepository.save(member);
    }
    @Transactional
    public void returnPoints(Long memberId, int points) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        member.setPoint(member.getPoint() + points);
        memberRepository.save(member);
    }

    @Transactional
    public void deductPoints(Long memberId, int points) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found"));

        int currentPoints = member.getPoint();
        if (currentPoints >= points) {
            member.setPoint(currentPoints - points);
            memberRepository.save(member);
        } else {
            throw new InsufficientPointsException("Insufficient points to make the bid.");
        }
    }


}