package app.payload;

import java.io.Serializable;

public class AuctionTimeoutPayload implements Serializable{
  private boolean isFinished;
  private int auctionId;
  public AuctionTimeoutPayload(boolean isFinished, int auctionId) {
    this.isFinished = isFinished;
    this.auctionId = auctionId;
  }

  public boolean isFinished() {
    return isFinished;
  }
  public int getAuctionId() {
    return auctionId;
  }
}
