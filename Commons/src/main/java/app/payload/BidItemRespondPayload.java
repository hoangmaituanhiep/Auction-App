package app.payload;

public class BidItemRespondPayload {
  private boolean isSuccess;
  private String error;

  public BidItemRespondPayload(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public BidItemRespondPayload(boolean isSuccess, String error) {
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
}
