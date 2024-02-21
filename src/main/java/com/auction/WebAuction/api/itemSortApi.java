package com.auction.WebAuction.api;

import com.auction.WebAuction.dto.ItemDTO;
import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.repository.ItemRepository;
import com.auction.WebAuction.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class itemSortApi {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/itemSortType")
    public List<ItemDTO> handleItemSortType(@RequestParam("selectedButtonId") String selectedButtonId) {
        List<Item> items;

        if(selectedButtonId.equals("viewSortBtn")){
            System.out.println("viewSortBtn!");
            items = itemRepository.findAllByOrderByViewCountDesc();
        } else if(selectedButtonId.equals("priceSortBtn")){
            System.out.println("priceSortBtn!");
            items = itemRepository.findAllByOrderByPriceDesc();
        } else {
            System.out.println("timeSortBtn!");
            items = itemRepository.findAllByOrderByDateDesc();
        }

        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setInfo(item.getInfo());
        dto.setPrice(item.getPrice());
        dto.setView_count(item.getView_count());
        dto.setDate(item.getDate());
        dto.setUrl(item.getUrl());
        dto.setEnabled(item.isEnabled());
        return dto;
    }

}
