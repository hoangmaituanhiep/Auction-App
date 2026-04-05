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
    
    public String getName() {return name;}
    public double getCurrent_Price() {return current_Price;}
    public String getDetail() {return detail;}
    
    public abstract String toString();
}
class Electronics extends Item{
    private String company;
    public Electronics(String name, String company) {
        super(name);
        this.company=company;
    }
    public String getCompany() {return company;}
    
    public String toString() {
        return "Name: " + super.getName() + 
                "\nCompany: " + company + 
                "\nDescribe: " + super.getDetail() + 
                "\nPrice: " + super.getCurrent_Price();
    }
}
class Art extends Item {
    private String artist_name;
    public Art(String name, String artist_name) {
        super(artist_name);
        this.artist_name=artist_name;
    }
    public String getArtist_name() {return artist_name;}
    
    public String toString() {
        return "Name: " + super.getName() +
                "\nArtist: : " + artist_name +
                "\nDescribe: " + super.getDetail() +
                "\nPrice: " + super.getCurrent_Price();
    }
}
class Vehicle extends Item {
    private String company;
    public Vehicle(String name, String company) {
        super(name);
        this.company=company;
    } 
    public String getCompany() {return company;}

    public toString() {
        return "Name: " + super.getName() +
                "\nCompany: " + company +
                "\nDescribe: " + super.getDetail() +
                "\nPrice: " + super.getCurrent_Price();
    }
}