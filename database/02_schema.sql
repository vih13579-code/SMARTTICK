USE Smarttick;
GO

IF OBJECT_ID('dbo.Customers', 'U') IS NULL
CREATE TABLE dbo.Customers (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(120) NOT NULL,
    email VARCHAR(160) NULL UNIQUE
);
GO

IF OBJECT_ID('dbo.Products', 'U') IS NULL
CREATE TABLE dbo.Products (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(200) NOT NULL,
    unit_price BIGINT NOT NULL CHECK (unit_price >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    active BIT NOT NULL DEFAULT 1
);
GO

IF OBJECT_ID('dbo.Carts', 'U') IS NULL
CREATE TABLE dbo.Carts (
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT PK_Carts PRIMARY KEY (customer_id, product_id),
    CONSTRAINT FK_Carts_Customers FOREIGN KEY (customer_id) REFERENCES dbo.Customers(customer_id),
    CONSTRAINT FK_Carts_Products FOREIGN KEY (product_id) REFERENCES dbo.Products(product_id)
);
GO

IF OBJECT_ID('dbo.Orders', 'U') IS NULL
CREATE TABLE dbo.Orders (
    order_id INT IDENTITY(1001,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    customer_name NVARCHAR(120) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    status NVARCHAR(30) NOT NULL DEFAULT N'Cho xac nhan',
    CONSTRAINT FK_Orders_Customers FOREIGN KEY (customer_id) REFERENCES dbo.Customers(customer_id),
    CONSTRAINT CK_Orders_Status CHECK (status IN (N'Cho xac nhan',N'Dang xu ly',N'Dang giao',N'Hoan thanh',N'Da huy'))
);
GO

IF OBJECT_ID('dbo.OrderDetails', 'U') IS NULL
CREATE TABLE dbo.OrderDetails (
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name NVARCHAR(200) NOT NULL,
    unit_price BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    CONSTRAINT PK_OrderDetails PRIMARY KEY (order_id, product_id),
    CONSTRAINT FK_OrderDetails_Orders FOREIGN KEY (order_id) REFERENCES dbo.Orders(order_id),
    CONSTRAINT FK_OrderDetails_Products FOREIGN KEY (product_id) REFERENCES dbo.Products(product_id)
);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_Orders_Customer_Created' AND object_id=OBJECT_ID('dbo.Orders'))
    CREATE INDEX IX_Orders_Customer_Created ON dbo.Orders(customer_id, created_at DESC);
GO
