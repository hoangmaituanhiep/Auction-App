package app.functions;

public class Art extends Item {
  public Art(String name, String detail, int duration, double startingPrice) {
    super(name, detail, duration, startingPrice);
  }

  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}
