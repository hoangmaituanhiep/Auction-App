package app.payload;

import java.io.Serializable;


public class ConnectionRespondPayload implements Serializable{
  private boolean isSuccess;
  private String error;

  public ConnectionRespondPayload(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public ConnectionRespondPayload(boolean isSuccess, String error) {
    this.isSuccess = isSuccess;
    this.error = error;
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
