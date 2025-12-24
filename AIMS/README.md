# AIMS-Ver2025

## Yêu cầu
- JDK >= 17
- Maven >= 3.9.5
- SQLite >= 3.44.0
- Spring Boot >= 3.1.5

## Hướng dẫn chạy code

### Clone repository về máy local
```bash 
git clone https://github.com/trangntt-for-student/AIMS-Ver2025.git
```
Chuyển sang thư mục của project
```bash
cd AIMS-Ver2025
```

### Build dự án bằng Maven
```bash
mvn clean install
```

Hoặc có thể bỏ qua bước này nếu sử dụng IDE như IntelliJ IDEA hoặc Eclipse để tự động build project.

### Tạo file cấu hình application.properties

Tạo file `application.properties` trong thư mục `src/main/resources/`.

Copy nội dung của file `application.properties.example` và dán vào file `application.properties`. Chỉnh sửa các thông tin cấu hình theo nhu cầu của bạn.

Truy cập trang: https://developer.paypal.com/dashboard/ để lấy các thông tin cần thiết cho cấu hình PayPal.
- Bước 1: Tạo tài khoản sandbox (nếu chưa có).
- Bước 2: Tạo ứng dụng mới trong phần Apps & Credentials để lấy Client ID và Secret.

### Chạy ứng dụng
Sử dụng Maven để chạy ứng dụng:
```bash
mvn spring-boot:run
```
Hoặc chạy trực tiếp từ IDE bằng cách chạy class `com.hust.soict.aims.App.java`.
