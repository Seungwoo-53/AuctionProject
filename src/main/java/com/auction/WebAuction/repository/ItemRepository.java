package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findAllByOrderByPriceDesc();
    List<Item> findAllByOrderByDateDesc();
    @Query("SELECT e FROM Item e ORDER BY e.view_count DESC")
    List<Item> findAllByOrderByViewCountDesc();

}
