/*
 * Distinguishes an intentional SMARTTICK password from a legacy password
 * placeholder on Google accounts.
 *
 * Existing Google-linked accounts start with HasLocalPassword = 0 and may
 * create their own password without entering a current password. Existing
 * local accounts keep HasLocalPassword = 1.
 */
USE [FWatch];
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

IF OBJECT_ID(N'dbo.Customers', N'U') IS NULL
    THROW 51200, 'dbo.Customers does not exist.', 1;

IF COL_LENGTH(N'dbo.Customers', N'HasLocalPassword') IS NULL
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;

        ALTER TABLE dbo.Customers
            ADD HasLocalPassword BIT NULL;

        EXEC sys.sp_executesql N'
            UPDATE dbo.Customers
            SET HasLocalPassword =
                CASE
                    WHEN NULLIF(LTRIM(RTRIM(GoogleID)), '''') IS NOT NULL THEN 0
                    WHEN NULLIF(LTRIM(RTRIM([Password])), '''') IS NOT NULL THEN 1
                    ELSE 0
                END;';

        EXEC sys.sp_executesql N'
            ALTER TABLE dbo.Customers
                ALTER COLUMN HasLocalPassword BIT NOT NULL;';

        EXEC sys.sp_executesql N'
            ALTER TABLE dbo.Customers
                ADD CONSTRAINT DF_Customers_HasLocalPassword
                DEFAULT 1 FOR HasLocalPassword;';

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

SELECT CustomerID, Email,
       CASE
           WHEN NULLIF(LTRIM(RTRIM(GoogleID)), '') IS NULL THEN 'LOCAL'
           ELSE 'GOOGLE'
       END AS AccountSource,
       HasLocalPassword
FROM dbo.Customers
ORDER BY CustomerID;
GO
