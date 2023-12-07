package com.auction.WebAuction.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TimeRemainingService {
    public Duration calculateTimeRemaining(LocalDateTime expirationDate) {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, expirationDate);
    }
}