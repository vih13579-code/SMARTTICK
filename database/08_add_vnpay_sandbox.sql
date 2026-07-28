-- SMARTTICK VNPAY Sandbox payment migration for Microsoft SQL Server.
-- Back up the FWatch database before applying this migration.
USE [FWatch];
GO
SET XACT_ABORT ON;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH('dbo.Orders', 'PaymentMethod') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD PaymentMethod VARCHAR(30) NOT NULL
            CONSTRAINT DF_Orders_PaymentMethod DEFAULT 'cod';
    END;

    IF COL_LENGTH('dbo.Orders', 'PaymentStatus') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD PaymentStatus VARCHAR(30) NOT NULL
            CONSTRAINT DF_Orders_PaymentStatus DEFAULT 'PENDING';
    END;

    IF COL_LENGTH('dbo.Orders', 'DepositAmount') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD DepositAmount BIGINT NOT NULL
            CONSTRAINT DF_Orders_DepositAmount DEFAULT 0;
    END;

    IF COL_LENGTH('dbo.Orders', 'AmountDue') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD AmountDue BIGINT NOT NULL
            CONSTRAINT DF_Orders_AmountDue DEFAULT 0;
    END;

    IF COL_LENGTH('dbo.Orders', 'PaymentReference') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD PaymentReference VARCHAR(64) NULL;
    END;

    IF COL_LENGTH('dbo.Orders', 'PaidAt') IS NULL
    BEGIN
        ALTER TABLE dbo.Orders ADD PaidAt DATETIME NULL;
    END;

    IF OBJECT_ID('dbo.Payments', 'U') IS NULL
    BEGIN
        CREATE TABLE dbo.Payments (
            ID BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Payments PRIMARY KEY,
            OrderID INT NOT NULL,
            Provider VARCHAR(50) NOT NULL,
            TransactionRef VARCHAR(100) NOT NULL,
            VnpayTransactionNo VARCHAR(100) NULL,
            Amount BIGINT NOT NULL,
            [Status] VARCHAR(20) NOT NULL,
            ResponseCode VARCHAR(10) NULL,
            TransactionStatus VARCHAR(10) NULL,
            BankCode VARCHAR(50) NULL,
            BankTransactionNo VARCHAR(100) NULL,
            PayDate DATETIME NULL,
            RawResponse NVARCHAR(MAX) NULL,
            ExpiresAt DATETIME NOT NULL,
            CreatedAt DATETIME NOT NULL CONSTRAINT DF_Payments_CreatedAt DEFAULT GETDATE(),
            UpdatedAt DATETIME NOT NULL CONSTRAINT DF_Payments_UpdatedAt DEFAULT GETDATE(),
            CONSTRAINT UQ_Payments_TransactionRef UNIQUE (TransactionRef),
            CONSTRAINT FK_Payments_Orders FOREIGN KEY (OrderID) REFERENCES dbo.Orders(OrderID),
            CONSTRAINT CK_Payments_Amount CHECK (Amount > 0),
            CONSTRAINT CK_Payments_Status CHECK (
                [Status] IN ('PENDING','PAID','FAILED','CANCELLED','EXPIRED')
            )
        );
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE name = 'IX_Payments_Order_Status'
          AND object_id = OBJECT_ID('dbo.Payments')
    )
    BEGIN
        CREATE INDEX IX_Payments_Order_Status
            ON dbo.Payments(OrderID, [Status], CreatedAt DESC);
    END;

    EXEC sys.sp_executesql N'
        UPDATE dbo.Orders
        SET PaymentStatus = UPPER(PaymentStatus)
        WHERE PaymentStatus IS NOT NULL;
    ';

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
