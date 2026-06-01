package app.payload;

import java.io.Serializable;

public class KickUser implements Serializable{
  String username;

  public String getUsername() {
    return username;
  }

  public KickUser(String username) {
    this.username = username;
  }
}
