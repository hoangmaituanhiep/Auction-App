# Auction-App
A big project.

To run project using terminal: 
<<<<<<< HEAD
- Run Server: mvn exec:java -pl Server
- Run Client GUI: mvn javafx:run -pl Client
=======
- Run Server:
    + On Linux: mvn clean compile exec:java -Dexec.mainClass="app.ServerMain"
    + On Windows: mvn clean compile exec:java "-Dexec.mainClass=app.ServerMain"
- Run Client GUI: mvn clean javafx:run -Djavafx.args="8080 1IPv4"  # <--- replace IPv4 with actual PC 1 IP
>>>>>>> ffb890dbcf0941557e28f224e3990d8d650d85da
