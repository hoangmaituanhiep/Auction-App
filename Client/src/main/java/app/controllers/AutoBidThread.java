package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

import app.NetworkClient;
import app.functions.Item;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;

public class AutoBidThread implements Runnable{
  private boolean biddable = true;
  private double amount = 0;
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
  private Item item;
  private NetworkClient networkClient = NetworkClient.getInstance();
  private User user = User.getInstance();
  
  public boolean setAmount(double Amount) {
    if (Amount <= 0) {
      logger.error("Amount must be greater than 0");
      return false;
    }
    this.amount = Amount;
    return true;
  }

  void setBiddable(boolean biddable) {
    this.biddable = biddable;
  }

  void setItem(Item item) {
    this.item = item;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Thread.sleep(1000);
        if (biddable) {
          BidItemRequestPayload payload = new BidItemRequestPayload(item.getId(), user.getUserName(), item.getCurrentPrice() + amount);
          PacketMessage mesage = new PacketMessage(Message.NEW_PRICE_REQUEST, payload);

          try{
              networkClient.sendPacket(mesage);
              this.biddable = false;
              logger.info("New price is sent.");
          } catch(IOException e){
              logger.error("ERROR: {}", e.getMessage());
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.info("INFO: Stop auto bidding.");
      }
    }
  }
}
