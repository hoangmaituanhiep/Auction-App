package app.functions;

public class Electronics extends Item {

  public Electronics(String name, String detail, int duration, double startingPrice) {
    super(name, detail, duration, startingPrice);
  }

  @Override
  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}
