USE FWatch;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

INSERT INTO dbo.CustomerVoucher (CustomerID, VoucherID, ExpirationDate, Quantity)
SELECT
    customer.CustomerID,
    voucher.VoucherID,
    NULL,
    1
FROM dbo.Customers AS customer
CROSS JOIN dbo.Vouchers AS voucher
WHERE customer.IsBlock = 0
  AND customer.IsDeleted = 0
  AND voucher.[Status] = 1
  AND voucher.EndDate >= GETDATE()
  AND (voucher.MaxUsedCount = 0 OR voucher.UsedCount < voucher.MaxUsedCount)
  AND NOT EXISTS (
      SELECT 1
      FROM dbo.CustomerVoucher AS customerVoucher
      WHERE customerVoucher.CustomerID = customer.CustomerID
        AND customerVoucher.VoucherID = voucher.VoucherID
  );

COMMIT TRANSACTION;
GO
