# Auction-App

## 1. Mô tả ngắn gọn và phạm vi hệ thống
Auction-App là một ứng dụng đấu giá phát triển bằng Java, gồm 3 module chính: `Commons`, `Server`, và `Client`.
Hệ thống cho phép nhiều client kết nối đến server, tạo và quản lý đấu giá, gửi/nhận bid, và hiển thị giao diện người dùng JavaFX cho client.

## 2. Công nghệ sử dụng và môi trường chạy
- Java 21 (maven compiler release 25)
- JavaFX 21
- Maven làm công cụ xây dựng
- Maven Wrapper có sẵn để chạy trên Windows, Linux, và macOS
- IDE gợi ý: IntelliJ IDEA, Eclipse, VS Code với Java extensions

## 3. Yêu cầu cài đặt
1. Cài đặt JDK 21 hoặc JDK tương thích với JavaFX 21.
2. Cài đặt Maven nếu không sử dụng Maven Wrapper.
3. Đảm bảo biến môi trường `JAVA_HOME` trỏ đến JDK hợp lệ.

## 4. Cấu trúc thư mục chính
```
Auction-App/
  ├─ Commons/      # Module chia sẻ các lớp, payload, packet, DTO chung
  ├─ Server/       # Module chứa logic server, xử lý kết nối và quản lý đấu giá
  ├─ Client/       # Module chứa giao diện JavaFX và client kết nối đến server
  ├─ pom.xml       # POM gốc quản lý modules và cấu hình chung
  ├─ mvnw          # Maven Wrapper cho Linux/macOS
  └─ mvnw.cmd      # Maven Wrapper cho Windows
```

## 5. Lệnh chạy chương trình
### 5.1 Chuẩn bị
Sử dụng một trong các lệnh sau, tùy hệ điều hành:
- Windows: `mvnw.cmd clean install`
- Linux/macOS: `./mvnw clean install`

Hoặc nếu dùng Maven cài sẵn:
- `mvn clean install`

### 5.2 Cách 1: Chạy bằng Maven Wrapper / compile rồi run
Dùng khi bạn muốn chạy trực tiếp từ mã nguồn mà không cần tạo fat jar.

#### Chạy Server
- Windows: `mvnw.cmd -pl Server exec:java`
- Linux/macOS: `./mvnw -pl Server exec:java`

#### Chạy Client GUI
- Windows: `mvnw.cmd -pl Client javafx:run`
- Linux/macOS: `./mvnw -pl Client javafx:run`

> Đây là cách đơn giản nhất để chạy, phù hợp khi đang phát triển hoặc kiểm tra nhanh.

### 5.3 Cách 2: Chạy bằng package / jar
Dùng khi bạn muốn đóng gói ứng dụng rồi chạy bằng file jar.

#### Build jar
- Windows: `mvnw.cmd -am -pl Server,Client clean package`
- Linux/macOS: `./mvnw -am -pl Server,Client clean package`

Hoặc build từng module riêng:
- Windows server: `mvnw.cmd -pl Server clean package`
- Windows client: `mvnw.cmd -pl Client -Djavafx.platform=win clean package`
- Linux/macOS server: `./mvnw -pl Server clean package`
- Linux/macOS client: `./mvnw -pl Client -Djavafx.platform=linux clean package`

Kết quả:
- `Server/target/server-1.0-SNAPSHOT.jar`
- `Server/target/server-1.0-SNAPSHOT-fat.jar`
- `Client/target/client-1.0-SNAPSHOT.jar`
- `Client/target/client-1.0-SNAPSHOT-fat.jar`
- `Commons/target/commons-1.0-SNAPSHOT.jar`

#### Chạy bằng jar
##### Server
- `java -jar Server/target/server-1.0-SNAPSHOT-fat.jar`

##### Client
- `java -jar Client/target/client-1.0-SNAPSHOT-fat.jar`

> Nếu bạn dùng fat jar cho client thì không cần cấu hình thêm module-path.
> Nếu không dùng fat jar, client vẫn cần JavaFX runtime:
> - Windows: `set PATH_TO_FX=C:\path\to\javafx-sdk-21\lib`
>   `java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar Client/target/client-1.0-SNAPSHOT.jar`
> - Linux/macOS: `export PATH_TO_FX=/path/to/javafx-sdk-21/lib`
>   `java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar Client/target/client-1.0-SNAPSHOT.jar`

### 5.4 Chạy Server/Client theo thứ tự
1. Chạy server trước:
   - `java -jar Server/target/server-1.0-SNAPSHOT-fat.jar`
2. Sau khi server đã khởi động, chạy client:
   - `java -jar Client/target/client-1.0-SNAPSHOT-fat.jar`

> Nếu bạn chạy client bằng jar không fat, vẫn hãy khởi tạo `PATH_TO_FX` và thêm `--module-path` / `--add-modules` như trên.

## 6. Hướng dẫn chạy Server/Client theo thứ tự
1. Chạy `Server` trước để mở máy chủ đấu giá và lắng nghe kết nối.
2. Sau khi server đã khởi động, chạy `Client` để kết nối và sử dụng giao diện JavaFX.
3. Khi mở client, do kết nối thông qua mạng lan, cần nhập IPv4 của server để có thể kết nối.
4. Có thể mở nhiều client để tham gia đấu giá từ các máy khác nhau hoặc nhiều cửa sổ.

## 7. Danh sách chức năng đã hoàn thành
- Khởi tạo server và lắng nghe kết nối từ client
- Xây dựng client JavaFX để người dùng tương tác
- Gửi/nhận dữ liệu qua mạng giữa Client và Server
- Quản lý các phiên đấu giá, đặt giá thầu và cập nhật trạng thái trong thời gian thực
- Tách module `Commons` dùng chung giữa Server và Client
- Hỗ trợ chạy bằng Maven Wrapper trên nhiều hệ điều hành

## 8. Link báo cáo PDF và video demo
- Báo cáo PDF: https://docs.google.com/document/d/1quT46TC4YDHrs6e-gsFyrI98bD6_vWwmcaxR7VNfjHw/edit?usp=sharing
- Video demo: 

