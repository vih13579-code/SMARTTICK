-- FWatch - 01_create_database.sql
-- Run in SQL Server Management Studio with an account that can create databases.
USE [master];
GO
IF DB_ID(N'FWatch') IS NULL
BEGIN
    CREATE DATABASE [FWatch];
END
GO
ALTER DATABASE [FWatch] SET READ_COMMITTED_SNAPSHOT ON WITH ROLLBACK IMMEDIATE;
GO
