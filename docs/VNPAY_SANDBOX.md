# SMARTTICK VNPAY Sandbox

This integration is for VNPAY Sandbox only. `VnpayConfig` rejects a payment
URL whose host is not `sandbox.vnpayment.vn`.

## Existing architecture

- Maven WAR, Java 8, Tomcat Servlet API 4.0.1.
- Servlet/JSP MVC with `@WebServlet` and `@WebFilter`.
- JDBC with Microsoft SQL Server through `DB.DBContext`.
- Gson is already available for JSON.
- Orders are stored in `dbo.Orders`; order creation is handled by
  `Controllers.BuyProductsServlet` and `DAOs.OrderDAO`.
- Central HTML error pages already exist in `WEB-INF/web.xml`.

No new Maven dependency is required. HMAC SHA512 uses `javax.crypto.Mac`.

## Changed tree

```text
Smarttick/
|-- .gitignore
|-- vnpay.properties.example
|-- database/
|   |-- 02_schema.sql
|   `-- 08_add_vnpay_sandbox.sql
|-- docs/
|   |-- VNPAY_SANDBOX.md
|   `-- postman/
|       `-- SMARTTICK-VNPAY-Sandbox.postman_collection.json
`-- src/main/
    |-- java/
    |   |-- Configs/
    |   |   `-- VnpayConfig.java
    |   |-- Controllers/
    |   |   |-- BuyProductsServlet.java
    |   |   |-- OrderPaymentStatusServlet.java
    |   |   |-- VnpayCreatePaymentServlet.java
    |   |   |-- VnpayIpnServlet.java
    |   |   `-- VnpayReturnServlet.java
    |   |-- DAOs/
    |   |   |-- OrderDAO.java
    |   |   `-- PaymentDAO.java
    |   |-- Models/
    |   |   `-- Payment.java
    |   `-- Utils/
    |       `-- VnpayUtil.java
    |-- resources/
    |   `-- vnpay.properties
    `-- webapp/
        |-- ConfirmView.jsp
        |-- ProductDetailView.jsp
        |-- VnpayResultView.jsp
        `-- assets/css/smarttick.css
```

`src/main/resources/vnpay.properties` is intentionally ignored by Git.
Commit only `vnpay.properties.example`.

## Database setup

For an existing development database, run:

```text
database/08_add_vnpay_sandbox.sql
```

The migration adds `Orders.PaidAt`, creates `dbo.Payments`, adds a unique
constraint for `TransactionRef`, and adds an order/status index. The script is
idempotent for an installation that does not yet have the payment table.

`Amount` is stored as `BIGINT` because this project stores VND as whole numbers.
The value sent to VNPAY is always `Amount * 100`.

## Local configuration

Copy `vnpay.properties.example` to:

```text
src/main/resources/vnpay.properties
```

Fill in the Sandbox TmnCode and HashSecret. Never commit that file and never
put the HashSecret in JSP, JavaScript, logs, Postman collections, or tickets.

For browser-only local testing, Return URL may use localhost. A real IPN cannot
reach localhost, so complete the HTTPS tunnel setup below.

For the local merchant registered as `demo-vnpay-test.com`, use callback URLs
with that exact host and map it to `127.0.0.1` in the Windows hosts file. Leave
`vnpay.bankCode` blank during browser testing so VNPAY shows the payment-method
chooser. From there, choose the QR payment method. Only set `vnpay.bankCode` to
`VNPAYQR` when the Sandbox merchant explicitly supports that direct method.

## HTTPS tunnel and Tomcat

1. Start SQL Server and apply `database/08_add_vnpay_sandbox.sql`.
2. Build with `mvn clean package`.
3. Deploy `target/SMARTTICK.war` to Tomcat and start Tomcat on port 8080.
4. Start ngrok:

```powershell
ngrok http 8080
```

5. Copy the HTTPS forwarding host, for example
   `https://example.ngrok-free.app`.
6. Set these local properties and restart Tomcat:

```properties
vnpay.returnUrl=https://example.ngrok-free.app/SMARTTICK/api/payments/vnpay/return
vnpay.ipnUrl=https://example.ngrok-free.app/SMARTTICK/api/payments/vnpay/ipn
```

7. Send/register the same HTTPS IPN URL with the VNPAY Sandbox merchant
   configuration. VNPAY 2.1.0 sends `vnp_ReturnUrl` in the payment URL, while
   the IPN URL is configured with VNPAY and is not a payment request parameter.
8. Keep ngrok and Tomcat running for the complete payment and callback flow.

The free ngrok hostname can change after restart. Update both properties and
the VNPAY Sandbox merchant configuration whenever it changes.

## Runtime flow

```text
ConfirmView.jsp
  -> POST /order (creates Orders row with PaymentStatus=PENDING)
  -> POST /api/payments/vnpay/create (reads amount from SQL Server)
  -> VNPAY Sandbox payment-method chooser / QR page
  -> GET /api/payments/vnpay/return (signature check and display only)
  -> GET /api/payments/vnpay/ipn (signature, amount and idempotency checks)
  -> Payments + Orders updated in one JDBC transaction
  -> GET /api/payments/order/status (result page reads database state)
```

For orders with six or more items, the existing checkout policy charges the
30 percent `DepositAmount`. Other orders charge `TotalAmount`. Both Create and
IPN read and compare the same database values.

## API requests

Create a payment URL. This request requires the customer's `JSESSIONID` and
the order must belong to that customer:

```http
POST /SMARTTICK/api/payments/vnpay/create
Content-Type: application/x-www-form-urlencoded

orderId=123
```

JSON is also accepted:

```json
{"orderId":123}
```

Read the real payment status:

```http
GET /SMARTTICK/api/payments/order/status?orderId=123
Cookie: JSESSIONID=...
```

Return and IPN endpoints are called by VNPAY:

```http
GET /SMARTTICK/api/payments/vnpay/return?...signed VNPAY fields...
GET /SMARTTICK/api/payments/vnpay/ipn?...signed VNPAY fields...
```

Do not manually construct callback signatures. Capture a genuine Sandbox
callback when testing replay behavior.

## Test cases

1. Success: select VNPAY QR, complete the Sandbox flow, verify `Payments.Status`
   and `Orders.PaymentStatus` are `PAID`, with transaction number and `PaidAt`.
2. Cancel: cancel on the Sandbox page; code `24` is persisted as `CANCELLED`.
3. Failure: use a Sandbox-declined scenario; the payment becomes `FAILED`.
4. Expired: leave the payment page beyond `vnpay.expireMinutes`; a returned
   expiry code is stored as `EXPIRED`.
5. Duplicate IPN: replay the exact valid IPN URL. The first call returns
   `RspCode=00`; a later call returns `RspCode=02` and does not update again.
6. Invalid signature: modify one field in a captured callback. IPN returns
   `RspCode=97` and leaves both tables unchanged.
7. Invalid amount: a validly signed callback with an amount different from the
   database returns `RspCode=04`.
8. Paid order retry: call Create again for a paid order. The API returns HTTP
   409 and does not create another payment.
9. Ownership: call Create or Status with another customer's order ID. The
   endpoint returns not found and does not expose payment data.

## Security behavior

- The browser never sends or chooses the amount.
- TmnCode and HashSecret are loaded through `ServletContext` from
  `/WEB-INF/classes/vnpay.properties`.
- All signing and verification uses the shared `Utils.VnpayUtil`.
- Return URL never mutates payment or order state.
- IPN locks the payment and order rows and accepts only a `PENDING` payment.
- `PAID` requires both `vnp_ResponseCode=00` and
  `vnp_TransactionStatus=00`.
- Callback amount must equal both `Payments.Amount` and the payable order
  amount after division by 100.
- The secret is never included in application logs.

Official references:

- https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
- https://sandbox.vnpayment.vn/apis/docs/chuyen-doi-thuat-toan/changeTypeHash.html
- https://sandbox.vnpayment.vn/apis/docs/faqs/
