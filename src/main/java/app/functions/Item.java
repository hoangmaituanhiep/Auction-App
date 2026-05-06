package app.functions;

import java.time.*;

import javafx.scene.image.Image;

public abstract class Item extends Entity {
    private String name;
    private double startingPrice;
    private double current_Price;
    private double maxPrice;
    private String detail = "Seller is too lazy to write anything here.";
    private LocalDateTime endTime;
    private Image image;

    public Item(String name) {
        this.name = name;
    }

    public void setStartingPrice(double price) {
        this.startingPrice = price;
        this.current_Price = price;
    }

    public void setNewPrice(double NewPrice) {
        this.current_Price = NewPrice;
    }

    public void writeDetail(String detail) {
        this.detail = detail;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public String getName() {
        return name;
    }

    public double getCurrent_Price() {
        return current_Price;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double Price) {
        this.maxPrice = Price;
    }

    public String getDetail() {
        return detail +
                "\nStrating price: " + startingPrice +
                "\nCurrent highest price: " + current_Price;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public abstract String toString();
}

class Electronics extends Item {

    public Electronics(String name, String company) {
        super(name);
    }

    @Override
    public String toString() {
        return "Name: " + super.getName() +
                "\nDescribe: " + super.getDetail() +
                "\nPrice: " + super.getCurrent_Price();
    }
}

class Art extends Item {
    public Art(String name, String artist_name) {
        super(name);
    }

    public String toString() {
        return "Name: " + super.getName() +
                "\nDescribe: " + super.getDetail() +
                "\nPrice: " + super.getCurrent_Price();
    }
}

class Vehicle extends Item {
    public Vehicle(String name, String company) {
        super(name);
    }

    public String toString() {
        return "Name: " + super.getName() +
                "\nDescribe: " + super.getDetail() +
                "\nPrice: " + super.getCurrent_Price();
    }
}