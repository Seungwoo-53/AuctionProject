package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Member findByUsername(String username);
}
