package app.functions;
public abstract class Item extends Entity {
    private String name;
    private double current_Price;
    private String detail = "Seller is too lazy to write anything here.";
    Item(String name) { //Giá khởi điểm
        this.name = name;
    }
    public void setNewPrice(double NewPrice){ //Giá hiện tại
        if (NewPrice > current_Price) {
            this.current_Price = NewPrice;
        } else {
            System.out.println("Hahaha Sucker.");
        }
    }
    public void writeDetail (String detail) {this.detail = detail;}

    public double getCurrent_Price() {return current_Price;}
    public String getDetail() {return detail;}
}
class Electronics extends Item{
    private String company;
    public void setCompany(String company) {this.company = company;}
    public String getCompany() {return company;}
}
class Art extends Item {
    private String artist_name;
    public void setArtist_name(String artist) {this.artist_name = artist;}
    public String getArtist_name() {return artist_name;}
}
class Vehicle extends Item {
    private String company;
    public void setCompany(String company) {this.company = company;}
    public String getCompany() {return company;}
}