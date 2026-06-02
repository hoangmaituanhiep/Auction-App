package app.payload;

import java.io.Serializable;

import app.functions.Item;

public class WinnerPayload implements Serializable{
  private String username;
  private int auctionId;
  private Item item;

  public WinnerPayload(String username, int auctionid, Item item) {
    this.username = username;
    this.auctionId = auctionid;
    this.item = item;
  }

  public String getUsername() {
    return username;
  }

  public int getAuctionId() {
    return auctionId;
  }

  public Item getItem() {
    return item;
  }
}
