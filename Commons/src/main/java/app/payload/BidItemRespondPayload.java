package app.payload;

import java.io.Serializable;

public class BidItemRespondPayload implements Serializable{
  private boolean isSuccess;
  private String error;
  private int Id;

  public BidItemRespondPayload(int Id, boolean isSuccess) {
    this.Id = Id;
    this.isSuccess = isSuccess;
  }

  public BidItemRespondPayload(int Id, boolean isSuccess, String error) {
    this.Id = Id;
    this.error = error;
    this.isSuccess = isSuccess;
  }

  public boolean isSuccess() {
    return isSuccess;
  }
  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }
  public int getId() {
    return Id;
  }
}
