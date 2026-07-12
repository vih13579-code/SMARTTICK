USE Smarttick;
GO
DELETE FROM dbo.OrderDetails;
DELETE FROM dbo.Orders;
DELETE FROM dbo.Carts;
DELETE FROM dbo.Products;
DELETE FROM dbo.Customers;
DBCC CHECKIDENT ('dbo.Orders', RESEED, 1000);
DBCC CHECKIDENT ('dbo.Products', RESEED, 0);
DBCC CHECKIDENT ('dbo.Customers', RESEED, 0);
GO
:r .\03_seed.sql
