package com.auction.WebAuction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    private int price;

}