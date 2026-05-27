package app.payload;

import java.io.Serializable;

public class WinnerPayload implements Serializable{
  String username;

  public WinnerPayload(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
