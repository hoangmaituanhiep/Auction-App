package app.functions;

public class Electronics extends Item {

  public Electronics(String name, String detail, double startingPrice) {
    super(name, detail, startingPrice);
  }

  @Override
  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}
