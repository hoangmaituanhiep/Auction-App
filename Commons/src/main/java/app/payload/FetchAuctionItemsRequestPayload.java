package app.payload;

import java.io.Serializable;

public class FetchAuctionItemsRequestPayload implements Serializable {
  private final int auctionId;

  public FetchAuctionItemsRequestPayload(int auctionId) {
    this.auctionId = auctionId;
  }

  public int getAuctionId() {
    return auctionId;
  }
}