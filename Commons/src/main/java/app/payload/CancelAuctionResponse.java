package app.payload;

import java.io.Serializable;

public class CancelAuctionResponse implements Serializable{
  int auctionId;
  boolean isSuccess;
  String error;

  public CancelAuctionResponse(boolean isSuccess, int auctionId) {
    this.isSuccess = isSuccess;
    this.auctionId = auctionId;
  }

  public CancelAuctionResponse(boolean isSuccess, String error){
    this.isSuccess = isSuccess;
    this.error = error;
  }

  public boolean isSuccess(){
    return isSuccess;
  }

  public String getError() {
    return error;
  }

  public int getAuctionId() {
    return auctionId;
  }
}
