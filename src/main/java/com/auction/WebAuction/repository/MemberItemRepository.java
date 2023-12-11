package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.MemberItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberItemRepository extends JpaRepository<MemberItem,Long> {


    List<MemberItem> findByItem_Id(Long itemId);

    @Query("SELECT mi.price FROM MemberItem mi WHERE mi.item.id = :itemId")
    int findPriceByItemId(@Param("itemId") Long itemId);

    MemberItem findByMemberId(Long memberItemId);
    MemberItem findByItemId(Long memberItemId);
    @Query("SELECT mi.member.username FROM MemberItem mi WHERE mi.item.id = :itemId")
    String findMemberUsernameByItemId(@Param("itemId") Long itemId);
    void deleteByItemId(Long itemId);

}
