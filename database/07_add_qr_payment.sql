-- Adds QR payment and deposit fields for checkout.
USE [FWatch];
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Orders') AND name = 'PaymentMethod')
BEGIN
    ALTER TABLE dbo.Orders ADD PaymentMethod VARCHAR(30) NOT NULL
        CONSTRAINT DF_Orders_PaymentMethod DEFAULT 'cod';
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Orders') AND name = 'PaymentStatus')
BEGIN
    ALTER TABLE dbo.Orders ADD PaymentStatus VARCHAR(30) NOT NULL
        CONSTRAINT DF_Orders_PaymentStatus DEFAULT 'pending';
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Orders') AND name = 'DepositAmount')
BEGIN
    ALTER TABLE dbo.Orders ADD DepositAmount BIGINT NOT NULL
        CONSTRAINT DF_Orders_DepositAmount DEFAULT 0;
    ALTER TABLE dbo.Orders ADD CONSTRAINT CK_Orders_DepositAmount CHECK (DepositAmount >= 0);
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Orders') AND name = 'AmountDue')
BEGIN
    ALTER TABLE dbo.Orders ADD AmountDue BIGINT NOT NULL
        CONSTRAINT DF_Orders_AmountDue DEFAULT 0;
    ALTER TABLE dbo.Orders ADD CONSTRAINT CK_Orders_AmountDue CHECK (AmountDue >= 0);
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Orders') AND name = 'PaymentReference')
BEGIN
    ALTER TABLE dbo.Orders ADD PaymentReference VARCHAR(64) NULL;
END;
GO
