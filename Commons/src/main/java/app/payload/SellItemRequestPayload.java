package app.payload;

import java.io.Serializable;

import app.functions.Item;

public class SellItemRequestPayload implements Serializable{
  Item item;
  int auctionId;

  public SellItemRequestPayload (Item item) {
    this.item = item;
    this.auctionId = -1;
  }

  public SellItemRequestPayload (Item item, int auctionId) {
    this.item = item;
    this.auctionId = auctionId;
  }

  public Item getItem() {
    return item;
  }

  public int getAuctionId() {
    return auctionId;
  }
}
