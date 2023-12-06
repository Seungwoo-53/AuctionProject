package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.MemberItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberItemRepository extends JpaRepository<MemberItem,Long> {
}
