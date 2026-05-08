package app.packets;

public enum Message {
    WELCOME("Welcome"),
    SEND_AUCTION("Send auction"),
    SEND_AUCTION_ID("Send auction id"),
    CANCLE_AUCTION("Cancel auction"),
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
