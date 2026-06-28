# SMARTTICK Skeleton

Đây là bản **project rỗng/Skeleton** được dựng lại từ cấu trúc thư mục của project `Smarttick.zip`.
Mục đích: để từng thành viên trong nhóm nhận chức năng nào thì tự thêm code vào đúng package/module đó, tránh sửa lẫn lộn.

## Công nghệ giữ theo project gốc

- Java Web Maven WAR
- Servlet/JSP dùng `javax.servlet` phù hợp Tomcat 9.x
- JSP/JSTL
- SQL Server JDBC
- Cấu trúc MVC cơ bản: `Controller -> Service -> DAO -> DB -> Model -> JSP`

## Cấu trúc chính

```text
SMARTTICK_Skeleton/
├── database/                  # Script database rỗng, từng thành viên tự bổ sung bảng/seed
├── image/                     # Chỗ để ảnh upload/test, hiện đang rỗng
├── src/main/java/
│   ├── Controllers/           # Servlet placeholder theo tên chức năng cũ
│   ├── Services/              # Business logic, thêm mới để tách logic khỏi Servlet
│   ├── DAOs/                  # Truy vấn database
│   ├── DB/                    # DBContext
│   ├── Filters/               # Authorization/encoding/session filter
│   ├── Models/                # Entity/DTO
│   └── Utils/                 # Helper dùng chung
├── src/main/resources/        # db.properties.example, email.properties.example
├── src/main/webapp/           # JSP placeholder + assets
│   ├── assets/css/
│   ├── assets/js/
│   ├── errors/
│   └── WEB-INF/web.xml
├── pom.xml
└── README.md
```

## Cách chạy

1. Mở project bằng NetBeans/IntelliJ.
2. Copy file cấu hình:
   - `src/main/resources/db.properties.example` -> `src/main/resources/db.properties`
   - `src/main/resources/email.properties.example` -> `src/main/resources/email.properties` nếu dùng OTP/mail.
3. Sửa thông tin SQL Server trong `db.properties`.
4. Chạy script trong thư mục `database/` nếu cần tạo DB.
5. Build project:

```bash
mvn clean package
```

6. Deploy file WAR trong `target/SMARTTICK-SKELETON.war` lên Tomcat 9.x.

## Quy ước chia việc cho nhóm

| Nhóm chức năng | Controller/View/DAO nên làm việc |
|---|---|
| Auth/Login/Register/OTP | `CustomerLoginServlet`, `EmployeeLoginServlet`, `RegisterServlet`, `RegisterOTPServlet`, `VerifyOTPServlet`, `ResetPasswordServlet`, `CustomerDAO`, `EmployeeDAO`, `EmailService` |
| Customer/Profile/Address | `CustomerDashboardServlet`, `ViewCustomerProfileServlet`, `UpdateCustomerProfileServlet`, `AddressDAO`, `CustomerDAO` |
| Product/Catalog/Search | `ProductListServlet`, `ProductDetailServlet`, `CreateProductServlet`, `UpdateProductServlet`, `DeleteProductServlet`, `ProductDAO`, `BrandDAO`, `CategoryDAO` |
| Cart/Checkout/Order | `AddToCartServlet`, `ViewCartServlet`, `UpdateCartServlet`, `BuyProductsServlet`, `ViewOrderHistoryServlet`, `OrderDAO`, `OrderDetailDAO`, `CartDAO` |
| Voucher | `CreateVoucherServlet`, `UpdateVoucherServlet`, `DeleteVoucherServlet`, `ViewVoucherListServlet`, `VoucherDAO`, `CustomerVoucherDAO` |
| Supplier/Import/Inventory | `CreateSupplierServlet`, `ViewSupplierServlet`, `ImportStockServlet`, `ViewWarehouseServlet`, `SupplierDAO`, `ImportOrderDAO`, `StockDAO` |
| Feedback/Comment | `CommentServlet`, `ReplyFeedbackServlet`, `ViewFeedbackForManagerServlet`, `ProductRatingDAO`, `RatingRepliesDAO` |
| Statistic/Report | `RevenueStatisticServlet`, `ProductStatisticServlet`, `InventoryStatisticServlet`, `StatisticDAO` |

## Quy tắc code để tránh conflict

- Không viết SQL trực tiếp trong JSP.
- Controller chỉ xử lý request/response, không viết business logic dài.
- Business logic để trong `Services/`.
- Query database để trong `DAOs/`.
- Mỗi thành viên tạo branch riêng theo dạng: `feature/product-management`, `feature/cart-order`, `feature/auth`.
- Khi thêm servlet mới phải cập nhật `src/main/webapp/WEB-INF/web.xml` hoặc dùng annotation `@WebServlet` thống nhất với nhóm.
- Không commit file `target/`, `.class`, `db.properties`, `email.properties` thật.

## Lưu ý

- Toàn bộ code xử lý thật đã được thay bằng placeholder/TODO.
- Thư mục `target/` và ảnh sản phẩm nặng đã bị loại bỏ để project nhẹ và sạch.
- Các file JSP hiện chỉ là trang mẫu, thành viên tự thay bằng giao diện thật.
