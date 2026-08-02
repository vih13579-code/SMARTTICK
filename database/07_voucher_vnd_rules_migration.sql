/*
 * SMARTTICK Voucher VND validation migration.
 *
 * Use this migration after database/06_simplify_vouchers_migration.sql.
 * It keeps the existing table and data, reports rows that violate the new
 * VND rules, and replaces only the related CHECK constraints.
 *
 * First run with @ApplyDataFixes = 0. If invalid rows are reported, review
 * the proposed rounded values before changing the flag to 1.
 */
USE [FWatch];
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

IF OBJECT_ID(N'dbo.Vouchers', N'U') IS NULL
    THROW 51100, 'dbo.Vouchers does not exist.', 1;

IF COL_LENGTH(N'dbo.Vouchers', N'VoucherType') IS NULL
   OR COL_LENGTH(N'dbo.Vouchers', N'VoucherValue') IS NULL
   OR COL_LENGTH(N'dbo.Vouchers', N'MaxDiscountAmount') IS NULL
   OR COL_LENGTH(N'dbo.Vouchers', N'MinOrderValue') IS NULL
    THROW 51101, 'dbo.Vouchers does not use the expected simplified schema.', 1;

DECLARE @ApplyDataFixes BIT = 0;

CREATE TABLE #VoucherVndIssues (
    VoucherID INT NOT NULL,
    VoucherCode VARCHAR(30) NOT NULL,
    IssueCode VARCHAR(40) NOT NULL,
    CurrentValue NVARCHAR(200) NULL,
    ProposedValue NVARCHAR(200) NULL
);

INSERT INTO #VoucherVndIssues
SELECT VoucherID, VoucherCode, 'PERCENT_VALUE_OUT_OF_RANGE',
       CONVERT(NVARCHAR(200), VoucherValue),
       CASE WHEN VoucherValue < 1 THEN N'1' ELSE N'100' END
FROM dbo.Vouchers
WHERE VoucherType = 'PERCENT'
  AND (VoucherValue < 1 OR VoucherValue > 100);

INSERT INTO #VoucherVndIssues
SELECT VoucherID, VoucherCode, 'INVALID_PERCENT_MAX',
       CONVERT(NVARCHAR(200), MaxDiscountAmount),
       CONVERT(NVARCHAR(200),
           CASE
               WHEN MaxDiscountAmount IS NULL OR MaxDiscountAmount < 1000 THEN 1000
               ELSE CEILING(MaxDiscountAmount / 1000.0) * 1000
           END)
FROM dbo.Vouchers
WHERE VoucherType = 'PERCENT'
  AND (
      MaxDiscountAmount IS NULL
      OR MaxDiscountAmount < 1000
      OR MaxDiscountAmount % 1000 <> 0
  );

INSERT INTO #VoucherVndIssues
SELECT VoucherID, VoucherCode, 'FIXED_MAX_NOT_NULL',
       CONVERT(NVARCHAR(200), MaxDiscountAmount), N'NULL'
FROM dbo.Vouchers
WHERE VoucherType = 'FIXED'
  AND MaxDiscountAmount IS NOT NULL;

INSERT INTO #VoucherVndIssues
SELECT VoucherID, VoucherCode, 'INVALID_MIN_ORDER_VND',
       CONVERT(NVARCHAR(200), MinOrderValue),
       CONVERT(NVARCHAR(200),
           CASE
               WHEN MinOrderValue < 1000 THEN 1000
               ELSE CEILING(MinOrderValue / 1000.0) * 1000
           END)
FROM dbo.Vouchers
WHERE MinOrderValue < 1000
   OR MinOrderValue % 1000 <> 0;

INSERT INTO #VoucherVndIssues
SELECT VoucherID, VoucherCode, 'FIXED_MIN_BELOW_VALUE',
       CONVERT(NVARCHAR(200), MinOrderValue),
       CONVERT(NVARCHAR(200), CEILING(VoucherValue / 1000.0) * 1000)
FROM dbo.Vouchers
WHERE VoucherType = 'FIXED'
  AND MinOrderValue < VoucherValue;

IF EXISTS (SELECT 1 FROM #VoucherVndIssues)
BEGIN
    SELECT VoucherID, VoucherCode, IssueCode, CurrentValue, ProposedValue
    FROM #VoucherVndIssues
    ORDER BY VoucherID, IssueCode;

    IF @ApplyDataFixes = 0
        THROW 51102,
            'Invalid VND voucher rows were found. Review the report before setting @ApplyDataFixes = 1.',
            1;
END;

BEGIN TRY
    BEGIN TRANSACTION;

    IF @ApplyDataFixes = 1
    BEGIN
        DECLARE @BackupTable SYSNAME =
            N'Vouchers_Vnd_Backup_' + CONVERT(CHAR(8), GETDATE(), 112)
            + N'_' + REPLACE(CONVERT(CHAR(8), GETDATE(), 108), N':', N'');
        DECLARE @BackupSql NVARCHAR(MAX) =
            N'SELECT * INTO dbo.' + QUOTENAME(@BackupTable) + N' FROM dbo.Vouchers;';
        EXEC sys.sp_executesql @BackupSql;
        PRINT N'Backup created: dbo.' + @BackupTable;

        UPDATE dbo.Vouchers
        SET VoucherValue =
            CASE WHEN VoucherValue < 1 THEN 1 ELSE 100 END
        WHERE VoucherType = 'PERCENT'
          AND (VoucherValue < 1 OR VoucherValue > 100);

        UPDATE dbo.Vouchers
        SET MaxDiscountAmount =
            CASE
                WHEN MaxDiscountAmount IS NULL OR MaxDiscountAmount < 1000 THEN 1000
                ELSE CEILING(MaxDiscountAmount / 1000.0) * 1000
            END
        WHERE VoucherType = 'PERCENT'
          AND (
              MaxDiscountAmount IS NULL
              OR MaxDiscountAmount < 1000
              OR MaxDiscountAmount % 1000 <> 0
          );

        UPDATE dbo.Vouchers
        SET MaxDiscountAmount = NULL
        WHERE VoucherType = 'FIXED';

        UPDATE dbo.Vouchers
        SET MinOrderValue =
            CASE
                WHEN MinOrderValue < 1000 THEN 1000
                ELSE CEILING(MinOrderValue / 1000.0) * 1000
            END
        WHERE MinOrderValue < 1000
           OR MinOrderValue % 1000 <> 0;

        UPDATE dbo.Vouchers
        SET MinOrderValue = CEILING(VoucherValue / 1000.0) * 1000
        WHERE VoucherType = 'FIXED'
          AND MinOrderValue < VoucherValue;
    END;

    IF EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE parent_object_id = OBJECT_ID(N'dbo.Vouchers')
          AND name = N'CK_Vouchers_Value'
    )
        ALTER TABLE dbo.Vouchers DROP CONSTRAINT CK_Vouchers_Value;

    IF EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE parent_object_id = OBJECT_ID(N'dbo.Vouchers')
          AND name = N'CK_Vouchers_MinOrderValue'
    )
        ALTER TABLE dbo.Vouchers DROP CONSTRAINT CK_Vouchers_MinOrderValue;

    IF EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE parent_object_id = OBJECT_ID(N'dbo.Vouchers')
          AND name = N'CK_Vouchers_MaxDiscount'
    )
        ALTER TABLE dbo.Vouchers DROP CONSTRAINT CK_Vouchers_MaxDiscount;

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_Value CHECK (
            VoucherValue > 0
            AND (
                VoucherType <> 'PERCENT'
                OR (VoucherValue >= 1 AND VoucherValue <= 100)
            )
        );

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_MinOrderValue CHECK (
            MinOrderValue >= 1000
            AND MinOrderValue % 1000 = 0
        );

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_MaxDiscount CHECK (
            (VoucherType = 'FIXED' AND MaxDiscountAmount IS NULL)
            OR
            (VoucherType = 'PERCENT'
             AND MaxDiscountAmount IS NOT NULL
             AND MaxDiscountAmount >= 1000
             AND MaxDiscountAmount % 1000 = 0)
        );

    ALTER TABLE dbo.Vouchers WITH CHECK CHECK CONSTRAINT
        CK_Vouchers_Value,
        CK_Vouchers_MinOrderValue,
        CK_Vouchers_MaxDiscount;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

SELECT VoucherID, VoucherCode, VoucherType, VoucherValue,
       MaxDiscountAmount, MinOrderValue, EndDate
FROM dbo.Vouchers
ORDER BY VoucherID;
GO
