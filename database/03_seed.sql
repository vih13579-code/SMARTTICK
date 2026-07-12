USE Smarttick;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Customers WHERE customer_id = 1)
    SET IDENTITY_INSERT dbo.Customers ON;
IF NOT EXISTS (SELECT 1 FROM dbo.Customers WHERE customer_id = 1)
    INSERT dbo.Customers(customer_id, full_name, email) VALUES (1, N'Le The Vinh', 'vinhlt@smarttick.local');
IF EXISTS (SELECT 1 FROM dbo.Customers WHERE customer_id = 1)
    SET IDENTITY_INSERT dbo.Customers OFF;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Products)
BEGIN
    INSERT dbo.Products(product_name, unit_price, stock) VALUES
      (N'Dong ho Classic 40', 2490000, 8),
      (N'Dong ho Ocean Blue', 3190000, 5),
      (N'Dong ho Minimal Steel', 1890000, 12);
END
GO
