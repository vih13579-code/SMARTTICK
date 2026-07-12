-- Allow Google profile image URLs to be stored as customer avatars.
USE [FWatch];
GO

IF COL_LENGTH('dbo.Customers', 'Avatar') IS NOT NULL
BEGIN
    ALTER TABLE dbo.Customers ALTER COLUMN Avatar NVARCHAR(1024) NULL;
END
GO
