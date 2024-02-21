package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Member findByUsername(String username);

    @Query("SELECT m.username FROM Member m WHERE m.username = :username")
    String findUsernameStringByUsername(@Param("username") String username);

}
