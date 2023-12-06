package com.auction.WebAuction.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MemberItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberItemId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
