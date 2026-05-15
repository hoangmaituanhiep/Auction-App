package app.payload;

import java.io.Serializable;

public class NewAuctionRespond implements Serializable {
  private boolean isSuccess;
  private int auctionId;
  private String name;
  private String duration;
  private String error;

  public NewAuctionRespond(boolean isSuccess, int auctionId, String name, String duration) {
    this.auctionId = auctionId;
    this.isSuccess = isSuccess;
    this.name = name;
    this.duration = duration;
  }

  public NewAuctionRespond(boolean isSuccess, String error) {
    this.isSuccess = isSuccess;
    this.error = error;
  }
  
  public boolean isSuccess() {
    return isSuccess;
  }
  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  public int getAuctionId() {
    return auctionId;
  }
  public void setAuctionId(int auctionId) {
    this.auctionId = auctionId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDuration() {
    return duration;
  }
  public void setDuration(String duration) {
    this.duration = duration;
  }
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }
}
