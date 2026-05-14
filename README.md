# Auction-App
A big project.

To run project using terminal: 
- Run Server:
    + On Linux: mvn clean compile exec:java -Dexec.mainClass="app.ServerMain"
    + On Windows: mvn clean compile exec:java "-Dexec.mainClass=app.ServerMain"
- Run Client GUI: mvn clean javafx:run -Djavafx.args="8080 1IPv4"  # <--- replace IPv4 with actual PC 1 IP
