package app.payload;

import java.io.Serializable;

public class AntiSnippingRespondPayload implements Serializable {
  int auctionId;
  String name;

  public AntiSnippingRespondPayload(int auctionId, String name) {
    this.auctionId = auctionId;
    this.name = name;
  }

  public int getAuctionId() {
    return auctionId;
  }

  public String getName() {
    return name;
  }
}
