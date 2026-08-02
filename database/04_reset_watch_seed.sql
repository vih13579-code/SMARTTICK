-- FWatch - 04_reset_watch_seed.sql
-- Development seed: safely replaces transactional and catalog demo data.
USE [FWatch];
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;
    DELETE FROM dbo.RatingReplies; DELETE FROM dbo.ProductRatings; DELETE FROM dbo.Carts;
    DELETE FROM dbo.OrderDetails; DELETE FROM dbo.Orders; DELETE FROM dbo.CustomerVoucher;
    DELETE FROM dbo.Addresses; DELETE FROM dbo.ImportStockDetails; DELETE FROM dbo.ImportStocks;
    DELETE FROM dbo.AttributeDetails; DELETE FROM dbo.Attributes; DELETE FROM dbo.Products;
    DELETE FROM dbo.Suppliers; DELETE FROM dbo.Vouchers; DELETE FROM dbo.OrderStatus;
    DELETE FROM dbo.Customers; DELETE FROM dbo.Employees; DELETE FROM dbo.Roles;
    DELETE FROM dbo.Brands; DELETE FROM dbo.Categories;
    DBCC CHECKIDENT ('dbo.RatingReplies', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.ProductRatings', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Orders', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Addresses', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.ImportStocks', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Attributes', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Products', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Suppliers', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Vouchers', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Customers', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Employees', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Roles', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Brands', RESEED, 0) WITH NO_INFOMSGS;
    DBCC CHECKIDENT ('dbo.Categories', RESEED, 0) WITH NO_INFOMSGS;
    INSERT INTO dbo.Roles(Name) VALUES (N'Admin'),(N'Shop Manager'),(N'Order Manager'),(N'Warehouse Manager');

    INSERT INTO dbo.Employees(FullName,Birthday,[Password],PhoneNumber,Email,Gender,CreatedDate,[Status],Avatar,RoleID) VALUES
    (N'FWatch Administrator','1995-01-01','0e7517141fb53f21ee439b355b5a1d0a','0901000001','admin@fwatch.vn',N'Other',CAST(GETDATE() AS DATE),1,NULL,1),
    (N'FWatch Shop Manager','1996-02-02','f09e2fa7b19117d5b6637dcc6388fffa','0901000002','manager@fwatch.vn',N'Other',CAST(GETDATE() AS DATE),1,NULL,2),
    (N'FWatch Order Manager','1997-03-03','f09e2fa7b19117d5b6637dcc6388fffa','0901000003','orders@fwatch.vn',N'Other',CAST(GETDATE() AS DATE),1,NULL,3),
    (N'FWatch Warehouse Manager','1998-04-04','f09e2fa7b19117d5b6637dcc6388fffa','0901000004','warehouse@fwatch.vn',N'Other',CAST(GETDATE() AS DATE),1,NULL,4);

    INSERT INTO dbo.Customers(FullName,Birthday,[Password],PhoneNumber,Email,Gender,CreatedDate,GoogleID,IsBlock,IsDeleted,Avatar) VALUES
    (N'FWatch Customer','2001-06-16','681ae46305e29b966801a96331ae607d','0912345678','customer@fwatch.vn',N'Other',GETDATE(),NULL,0,0,NULL),
    (N'Minh Anh Nguyen','2000-08-20','681ae46305e29b966801a96331ae607d','0923456789','minhanh@fwatch.vn',N'Female',DATEADD(DAY,-20,GETDATE()),NULL,0,0,NULL),
    (N'Quoc Bao Tran','1999-11-12','681ae46305e29b966801a96331ae607d','0934567890','quocbao@fwatch.vn',N'Male',DATEADD(DAY,-40,GETDATE()),NULL,0,0,NULL);

    INSERT INTO dbo.Addresses(CustomerID,AddressDetails,IsDefault) VALUES
    (1,N'Ninh Kieu, Can Tho',1),(1,N'District 1, Ho Chi Minh City',0),(2,N'Cai Rang, Can Tho',1),(3,N'Hai Chau, Da Nang',1);

    INSERT INTO dbo.Categories(Name) VALUES
    (N'Men''s Watches'),(N'Women''s Watches'),(N'Sports Watches'),(N'Mechanical Watches'),(N'Quartz Watches');

    INSERT INTO dbo.Brands(Name) VALUES
    (N'Casio'),(N'Citizen'),(N'Seiko'),(N'Orient'),(N'Tissot'),(N'Daniel Wellington');

    INSERT INTO dbo.Suppliers(TaxID,Name,Email,PhoneNumber,Address,CreatedDate,LastModify,IsDeleted,IsActivate) VALUES
    ('FW-CASIO-001',N'FWatch Casio Distributor','casio.supplier@fwatch.vn','0902000001',N'Ho Chi Minh City',GETDATE(),GETDATE(),0,1),
    ('FW-SWISS-002',N'FWatch Swiss Partner','swiss.supplier@fwatch.vn','0902000002',N'Hanoi',GETDATE(),GETDATE(),0,1),
    ('FW-JAPAN-003',N'FWatch Japan Partner','japan.supplier@fwatch.vn','0902000003',N'Da Nang',GETDATE(),GETDATE(),0,1);

    INSERT INTO dbo.Products(BrandID,CategoryID,Model,FullName,Description,IsDeleted,Price,Image,Image1,Image2,Image3,Quantity,Stock) VALUES
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Casio'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Men''s Watches'), N'MTP-1302PD-2A2V', N'Casio MTP-1302 Classic Blue', N'Classic blue-dial design, suitable for office wear and everyday use.', 0, 2390000, N'casio-mtp-1302-main.svg', N'casio-mtp-1302-side.svg', N'casio-mtp-1302-back.svg', N'casio-mtp-1302-detail.svg', 0, 24),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Casio'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches'), N'EFV-100D-1AV', N'Casio Edifice EFV-100D', N'A bold Edifice line with a stainless-steel bracelet and solid water resistance.', 0, 3290000, N'casio-edifice-efv-main.svg', N'casio-edifice-efv-side.svg', N'casio-edifice-efv-back.svg', N'casio-edifice-efv-detail.svg', 0, 16),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Citizen'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches'), N'NJ0150-81X', N'Citizen Tsuyosa Automatic', N'Modern blue-dial automatic watch with sapphire crystal and integrated steel bracelet.', 0, 10500000, N'citizen-tsuyosa-main.svg', N'citizen-tsuyosa-side.svg', N'citizen-tsuyosa-back.svg', N'citizen-tsuyosa-detail.svg', 0, 9),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Citizen'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Men''s Watches'), N'BM7108-81E', N'Citizen Eco-Drive Classic', N'Eco-Drive light-powered technology with a refined design for men.', 0, 7200000, N'citizen-eco-drive-main.svg', N'citizen-eco-drive-side.svg', N'citizen-eco-drive-back.svg', N'citizen-eco-drive-detail.svg', 0, 11),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Seiko'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches'), N'SRPD55K1', N'Seiko 5 Sports Automatic', N'Durable Seiko 5 Sports automatic movement with versatile sporty styling.', 0, 8900000, N'seiko-5-sports-main.svg', N'seiko-5-sports-side.svg', N'seiko-5-sports-back.svg', N'seiko-5-sports-detail.svg', 0, 8),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Seiko'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches'), N'SRPB41J1', N'Seiko Presage Cocktail Time', N'Signature sunburst dial, Japanese automatic movement, and elegant design.', 0, 13900000, N'seiko-presage-main.svg', N'seiko-presage-side.svg', N'seiko-presage-back.svg', N'seiko-presage-detail.svg', 0, 6),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Orient'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches'), N'RA-AC0M04Y10B', N'Orient Bambino Classic', N'Classic dress watch design with domed crystal and in-house automatic movement.', 0, 6990000, N'orient-bambino-main.svg', N'orient-bambino-side.svg', N'orient-bambino-back.svg', N'orient-bambino-detail.svg', 0, 14),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Orient'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches'), N'RA-AA0004E19B', N'Orient Mako III', N'Sport dive watch with 20 ATM water resistance and rotating bezel.', 0, 7990000, N'orient-mako-main.svg', N'orient-mako-side.svg', N'orient-mako-back.svg', N'orient-mako-detail.svg', 0, 7),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Tissot'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Men''s Watches'), N'T137.410.11.041.00', N'Tissot PRX Quartz Blue', N'Iconic 1970s integrated design with precise Swiss quartz movement.', 0, 12500000, N'tissot-prx-main.svg', N'tissot-prx-side.svg', N'tissot-prx-back.svg', N'tissot-prx-detail.svg', 0, 5),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Tissot'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches'), N'T006.407.11.033.00', N'Tissot Le Locle Powermatic 80', N'Swiss dress watch with Powermatic 80 movement and sapphire crystal.', 0, 23500000, N'tissot-le-locle-main.svg', N'tissot-le-locle-side.svg', N'tissot-le-locle-back.svg', N'tissot-le-locle-detail.svg', 0, 4),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Daniel Wellington'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches'), N'DW00100304', N'Daniel Wellington Petite Evergold', N'Minimal design with an elegant small dial and champagne-gold mesh strap.', 0, 4690000, N'dw-petite-main.svg', N'dw-petite-side.svg', N'dw-petite-back.svg', N'dw-petite-detail.svg', 0, 18),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Daniel Wellington'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches'), N'DW00100437', N'Daniel Wellington Iconic Link', N'Modern minimalist style with an elegant link bracelet.', 0, 5990000, N'dw-iconic-main.svg', N'dw-iconic-side.svg', N'dw-iconic-back.svg', N'dw-iconic-detail.svg', 0, 12),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Casio'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches'), N'GA-2100-1A1', N'Casio G-Shock GA-2100', N'Carbon Core Guard structure, shock resistance, and a bold sporty look.', 0, 3290000, N'casio-gshock-main.svg', N'casio-gshock-side.svg', N'casio-gshock-back.svg', N'casio-gshock-detail.svg', 0, 20),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Seiko'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches'), N'SUR531P1', N'Seiko Classic Ladies', N'Elegant ladies model with sapphire crystal and stainless-steel bracelet.', 0, 6700000, N'seiko-ladies-main.svg', N'seiko-ladies-side.svg', N'seiko-ladies-back.svg', N'seiko-ladies-detail.svg', 0, 10),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Citizen'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches'), N'EM0500-73A', N'Citizen Ladies Eco-Drive', N'Light-powered ladies watch with a slim, lightweight design.', 0, 6100000, N'citizen-ladies-main.svg', N'citizen-ladies-side.svg', N'citizen-ladies-back.svg', N'citizen-ladies-detail.svg', 0, 13),
        ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Orient'), (SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches'), N'RE-AV0004N00B', N'Orient Star Contemporary', N'Premium Orient Star line with semi-open-heart design and visible power reserve.', 0, 19500000, N'orient-star-main.svg', N'orient-star-side.svg', N'orient-star-back.svg', N'orient-star-detail.svg', 0, 3);

    DECLARE @AttributeNames TABLE(Name NVARCHAR(100));
    INSERT INTO @AttributeNames(Name) VALUES
    (N'Movement'),(N'Crystal Material'),(N'Strap Material'),(N'Water Resistance'),(N'Case Diameter'),(N'Case Shape'),(N'Style'),(N'Brand Origin'),(N'Warranty Period');
    INSERT INTO dbo.Attributes(CategoryID,Name)
    SELECT c.CategoryID,a.Name FROM dbo.Categories c CROSS JOIN @AttributeNames a;

    INSERT INTO dbo.AttributeDetails(AttributeID,ProductID,AttributeInfor)
    SELECT a.AttributeID,p.ProductID,
      CASE a.Name
        WHEN N'Movement' THEN CASE WHEN p.CategoryID=(SELECT CategoryID FROM dbo.Categories WHERE Name=N'Mechanical Watches') OR p.Model IN (N'NJ0150-81X',N'SRPD55K1',N'RA-AA0004E19B') THEN N'Automatic' WHEN p.Model IN (N'BM7108-81E',N'EM0500-73A') THEN N'Solar Eco-Drive' ELSE N'Quartz' END
        WHEN N'Crystal Material' THEN CASE WHEN p.Price>=10000000 THEN N'Sapphire' WHEN p.BrandID=(SELECT BrandID FROM dbo.Brands WHERE Name=N'Seiko') THEN N'Hardlex' ELSE N'Mineral Glass' END
        WHEN N'Strap Material' THEN CASE WHEN p.Model=N'GA-2100-1A1' THEN N'Rubber' WHEN p.Model=N'RA-AC0M04Y10B' THEN N'Leather Strap' ELSE N'Stainless Steel' END
        WHEN N'Water Resistance' THEN CASE WHEN p.Model IN (N'RA-AA0004E19B',N'GA-2100-1A1') THEN N'20 ATM' WHEN p.CategoryID=(SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches') THEN N'10 ATM' ELSE N'5 ATM' END
        WHEN N'Case Diameter' THEN CASE WHEN p.CategoryID=(SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches') THEN N'28–32 mm' ELSE N'38–42 mm' END
        WHEN N'Case Shape' THEN N'Round'
        WHEN N'Style' THEN CASE WHEN p.CategoryID=(SELECT CategoryID FROM dbo.Categories WHERE Name=N'Sports Watches') THEN N'Sports' WHEN p.CategoryID=(SELECT CategoryID FROM dbo.Categories WHERE Name=N'Women''s Watches') THEN N'Minimalist, elegant' ELSE N'Office, classic' END
        WHEN N'Brand Origin' THEN CASE WHEN p.BrandID IN ((SELECT BrandID FROM dbo.Brands WHERE Name=N'Tissot')) THEN N'Switzerland' WHEN p.BrandID=(SELECT BrandID FROM dbo.Brands WHERE Name=N'Daniel Wellington') THEN N'Sweden' ELSE N'Japan' END
        WHEN N'Warranty Period' THEN CASE WHEN p.Price>=10000000 THEN N'24 months' ELSE N'12 months' END
      END
    FROM dbo.Products p JOIN dbo.Attributes a ON a.CategoryID=p.CategoryID;

    INSERT INTO dbo.OrderStatus(ID,[Status]) VALUES
    (1,N'Pending Confirmation'),(2,N'Confirmed'),(3,N'Shipping'),(4,N'Delivered'),(5,N'Canceled');

    INSERT INTO dbo.Vouchers(VoucherCode,VoucherType,VoucherValue,MaxDiscountAmount,MinOrderValue,EndDate) VALUES
    ('FWELCOME','PERCENT',10,500000,2000000,DATEADD(MONTH,6,SYSDATETIME())),
    ('FREESHIP','FIXED',50000,NULL,1000000,DATEADD(MONTH,3,SYSDATETIME()));
    INSERT INTO dbo.CustomerVoucher(CustomerID,VoucherID,ExpirationDate,Quantity)
    SELECT 1,VoucherID,EndDate,1 FROM dbo.Vouchers;

    INSERT INTO dbo.Orders(CustomerID,FullName,Address,PhoneNumber,OrderedDate,DeliveredDate,[Status],TotalAmount,Discount) VALUES
    (1,N'FWatch Customer',N'Ninh Kieu, Can Tho','0912345678',DATEADD(DAY,-25,GETDATE()),DATEADD(DAY,-22,GETDATE()),4,10500000,0),
    (1,N'FWatch Customer',N'Ninh Kieu, Can Tho','0912345678',DATEADD(DAY,-5,GETDATE()),NULL,3,3290000,0),
    (2,N'Minh Anh Nguyen',N'Cai Rang, Can Tho','0923456789',DATEADD(DAY,-12,GETDATE()),DATEADD(DAY,-9,GETDATE()),4,4690000,0),
    (3,N'Quoc Bao Tran',N'Hai Chau, Da Nang','0934567890',DATEADD(DAY,-2,GETDATE()),NULL,1,8900000,0);
    INSERT INTO dbo.OrderDetails(OrderID,ProductID,Quantity,Price) VALUES
    (1,3,1,10500000),(2,13,1,3290000),(3,11,1,4690000),(4,5,1,8900000);

    INSERT INTO dbo.ImportStocks(EmployeeID,SupplierID,ImportDate,TotalCost,Completed) VALUES
    (4,1,DATEADD(DAY,-30,GETDATE()),48000000,1),(4,3,DATEADD(DAY,-18,GETDATE()),120000000,1);
    INSERT INTO dbo.ImportStockDetails(ImportID,ProductID,ImportQuantity,ImportPrice) VALUES
    (1,1,20,1700000),(1,13,20,2400000),(2,3,10,8500000),(2,5,10,7100000);

    INSERT INTO dbo.ProductRatings(CustomerID,ProductID,OrderID,CreatedDate,Star,Comment,IsDeleted,IsRead) VALUES
    (1,3,1,DATEADD(DAY,-20,GETDATE()),5,N'Beautiful dial, stable movement, and careful packaging.',0,0),
    (2,11,3,DATEADD(DAY,-8,GETDATE()),4,N'Elegant design, suitable for daily wear.',0,0);
    INSERT INTO dbo.RatingReplies(EmployeeID,RateID,Answer,IsRead) VALUES
    (1,1,N'FWatch thanks you for trusting our product.',0);
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
PRINT N'FWatch seed completed successfully.';
GO
