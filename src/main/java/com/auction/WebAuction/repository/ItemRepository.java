package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {

}
