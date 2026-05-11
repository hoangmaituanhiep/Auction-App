package app.functions;

public class Art extends Item {
  public Art(String name, String detail, double startingPrice) {
    super(name, detail, startingPrice);
  }

  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}
