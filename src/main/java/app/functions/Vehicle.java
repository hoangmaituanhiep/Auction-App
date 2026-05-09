package app.functions;

public class Vehicle extends Item {
  public Vehicle(String name, String detail, int duration, double startingPrice) {
    super(name, detail, duration, startingPrice);
  }

  public String toString() {
    return "Name: " + super.getName() +
        "\nDescribe: " + super.getDetail() +
        "\nPrice: " + super.getCurrentPrice();
  }
}