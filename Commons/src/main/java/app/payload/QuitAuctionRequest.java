package app.payload;

import java.io.Serializable;

public class QuitAuctionRequest implements Serializable{
  private int auctionId;

  public QuitAuctionRequest(int auctionId) {
    this.auctionId = auctionId;
  }

  public int getAuctionId() {
    return auctionId;
  }
}
