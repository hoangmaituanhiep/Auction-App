package app.payload;

import java.io.Serializable;

public class NewAuctionRequest implements Serializable {
  private String name;
  private String duration;
  public NewAuctionRequest(String name, String duration) {
    this.name = name;
    this.duration = duration;
  }
  public String getName() {
    return name;
  }
  public String getDuration() {
    return duration;
  }
}
