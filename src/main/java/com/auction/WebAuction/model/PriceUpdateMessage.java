package com.auction.WebAuction.model;

public class PriceUpdateMessage {
    private Long itemId;
    private int price;

    public PriceUpdateMessage(Long itemId, int price) {
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
                '}';
    }
}
