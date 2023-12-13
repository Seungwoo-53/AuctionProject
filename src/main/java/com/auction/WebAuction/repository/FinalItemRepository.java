package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.FinalItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalItemRepository extends JpaRepository<FinalItem,Long> {
    boolean existsByItemId(Long itemId);
}
