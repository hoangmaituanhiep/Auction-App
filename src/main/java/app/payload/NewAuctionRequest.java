package app.payload;

public class NewAuctionRequest {
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
