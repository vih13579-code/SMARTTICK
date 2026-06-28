-- SMARTTICK Skeleton
-- Thành viên phụ trách database có thể chỉnh tên DB tại đây.
IF DB_ID(N'Smarttick') IS NULL
BEGIN
    CREATE DATABASE Smarttick;
END
GO

USE Smarttick;
GO
