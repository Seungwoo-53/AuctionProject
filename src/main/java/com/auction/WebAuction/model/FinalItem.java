package com.auction.WebAuction.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FinalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String username;
    private String address;
    private String phone;

}
