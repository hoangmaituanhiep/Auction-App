package app.functions;

import java.time.LocalDateTime;
import java.util.*;

import app.Client;

public class Auction {
    private final String auctionId;
    private double step;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Map<String, Item> auctionItem;
    private Map<String, User> onlineUser;
    private List<Client> clients;
    private Admin admin;

    public Auction(String auctionId, Seller seller, double step,
            LocalDateTime starTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.step = step;
        this.startTime = starTime;
        this.endTime = endTime;
        this.status = "PENDING";
        this.auctionItem = new HashMap<String, Item>();
        this.onlineUser = new HashMap<>();
        clients = new ArrayList<Client>();
    }

    // Auction ID
    public String getAuctionId() {
        return auctionId;
    }

    // Step
    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    //Start Time
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //End Time
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    //Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //Auction Item
    public Item getItem(String id) {
        return auctionItem.get(id);
    }

    public double getCurrentHighestPrice(String id) {
        return getItem(id).getCurrentPrice();
    }

    public boolean setCurrentHighestPrice(String id, double price) {
        if (getItem(id).getCurrentPrice() < price) {
            getItem(id).setNewPrice(price);
            return true;
        }
        return false;
    }

    public void add(Map<String, Item> list_item) {
        auctionItem.putAll(list_item);
    }

    public Map<String, Item> getAuctionItem() {
        return auctionItem;
    }

    //Online User
    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        onlineUser.put(admin.toString(), admin);
    }

    public Map<String, User> getOnlineUser() {
        return onlineUser;
    }

    public void addOnlineUser(User user) {
        onlineUser.put(user.toString(), user);
    }

    public User getUser(String username) {
        if (onlineUser.containsKey(username)) {
            return onlineUser.get(username);
        }
        return null;
    }
    public List<Client> getClients() {
        return clients;
    }
    public void addClient(Client client) {
        clients.add(client);
    }
}
