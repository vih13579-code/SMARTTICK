# SMARTTICK – Watch Store Management System

SMARTTICK là phiên bản được refactor trực tiếp từ dự án FShop, giữ nguyên nền tảng **Java Servlet/JSP + Maven WAR + JDBC + SQL Server + Tomcat 9**. Catalog, dữ liệu mẫu, dashboard và giao diện đã được chuyển sang cửa hàng bán đồng hồ.

## 1. Công nghệ và môi trường

- JDK 17
- Maven 3.8+
- Apache Tomcat 9.x
- Microsoft SQL Server 2019/2022 hoặc tương thích
- SQL Server Management Studio (SSMS)
- Servlet API `javax.servlet` 4.0.1
- JSP/JSTL 1.2

> Không deploy dự án này trên Tomcat 10 vì Tomcat 10 dùng namespace `jakarta.servlet`, trong khi source SMARTTICK dùng `javax.servlet`.

## 2. Cấu trúc chính

```text
SMARTTICK/
├── database/
│   ├── 01_create_database.sql
│   ├── 02_schema.sql
│   ├── 03_watch_seed.sql
│   └── 04_reset_watch_seed.sql
├── src/main/java/
│   ├── Controllers/
│   ├── DAOs/
│   ├── DB/
│   ├── Filters/
│   ├── Models/
│   └── Utils/
├── src/main/resources/
│   ├── db.properties
│   └── db.properties.example
├── src/main/webapp/
├── pom.xml
├── CHANGELOG.md
├── FILES_CHANGED.md
└── BUILD_REPORT.md
```

## 3. Tạo database FWatch bằng SSMS

### 3.1 Bật kết nối SQL Server

1. Mở **SQL Server Configuration Manager**.
2. Vào `SQL Server Network Configuration` → `Protocols for MSSQLSERVER`.
3. Bật `TCP/IP`.
4. Trong `TCP/IP Properties` → `IP Addresses`, đặt `TCP Port` là `1433` nếu máy chưa có port cố định.
5. Khởi động lại dịch vụ SQL Server.
6. Trong SSMS, bật chế độ **SQL Server and Windows Authentication mode** nếu dùng tài khoản `sa`.
7. Đảm bảo tài khoản kết nối có quyền tạo database và bảng.

### 3.2 Chạy script theo đúng thứ tự

Mở SSMS và chạy lần lượt:

```text
database/01_create_database.sql
database/02_schema.sql
database/03_watch_seed.sql
```

- `01_create_database.sql`: tạo database `FWatch` nếu chưa tồn tại.
- `02_schema.sql`: tạo lại toàn bộ schema. Script này phục vụ môi trường development và có thể xóa các bảng FWatch hiện có.
- `03_watch_seed.sql`: làm sạch dữ liệu development rồi tạo tài khoản, catalog, 16 sản phẩm đồng hồ, thuộc tính, tồn kho, voucher và đơn hàng mẫu.
- `04_reset_watch_seed.sql`: dùng khi muốn xóa và seed lại dữ liệu mẫu mà không tạo lại schema.

> Hãy sao lưu dữ liệu trước khi chạy `02_schema.sql`, `03_watch_seed.sql` hoặc `04_reset_watch_seed.sql` trên database có dữ liệu thật.

## 4. Cấu hình kết nối database

Mở file:

```text
src/main/resources/db.properties
```

Cập nhật:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=FWatch;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=your_password
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
upload.product.path=uploads/products
```

Có thể copy `db.properties.example` thành `db.properties`. Không commit mật khẩu SQL Server thật lên Git.

Ứng dụng cũng hỗ trợ biến môi trường:

```text
SMARTTICK_DB_URL
SMARTTICK_DB_USERNAME
SMARTTICK_DB_PASSWORD
SMARTTICK_UPLOAD_PATH
```

Biến môi trường được ưu tiên hơn giá trị trong `db.properties`.

## 5. Build dự án

Tại thư mục chứa `pom.xml`, chạy:

```bash
mvn clean package
```

Kết quả mong đợi:

```text
target/SMARTTICK.war
```

Nếu Maven báo không tải được dependency, kiểm tra mạng, proxy Maven và file `settings.xml`.

## 6. Deploy trên Tomcat 9

1. Dừng Tomcat.
2. Copy `target/SMARTTICK.war` vào:

```text
<TOMCAT_HOME>/webapps/
```

3. Khởi động Tomcat.
4. Truy cập:

```text
http://localhost:8080/SMARTTICK/
```

Các route chính:

```text
/SMARTTICK/                    Trang chủ
/SMARTTICK/Watches             Danh sách và bộ lọc đồng hồ
/SMARTTICK/customerLogin       Đăng nhập khách hàng
/SMARTTICK/EmployeeLogin       Đăng nhập quản trị
/SMARTTICK/customer/dashboard  Customer Dashboard
/SMARTTICK/admin/dashboard     Admin Dashboard
```

## 7. Tài khoản test

### Admin

```text
Email: admin@fwatch.vn
Password: Admin@123
```

### Customer

```text
Email: customer@fwatch.vn
Password: Customer@123
```

### Các tài khoản nhân viên bổ sung

```text
manager@fwatch.vn
orders@fwatch.vn
warehouse@fwatch.vn
Password chung: Staff@123
```

Source cũ sử dụng MD5 cho luồng đăng nhập, vì vậy seed hiện giữ cơ chế đó để tương thích. Với môi trường production, cần migrate sang BCrypt/Argon2 và buộc người dùng đổi mật khẩu.

## 8. Upload ảnh sản phẩm

Admin có thể upload tối đa 4 ảnh cho mỗi sản phẩm:

- JPG/JPEG
- PNG
- WEBP
- Tối đa 5 MB mỗi ảnh

Ứng dụng kiểm tra extension, MIME khai báo và chữ ký nhị phân của file. Tên file được tạo bằng UUID; không dùng filename do người dùng cung cấp.

Nếu `upload.product.path` là đường dẫn tương đối, ảnh được lưu mặc định tại:

```text
<USER_HOME>/SMARTTICK/uploads/products
```

Có thể đặt đường dẫn tuyệt đối bằng biến `SMARTTICK_UPLOAD_PATH`. Tài khoản chạy Tomcat phải có quyền đọc/ghi thư mục này. Ảnh seed nội bộ nằm tại:

```text
src/main/webapp/assets/imgs/Products/watches
```

## 9. Cấu hình email và Google OAuth

Không có SMTP password hoặc Google client secret thật trong source. Có thể cấu hình email trong `src/main/resources/email.properties` hoặc dùng biến môi trường:

```text
SMARTTICK_SMTP_HOST
SMARTTICK_SMTP_PORT
SMARTTICK_SMTP_AUTH
SMARTTICK_SMTP_STARTTLS
SMARTTICK_SMTP_USERNAME
SMARTTICK_SMTP_PASSWORD
SMARTTICK_SMTP_FROM
```

Google OAuth dùng các biến:

```text
SMARTTICK_GOOGLE_CLIENT_ID
SMARTTICK_GOOGLE_CLIENT_SECRET
SMARTTICK_GOOGLE_REDIRECT_URI
```

Callback mặc định là `http://localhost:8080/SMARTTICK/GoogleLogin`. Khi chưa cấu hình credential thật, các luồng gửi OTP/email và Google Login sẽ báo lỗi cấu hình thay vì dùng khóa hard-code.

## 10. Chức năng chính

### Khách hàng

- Xem trang chủ và catalog đồng hồ.
- Tìm theo tên hoặc model.
- Lọc theo danh mục, thương hiệu và khoảng giá.
- Sắp xếp theo giá hoặc tên.
- Xem chi tiết, thông số kỹ thuật và đánh giá.
- Giỏ hàng, đặt hàng, voucher, địa chỉ và lịch sử đơn hàng.
- Customer Dashboard: thống kê đơn hàng, đơn gần đây, sản phẩm đã mua, voucher, địa chỉ và sản phẩm gợi ý.

### Admin / Shop Manager

- Dashboard tổng quan sản phẩm, tồn kho, khách hàng, đơn hàng và doanh thu hoàn tất.
- CRUD sản phẩm, soft delete/khôi phục, cập nhật stock và upload ảnh.
- CRUD danh mục và thương hiệu, chặn xóa khi đang được sản phẩm sử dụng.
- Quản lý khách hàng, đơn hàng, voucher, nhà cung cấp, nhập kho, feedback và thống kê.
- Filter phân quyền bảo vệ các route quản trị sản phẩm và `/admin/*`.

## 11. Kiểm tra nhanh sau khi deploy

1. Mở trang chủ và xác nhận hiển thị sản phẩm đồng hồ.
2. Lọc theo Casio/Seiko và khoảng giá.
3. Đăng nhập Admin → vào `/admin/dashboard`.
4. Tạo sản phẩm mới với ảnh chính.
5. Cập nhật sản phẩm mà không chọn ảnh mới để kiểm tra giữ ảnh cũ.
6. Soft delete và khôi phục sản phẩm.
7. Đăng nhập Customer → vào `/customer/dashboard`.
8. Thêm sản phẩm vào giỏ, đặt hàng và kiểm tra lịch sử đơn.
9. Dùng URL admin khi đăng nhập Customer để xác nhận bị từ chối.

## 12. Lưu ý triển khai

- Không dùng `db.properties` chứa mật khẩu thật trong repository công khai.
- Google OAuth callback mặc định là `http://localhost:8080/SMARTTICK/GoogleLogin`; cần đổi khi deploy domain khác.
- Email/Google OAuth cần khóa API riêng của môi trường triển khai.
- Script seed/reset dành cho development, không chạy trực tiếp trên production.
- Tham khảo `BUILD_REPORT.md` để xem phạm vi kiểm thử đã thực hiện trong gói bàn giao này.
