package com.auction.WebAuction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String info;
    private int price;
    private long view_count;
    private LocalDateTime date;
    private String url;
    private Boolean enabled;

    @OneToMany(mappedBy = "item")
    private List<MemberItem> memberItems = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
    private List<FinalItem> finalItems;

    public boolean isEnabled() {
        // enabled 필드의 값 반환
        return this.enabled != null && this.enabled;
    }

    public Long getId() {
        // id 필드의 값 반환
        return this.id;
    }
}
