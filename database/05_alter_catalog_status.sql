-- SMARTTICK - 05_alter_catalog_status.sql
-- Adds Status and CreatedDate to Categories & Brands tables.
-- Run once against FWatch database.
USE [FWatch];
GO

-- ── Categories ──────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Categories') AND name = 'Status')
BEGIN
    ALTER TABLE dbo.Categories ADD [Status] BIT NOT NULL CONSTRAINT DF_Categories_Status DEFAULT 1;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Categories') AND name = 'CreatedDate')
BEGIN
    ALTER TABLE dbo.Categories ADD CreatedDate DATETIME NOT NULL CONSTRAINT DF_Categories_CreatedDate DEFAULT GETDATE();
END
GO

-- ── Brands ──────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Brands') AND name = 'Status')
BEGIN
    ALTER TABLE dbo.Brands ADD [Status] BIT NOT NULL CONSTRAINT DF_Brands_Status DEFAULT 1;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Brands') AND name = 'CreatedDate')
BEGIN
    ALTER TABLE dbo.Brands ADD CreatedDate DATETIME NOT NULL CONSTRAINT DF_Brands_CreatedDate DEFAULT GETDATE();
END
GO
