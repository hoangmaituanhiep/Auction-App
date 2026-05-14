package app.functions;

public class Vehicle extends Item {
  public Vehicle(String name, String detail, double startingPrice) {
    super(name, detail, startingPrice);
  }

  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}