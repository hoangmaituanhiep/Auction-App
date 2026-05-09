package app.payload;

public class SellItemRespondPayload {
  private boolean isSuccess;
  private String error;

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

  public SellItemRespondPayload(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public SellItemRespondPayload(boolean isSuccess, String error) {
    this.isSuccess = isSuccess;
    this.error = error;
  }
}
