package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberFormRepository {
    private final EntityManager em;
    public boolean existByUserId(String username){
        List<Member> members = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username",username)
                .getResultList();
        return members.stream().findAny().isPresent();
    }
}
