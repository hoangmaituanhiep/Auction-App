package app.payload;

import java.io.Serializable;

public class CancelAuctionRequest implements Serializable{
  int auctionId;

  public CancelAuctionRequest(int auctionId) {
    this.auctionId = auctionId;
  }

  public int getAuctionId() {
    return auctionId;
  }
}
