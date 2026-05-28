package app.controllers;


import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import org.slf4j.Logger;


import app.functions.User;
import app.functions.Item;
import app.functions.BidTransaction;
import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import app.payload.BidItemRespondPayload;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class BiddingController {
   private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
   @FXML
   private ImageView itemImage;
   @FXML
   LineChart<String, Number> priceChart;
   private XYChart.Series<String, Number> priceSeries;
   private Item currentItem;
   @FXML
   private Label updateCurrentPrice;
   @FXML
   private TextField placeBid;
   @FXML
   private Button submitBidButton;


   private MainWebController mainWebController;
   private NetworkClient networkClient;
   private User user;


   private int itemId;


   public int getItemId () {
       return currentItem.getId();
   }


   public void setItem(Item item) {
       this.currentItem = item;
       this.itemId = item.getId();


       priceSeries.setName("Bidding History");


       for (BidTransaction transaction : item.getHistory()) {
         String timeStamps = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
         priceSeries.getData().add(new XYChart.Data<>(timeStamps, transaction.getPrice()));
       }


       priceChart.getData().add(priceSeries);
       updateCurrentPrice.setText(String.format("Current highest bid: {}", item.getCurrentPrice()));
   }


   @FXML
   public void initialize() {
       mainWebController = MainWebController.getInstance();
       networkClient = NetworkClient.getInstance();
       user = User.getInstance();
       priceSeries = new XYChart.Series<>();


       networkClient.addUIListener(Message.NEW_PRICE_RESPOND, packet -> {
           BidItemRespondPayload respone = (BidItemRespondPayload) packet.getPayload();
           if(respone.isSuccess()) {
               try {
                   updateCurrentPrice.setText(String.format("Current highest price: %s", placeBid.getText()));
                   currentItem.addHistory(new BidTransaction(user.getUserName(), Double.parseDouble(placeBid.getText())));

                   Stage thisStage = (Stage) submitBidButton.getScene().getWindow();
                   thisStage.close();
               } catch (Exception e) {
                   logger.error("Error while bidding: ", e);
               }
           }
           else {
            logger.error("ERROR: Bidding failed. {}", respone.getError());
           }
       });


       networkClient.addUIListener(Message.NEW_PRICE_BROADCAST, packet -> {
         BidItemRequestPayload newPrice = (BidItemRequestPayload) packet.getPayload();


         if (newPrice.getId() == this.itemId) {
           String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
           double newBid = newPrice.getPrice();


           priceSeries.getData().add(new XYChart.Data<>(timeStamp, newBid));


           updateCurrentPrice.setText(String.format("Current highest price: %s", placeBid.getText()));
         }
       });
   }


   @FXML
   public void bidAction() {
       logger.info("New price is bidded");


       BidItemRequestPayload payload = new BidItemRequestPayload(this.getItemId(), user.getUserName(), Double.parseDouble(placeBid.getText()));
       PacketMessage mesage = new PacketMessage(Message.NEW_PRICE_REQUEST, payload);


       try{
           networkClient.sendPacket(mesage);
           logger.info("New price is sent.");
       } catch(IOException e){
           logger.error("ERROR: {}", e.getMessage());
       }
   }
}
