package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.MemberItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberItemRepository extends JpaRepository<MemberItem,Long> {

    void deleteByItem_Id(Long itemId);
    List<MemberItem> findByItem_Id(Long itemId);

    int  findPriceByMember_Id(Long memberId);

    @Query("SELECT mi.member.username FROM MemberItem mi WHERE mi.item.id = :itemId")
    String findMemberUsernameByItemId(@Param("itemId") Long itemId);
}
