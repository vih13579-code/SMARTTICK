-- Author: CE181159-Nguyen Le Duy Minh
-- Since: 2026-06-29
-- Smarttick - 02_schema.sql
-- Recreates the development schema. Back up important data before running.
USE [Smarttick];
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    DROP TABLE IF EXISTS dbo.RatingReplies;
    DROP TABLE IF EXISTS dbo.ProductRatings;
    DROP TABLE IF EXISTS dbo.Carts;
    DROP TABLE IF EXISTS dbo.OrderDetails;
    DROP TABLE IF EXISTS dbo.Orders;
    DROP TABLE IF EXISTS dbo.CustomerVoucher;
    DROP TABLE IF EXISTS dbo.Addresses;
    DROP TABLE IF EXISTS dbo.ImportStockDetails;
    DROP TABLE IF EXISTS dbo.ImportStocks;
    DROP TABLE IF EXISTS dbo.AttributeDetails;
    DROP TABLE IF EXISTS dbo.Attributes;
    DROP TABLE IF EXISTS dbo.Products;
    DROP TABLE IF EXISTS dbo.Suppliers;
    DROP TABLE IF EXISTS dbo.Vouchers;
    DROP TABLE IF EXISTS dbo.OrderStatus;
    DROP TABLE IF EXISTS dbo.Customers;
    DROP TABLE IF EXISTS dbo.Employees;
    DROP TABLE IF EXISTS dbo.Roles;
    DROP TABLE IF EXISTS dbo.Brands;
    DROP TABLE IF EXISTS dbo.Categories;

    CREATE TABLE dbo.Roles (
        RoleID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Roles PRIMARY KEY,
        Name NVARCHAR(50) NOT NULL CONSTRAINT UQ_Roles_Name UNIQUE
    );

    CREATE TABLE dbo.Employees (
        EmployeeID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Employees PRIMARY KEY,
        FullName NVARCHAR(255) NOT NULL,
        Birthday DATE NULL,
        [Password] VARCHAR(64) NOT NULL,
        PhoneNumber VARCHAR(15) NULL,
        Email VARCHAR(254) NOT NULL CONSTRAINT UQ_Employees_Email UNIQUE,
        Gender NVARCHAR(10) NULL,
        CreatedDate DATE NOT NULL CONSTRAINT DF_Employees_CreatedDate DEFAULT CAST(GETDATE() AS DATE),
        [Status] BIT NOT NULL CONSTRAINT DF_Employees_Status DEFAULT 1,
        Avatar NVARCHAR(255) NULL,
        RoleID INT NOT NULL,
        CONSTRAINT FK_Employees_Roles FOREIGN KEY (RoleID) REFERENCES dbo.Roles(RoleID)
    );

    CREATE TABLE dbo.Customers (
        CustomerID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Customers PRIMARY KEY,
        FullName NVARCHAR(255) NOT NULL,
        Birthday DATE NULL,
        [Password] VARCHAR(64) NOT NULL,
        PhoneNumber VARCHAR(15) NULL,
        Email VARCHAR(254) NOT NULL CONSTRAINT UQ_Customers_Email UNIQUE,
        Gender NVARCHAR(10) NULL,
        CreatedDate DATETIME NOT NULL CONSTRAINT DF_Customers_CreatedDate DEFAULT GETDATE(),
        GoogleID VARCHAR(254) NULL,
        IsBlock BIT NOT NULL CONSTRAINT DF_Customers_IsBlock DEFAULT 0,
        IsDeleted BIT NOT NULL CONSTRAINT DF_Customers_IsDeleted DEFAULT 0,
        Avatar NVARCHAR(255) NULL
    );

    CREATE TABLE dbo.Addresses (
        AddressID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Addresses PRIMARY KEY,
        CustomerID INT NOT NULL,
        AddressDetails NVARCHAR(500) NOT NULL,
        IsDefault BIT NOT NULL CONSTRAINT DF_Addresses_IsDefault DEFAULT 0,
        CONSTRAINT FK_Addresses_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers(CustomerID)
    );

    CREATE TABLE dbo.Suppliers (
        SupplierID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Suppliers PRIMARY KEY,
        TaxID VARCHAR(20) NOT NULL CONSTRAINT UQ_Suppliers_TaxID UNIQUE,
        Name NVARCHAR(255) NOT NULL,
        Email VARCHAR(254) NULL,
        PhoneNumber VARCHAR(15) NULL,
        Address NVARCHAR(255) NULL,
        CreatedDate DATETIME NOT NULL CONSTRAINT DF_Suppliers_CreatedDate DEFAULT GETDATE(),
        LastModify DATETIME NOT NULL CONSTRAINT DF_Suppliers_LastModify DEFAULT GETDATE(),
        IsDeleted BIT NOT NULL CONSTRAINT DF_Suppliers_IsDeleted DEFAULT 0,
        IsActivate BIT NOT NULL CONSTRAINT DF_Suppliers_IsActivate DEFAULT 1
    );

    CREATE TABLE dbo.Categories (
        CategoryID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Categories PRIMARY KEY,
        Name NVARCHAR(50) NOT NULL CONSTRAINT UQ_Categories_Name UNIQUE
    );

    CREATE TABLE dbo.Brands (
        BrandID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Brands PRIMARY KEY,
        Name NVARCHAR(50) NOT NULL CONSTRAINT UQ_Brands_Name UNIQUE
    );

    CREATE TABLE dbo.Products (
        ProductID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Products PRIMARY KEY,
        BrandID INT NOT NULL,
        CategoryID INT NOT NULL,
        Model NVARCHAR(100) NOT NULL CONSTRAINT UQ_Products_Model UNIQUE,
        FullName NVARCHAR(255) NOT NULL CONSTRAINT UQ_Products_FullName UNIQUE,
        Description NVARCHAR(MAX) NOT NULL,
        IsDeleted BIT NOT NULL CONSTRAINT DF_Products_IsDeleted DEFAULT 0,
        Price BIGINT NOT NULL,
        Image NVARCHAR(255) NOT NULL,
        Image1 NVARCHAR(255) NULL,
        Image2 NVARCHAR(255) NULL,
        Image3 NVARCHAR(255) NULL,
        Quantity INT NOT NULL CONSTRAINT DF_Products_Quantity DEFAULT 0,
        Stock INT NOT NULL CONSTRAINT DF_Products_Stock DEFAULT 0,
        CONSTRAINT CK_Products_Price CHECK (Price > 0),
        CONSTRAINT CK_Products_Quantity CHECK (Quantity >= 0),
        CONSTRAINT CK_Products_Stock CHECK (Stock >= 0),
        CONSTRAINT FK_Products_Brands FOREIGN KEY (BrandID) REFERENCES dbo.Brands(BrandID),
        CONSTRAINT FK_Products_Categories FOREIGN KEY (CategoryID) REFERENCES dbo.Categories(CategoryID)
    );

    CREATE TABLE dbo.Attributes (
        AttributeID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Attributes PRIMARY KEY,
        CategoryID INT NOT NULL,
        Name NVARCHAR(100) NOT NULL,
        CONSTRAINT UQ_Attributes_Category_Name UNIQUE (CategoryID, Name),
        CONSTRAINT FK_Attributes_Categories FOREIGN KEY (CategoryID) REFERENCES dbo.Categories(CategoryID)
    );

    CREATE TABLE dbo.AttributeDetails (
        AttributeID INT NOT NULL,
        ProductID INT NOT NULL,
        AttributeInfor NVARCHAR(255) NULL,
        CONSTRAINT PK_AttributeDetails PRIMARY KEY (AttributeID, ProductID),
        CONSTRAINT FK_AttributeDetails_Attributes FOREIGN KEY (AttributeID) REFERENCES dbo.Attributes(AttributeID),
        CONSTRAINT FK_AttributeDetails_Products FOREIGN KEY (ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
    );

    CREATE TABLE dbo.Vouchers (
        VoucherID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Vouchers PRIMARY KEY,
        VoucherCode VARCHAR(20) NOT NULL CONSTRAINT UQ_Vouchers_Code UNIQUE,
        VoucherValue INT NOT NULL,
        VoucherType INT NOT NULL,
        StartDate DATETIME NOT NULL,
        EndDate DATETIME NOT NULL,
        UsedCount INT NOT NULL CONSTRAINT DF_Vouchers_UsedCount DEFAULT 0,
        MaxUsedCount INT NOT NULL,
        MaxDiscountAmount INT NULL,
        MinOrderValue INT NOT NULL,
        [Status] INT NOT NULL CONSTRAINT DF_Vouchers_Status DEFAULT 1,
        Description NVARCHAR(500) NULL,
        CONSTRAINT CK_Vouchers_Value CHECK (VoucherValue > 0),
        CONSTRAINT CK_Vouchers_Dates CHECK (EndDate > StartDate)
    );

    CREATE TABLE dbo.CustomerVoucher (
        CustomerID INT NOT NULL,
        VoucherID INT NOT NULL,
        ExpirationDate DATETIME NULL,
        Quantity INT NOT NULL CONSTRAINT DF_CustomerVoucher_Quantity DEFAULT 1,
        CONSTRAINT PK_CustomerVoucher PRIMARY KEY (CustomerID, VoucherID),
        CONSTRAINT FK_CustomerVoucher_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers(CustomerID),
        CONSTRAINT FK_CustomerVoucher_Vouchers FOREIGN KEY (VoucherID) REFERENCES dbo.Vouchers(VoucherID)
    );

    CREATE TABLE dbo.OrderStatus (
        ID INT NOT NULL CONSTRAINT PK_OrderStatus PRIMARY KEY,
        [Status] NVARCHAR(50) NOT NULL CONSTRAINT UQ_OrderStatus_Status UNIQUE
    );

    CREATE TABLE dbo.Orders (
        OrderID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Orders PRIMARY KEY,
        CustomerID INT NULL,
        FullName NVARCHAR(100) NOT NULL,
        Address NVARCHAR(500) NOT NULL,
        PhoneNumber VARCHAR(15) NOT NULL,
        OrderedDate DATETIME NOT NULL CONSTRAINT DF_Orders_OrderedDate DEFAULT GETDATE(),
        DeliveredDate DATETIME NULL,
        [Status] INT NOT NULL CONSTRAINT DF_Orders_Status DEFAULT 1,
        TotalAmount BIGINT NOT NULL CONSTRAINT DF_Orders_Total DEFAULT 0,
        Discount INT NOT NULL CONSTRAINT DF_Orders_Discount DEFAULT 0,
        CONSTRAINT FK_Orders_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers(CustomerID),
        CONSTRAINT FK_Orders_OrderStatus FOREIGN KEY ([Status]) REFERENCES dbo.OrderStatus(ID),
        CONSTRAINT CK_Orders_Total CHECK (TotalAmount >= 0)
    );

    CREATE TABLE dbo.OrderDetails (
        OrderID INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        Price BIGINT NOT NULL,
        CONSTRAINT PK_OrderDetails PRIMARY KEY (OrderID, ProductID),
        CONSTRAINT FK_OrderDetails_Orders FOREIGN KEY (OrderID) REFERENCES dbo.Orders(OrderID) ON DELETE CASCADE,
        CONSTRAINT FK_OrderDetails_Products FOREIGN KEY (ProductID) REFERENCES dbo.Products(ProductID),
        CONSTRAINT CK_OrderDetails_Quantity CHECK (Quantity > 0),
        CONSTRAINT CK_OrderDetails_Price CHECK (Price > 0)
    );

    CREATE TABLE dbo.ImportStocks (
        ImportID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ImportStocks PRIMARY KEY,
        EmployeeID INT NULL,
        SupplierID INT NULL,
        ImportDate DATETIME NULL,
        TotalCost BIGINT NOT NULL CONSTRAINT DF_ImportStocks_TotalCost DEFAULT 0,
        Completed BIT NOT NULL CONSTRAINT DF_ImportStocks_Completed DEFAULT 0,
        CONSTRAINT FK_ImportStocks_Employees FOREIGN KEY (EmployeeID) REFERENCES dbo.Employees(EmployeeID),
        CONSTRAINT FK_ImportStocks_Suppliers FOREIGN KEY (SupplierID) REFERENCES dbo.Suppliers(SupplierID)
    );

    CREATE TABLE dbo.ImportStockDetails (
        ImportID INT NOT NULL,
        ProductID INT NOT NULL,
        ImportQuantity INT NOT NULL,
        ImportPrice BIGINT NOT NULL,
        CONSTRAINT PK_ImportStockDetails PRIMARY KEY (ImportID, ProductID),
        CONSTRAINT FK_ImportStockDetails_ImportStocks FOREIGN KEY (ImportID) REFERENCES dbo.ImportStocks(ImportID) ON DELETE CASCADE,
        CONSTRAINT FK_ImportStockDetails_Products FOREIGN KEY (ProductID) REFERENCES dbo.Products(ProductID),
        CONSTRAINT CK_ImportStockDetails_Quantity CHECK (ImportQuantity > 0),
        CONSTRAINT CK_ImportStockDetails_Price CHECK (ImportPrice > 0)
    );

    CREATE TABLE dbo.ProductRatings (
        RateID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ProductRatings PRIMARY KEY,
        CustomerID INT NULL,
        ProductID INT NULL,
        OrderID INT NULL,
        CreatedDate DATETIME NOT NULL CONSTRAINT DF_ProductRatings_CreatedDate DEFAULT GETDATE(),
        Star INT NOT NULL,
        Comment NVARCHAR(300) NULL,
        IsDeleted BIT NOT NULL CONSTRAINT DF_ProductRatings_IsDeleted DEFAULT 0,
        IsRead BIT NOT NULL CONSTRAINT DF_ProductRatings_IsRead DEFAULT 0,
        CONSTRAINT FK_ProductRatings_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers(CustomerID),
        CONSTRAINT FK_ProductRatings_Products FOREIGN KEY (ProductID) REFERENCES dbo.Products(ProductID),
        CONSTRAINT FK_ProductRatings_Orders FOREIGN KEY (OrderID) REFERENCES dbo.Orders(OrderID),
        CONSTRAINT CK_ProductRatings_Star CHECK (Star BETWEEN 1 AND 5)
    );

    CREATE TABLE dbo.RatingReplies (
        ReplyID INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_RatingReplies PRIMARY KEY,
        EmployeeID INT NULL,
        RateID INT NULL,
        Answer NVARCHAR(300) NULL,
        IsRead BIT NOT NULL CONSTRAINT DF_RatingReplies_IsRead DEFAULT 0,
        CONSTRAINT FK_RatingReplies_Employees FOREIGN KEY (EmployeeID) REFERENCES dbo.Employees(EmployeeID),
        CONSTRAINT FK_RatingReplies_ProductRatings FOREIGN KEY (RateID) REFERENCES dbo.ProductRatings(RateID) ON DELETE CASCADE
    );

    CREATE TABLE dbo.Carts (
        CustomerID INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        CONSTRAINT PK_Carts PRIMARY KEY (CustomerID, ProductID),
        CONSTRAINT FK_Carts_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers(CustomerID) ON DELETE CASCADE,
        CONSTRAINT FK_Carts_Products FOREIGN KEY (ProductID) REFERENCES dbo.Products(ProductID),
        CONSTRAINT CK_Carts_Quantity CHECK (Quantity > 0)
    );

    CREATE INDEX IX_Products_Category ON dbo.Products(CategoryID, IsDeleted);
    CREATE INDEX IX_Products_Brand ON dbo.Products(BrandID, IsDeleted);
    CREATE INDEX IX_Products_Price ON dbo.Products(Price);
    CREATE INDEX IX_Products_Name_Model ON dbo.Products(FullName, Model);
    CREATE INDEX IX_Orders_Customer_Date ON dbo.Orders(CustomerID, OrderedDate DESC);
    CREATE INDEX IX_Orders_Status ON dbo.Orders([Status]);
    CREATE INDEX IX_Ratings_Product ON dbo.ProductRatings(ProductID, CreatedDate DESC);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
