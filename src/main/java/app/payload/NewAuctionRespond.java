package app.payload;

public class NewAuctionRespond {
  private boolean isSuccess;
  private int auctionId;
  private String error;

  public NewAuctionRespond(boolean isSuccess, int auctionId) {
    this.auctionId = auctionId;
    this.isSuccess = isSuccess;
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
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }
}
