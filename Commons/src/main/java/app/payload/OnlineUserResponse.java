package app.payload;

import java.io.Serializable;
import java.util.List;

public class OnlineUserResponse implements Serializable{
  private List<String> onlineUsernames;
  public OnlineUserResponse(List<String> online) {
    this.onlineUsernames = online;
  }
  public List<String> getOnlineUsernames() {
    return this.onlineUsernames;
  }
}
