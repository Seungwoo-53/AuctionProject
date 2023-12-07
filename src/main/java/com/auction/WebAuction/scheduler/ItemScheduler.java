package com.auction.WebAuction.scheduler;

import com.auction.WebAuction.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ItemScheduler {

    @Autowired
    private ItemService itemService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행 (단위: 밀리초)
    public void updateItemValidity() {
        itemService.updateItemValidity();
    }
}