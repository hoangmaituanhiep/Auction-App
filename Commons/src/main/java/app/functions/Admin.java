package app.functions;

import java.util.HashMap;
import java.util.Map;

public class Admin extends User {
  private String password;
  private Map<String, User> listUser;

  public Admin() {
    this.userName = "admin1";
    this.password = "toidongtinh";
    listUser = new HashMap<>();
  }

  public String getPassword() {
    return password;
  }
  public String getUsername() {
    return userName;
  }

  public void addUser(User user) {
    listUser.put(user.getUserName(), user);
  }

  public Map<String, User> getListUser() {
    return listUser;
  }

  public User findUser(String userName) {
    return listUser.get(userName);
  }

  public String toString() {
    return "#admin:" + getUserName();
  }
}
