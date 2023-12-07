package com.auction.WebAuction.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MemberItemBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long memberItemBackupId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int price;

    // 추가적인 필드도 가능
}