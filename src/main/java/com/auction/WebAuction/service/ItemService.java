package com.auction.WebAuction.service;

import com.auction.WebAuction.model.Item;
import com.auction.WebAuction.repository.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private HttpSession httpSession;

    private static final String LAST_VIEWED_TIME_MAP_KEY = "lastViewedTimeMap";

    public long incrementViewCount(long id) {
        Item item = itemRepository.findById(id).orElse(null);

        if (item != null && canIncrementViewCount(id)) {
            item.setView_count(item.getView_count() + 1);
            itemRepository.save(item);
            updateLastViewedTime(id);
            return item.getView_count();
        }

        return 0L; // or throw an exception, depending on your requirements
    }

    public long getViewCount(long id) {
        Item item = itemRepository.findById(id).orElse(null);

        return (item != null) ? item.getView_count() : 0L;
    }

    private boolean canIncrementViewCount(long id) {
        Map<Long, LocalDateTime> lastViewedTimeMap = getLastViewedTimeMap();

        LocalDateTime lastViewedTime = lastViewedTimeMap.get(id);

        if (lastViewedTime == null || lastViewedTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            return true;
        }

        return false;
    }

    private void updateLastViewedTime(long id) {
        Map<Long, LocalDateTime> lastViewedTimeMap = getLastViewedTimeMap();
        lastViewedTimeMap.put(id, LocalDateTime.now());
        httpSession.setAttribute(LAST_VIEWED_TIME_MAP_KEY, lastViewedTimeMap);
    }

    private Map<Long, LocalDateTime> getLastViewedTimeMap() {
        Map<Long, LocalDateTime> lastViewedTimeMap = (Map<Long, LocalDateTime>) httpSession.getAttribute(LAST_VIEWED_TIME_MAP_KEY);

        if (lastViewedTimeMap == null) {
            lastViewedTimeMap = new HashMap<>();
        }

        return lastViewedTimeMap;
    }
}