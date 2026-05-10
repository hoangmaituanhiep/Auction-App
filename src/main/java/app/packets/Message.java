package app.packets;

public enum Message {
    WELCOME("Welcome"),
    SEND_ITEM_REQUEST("Send item request"),
    SEND_ITEM_RESPOND("Send item respond"),
    BROADCAST_NEW_ITEM("Globally notify"),
    DISCONNECT("disconnect"),
    ERROR("Error"),
    JOIN_AUCTION("Join auction"),
    LOGIN_REQUEST("Wait for login"),
    LOGIN_RESPONSE("Send login signal"),
    SIGNUP_REQUEST("Wait for signup"),
    SIGNUP_RESPONSE("Send signup signal");

    private String message;
    Message(String message) {this.message = message;}
    public String getMessage() {
      return this.message;
    }
}
