package com.auction.WebAuction.dto;

public class ItemUpdateDTO {
    private Long itemId;
    private int price;

    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public ItemUpdateDTO(Long itemId, int price) {
        this.itemId = itemId;
        this.price = price;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    @Override
    public String toString() {
        return "PriceUpdateMessage{" +
                "itemId=" + itemId +
                ", price=" + price +
                ", loginName=" + loginName +
                '}';
    }
}
