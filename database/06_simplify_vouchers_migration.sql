/*
 * SMARTTICK Voucher migration (existing table -> simplified seven-column table).
 *
 * This is a migration for the existing dbo.Vouchers table. It does not drop the
 * table, preserves VoucherID values/FKs, and creates a timestamped data backup.
 *
 * Keep @ApplyDataFixes = 0 on the first run. The script reports every invalid row
 * and stops before changing it. Review that result, then explicitly set the flag
 * to 1 if the proposed deterministic corrections are accepted.
 */
USE [FWatch];
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

IF OBJECT_ID(N'dbo.Vouchers', N'U') IS NULL
    THROW 51000, 'dbo.Vouchers does not exist.', 1;

IF COL_LENGTH(N'dbo.Vouchers', N'StartDate') IS NULL
    THROW 51001, 'dbo.Vouchers already appears to use the simplified schema.', 1;

DECLARE @ApplyDataFixes BIT = 0;
DECLARE @BackupTable SYSNAME =
    N'Vouchers_Backup_' + CONVERT(CHAR(8), GETDATE(), 112)
    + N'_' + REPLACE(CONVERT(CHAR(8), GETDATE(), 108), N':', N'');
DECLARE @BackupSql NVARCHAR(MAX) =
    N'SELECT * INTO dbo.' + QUOTENAME(@BackupTable) + N' FROM dbo.Vouchers;';

EXEC sys.sp_executesql @BackupSql;
PRINT N'Backup created: dbo.' + @BackupTable;

CREATE TABLE #VoucherMigrationIssues (
    VoucherID INT NOT NULL,
    VoucherCode VARCHAR(100) NULL,
    IssueCode VARCHAR(40) NOT NULL,
    CurrentValue NVARCHAR(200) NULL,
    ProposedValue NVARCHAR(200) NULL
);

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'INVALID_CODE', VoucherCode,
       'MANUAL_CORRECTION_REQUIRED'
FROM dbo.Vouchers
WHERE LEN(LTRIM(RTRIM(VoucherCode))) NOT BETWEEN 3 AND 30
   OR UPPER(LTRIM(RTRIM(VoucherCode))) COLLATE Latin1_General_100_BIN2
      LIKE '%[^A-Z0-9_-]%';

;WITH DuplicateCodes AS (
    SELECT VoucherID, VoucherCode,
           ROW_NUMBER() OVER (
               PARTITION BY UPPER(LTRIM(RTRIM(VoucherCode)))
               ORDER BY VoucherID
           ) AS DuplicateNumber
    FROM dbo.Vouchers
)
INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'DUPLICATE_CODE', VoucherCode,
       'MANUAL_CORRECTION_REQUIRED'
FROM DuplicateCodes
WHERE DuplicateNumber > 1;

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'INVALID_TYPE', CONVERT(NVARCHAR(200), VoucherType), '0 (FIXED)'
FROM dbo.Vouchers
WHERE VoucherType NOT IN (0, 1);

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'VALUE_NOT_POSITIVE', CONVERT(NVARCHAR(200), VoucherValue), '1'
FROM dbo.Vouchers
WHERE VoucherValue <= 0;

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'PERCENT_BELOW_1', CONVERT(NVARCHAR(200), VoucherValue), '1'
FROM dbo.Vouchers
WHERE VoucherType = 1 AND VoucherValue > 0 AND VoucherValue < 1;

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'PERCENT_OVER_100', CONVERT(NVARCHAR(200), VoucherValue), '100'
FROM dbo.Vouchers
WHERE VoucherType = 1 AND VoucherValue > 100;

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'INVALID_PERCENT_MAX',
       CONVERT(NVARCHAR(200), MaxDiscountAmount),
       CONVERT(NVARCHAR(200),
           CASE
               WHEN MaxDiscountAmount IS NULL OR MaxDiscountAmount < 1000 THEN 1000
               ELSE CEILING(MaxDiscountAmount / 1000.0) * 1000
           END)
FROM dbo.Vouchers
WHERE VoucherType = 1
  AND (
      MaxDiscountAmount IS NULL
      OR MaxDiscountAmount < 1000
      OR MaxDiscountAmount % 1000 <> 0
  );

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'FIXED_MAX_NOT_NULL',
       CONVERT(NVARCHAR(200), MaxDiscountAmount), 'NULL'
FROM dbo.Vouchers
WHERE VoucherType = 0 AND MaxDiscountAmount IS NOT NULL;

INSERT INTO #VoucherMigrationIssues
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

INSERT INTO #VoucherMigrationIssues
SELECT VoucherID, VoucherCode, 'FIXED_MIN_BELOW_VALUE',
       CONVERT(NVARCHAR(200), MinOrderValue), CONVERT(NVARCHAR(200), VoucherValue)
FROM dbo.Vouchers
WHERE VoucherType = 0 AND MinOrderValue < VoucherValue;

IF EXISTS (SELECT 1 FROM #VoucherMigrationIssues)
BEGIN
    SELECT VoucherID, VoucherCode, IssueCode, CurrentValue, ProposedValue
    FROM #VoucherMigrationIssues
    ORDER BY VoucherID, IssueCode;

    IF EXISTS (
        SELECT 1 FROM #VoucherMigrationIssues
        WHERE IssueCode IN ('INVALID_CODE', 'DUPLICATE_CODE')
    )
        THROW 51003,
            'Invalid or duplicate business codes require manual correction before migration.',
            1;

    IF @ApplyDataFixes = 0
        THROW 51002,
            'Invalid voucher rows were found. Review the report, then set @ApplyDataFixes = 1 to continue.',
            1;
END;

BEGIN TRY
    BEGIN TRANSACTION;

    IF @ApplyDataFixes = 1
    BEGIN
        UPDATE dbo.Vouchers
        SET VoucherType = 0
        WHERE VoucherType NOT IN (0, 1);

        UPDATE dbo.Vouchers
        SET VoucherValue = 1
        WHERE VoucherValue <= 0;

        UPDATE dbo.Vouchers
        SET VoucherValue = 100
        WHERE VoucherType = 1 AND VoucherValue > 100;

        UPDATE dbo.Vouchers
        SET VoucherValue = 1
        WHERE VoucherType = 1 AND VoucherValue > 0 AND VoucherValue < 1;

        UPDATE dbo.Vouchers
        SET MaxDiscountAmount =
            CASE
                WHEN MaxDiscountAmount IS NULL OR MaxDiscountAmount < 1000 THEN 1000
                ELSE CEILING(MaxDiscountAmount / 1000.0) * 1000
            END
        WHERE VoucherType = 1
          AND (
              MaxDiscountAmount IS NULL
              OR MaxDiscountAmount < 1000
              OR MaxDiscountAmount % 1000 <> 0
          );

        UPDATE dbo.Vouchers
        SET MaxDiscountAmount = NULL
        WHERE VoucherType = 0;

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
        WHERE VoucherType = 0 AND MinOrderValue < VoucherValue;
    END;

    UPDATE dbo.Vouchers
    SET VoucherCode = UPPER(LTRIM(RTRIM(VoucherCode)));

    DECLARE @DropSql NVARCHAR(MAX) = N'';

    SELECT @DropSql += N'ALTER TABLE dbo.Vouchers DROP CONSTRAINT '
            + QUOTENAME(dc.name) + N';'
    FROM sys.default_constraints dc
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.Vouchers');

    SELECT @DropSql += N'ALTER TABLE dbo.Vouchers DROP CONSTRAINT '
            + QUOTENAME(cc.name) + N';'
    FROM sys.check_constraints cc
    WHERE cc.parent_object_id = OBJECT_ID(N'dbo.Vouchers');

    SELECT @DropSql += N'ALTER TABLE dbo.Vouchers DROP CONSTRAINT '
            + QUOTENAME(kc.name) + N';'
    FROM sys.key_constraints kc
    WHERE kc.parent_object_id = OBJECT_ID(N'dbo.Vouchers')
      AND kc.[type] = 'UQ';

    EXEC sys.sp_executesql @DropSql;

    ALTER TABLE dbo.Vouchers
        ALTER COLUMN VoucherCode VARCHAR(30)
        COLLATE Latin1_General_100_CI_AS NOT NULL;

    ALTER TABLE dbo.Vouchers ALTER COLUMN VoucherType VARCHAR(10) NOT NULL;

    UPDATE dbo.Vouchers
    SET VoucherType = CASE VoucherType
        WHEN '1' THEN 'PERCENT'
        WHEN '0' THEN 'FIXED'
    END;

    ALTER TABLE dbo.Vouchers ALTER COLUMN VoucherValue DECIMAL(18,2) NOT NULL;
    ALTER TABLE dbo.Vouchers ALTER COLUMN MaxDiscountAmount DECIMAL(18,2) NULL;
    ALTER TABLE dbo.Vouchers ALTER COLUMN MinOrderValue DECIMAL(18,2) NOT NULL;
    ALTER TABLE dbo.Vouchers ALTER COLUMN EndDate DATETIME2 NOT NULL;

    UPDATE dbo.Vouchers
    SET MaxDiscountAmount = NULL
    WHERE VoucherType = 'FIXED';

    ALTER TABLE dbo.Vouchers
        DROP COLUMN StartDate, UsedCount, MaxUsedCount, [Status], Description;

    ALTER TABLE dbo.Vouchers
        ADD CONSTRAINT UQ_Vouchers_Code UNIQUE (VoucherCode);

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_Code_Format CHECK (
            LEN(VoucherCode) BETWEEN 3 AND 30
            AND VoucherCode COLLATE Latin1_General_100_BIN2
                NOT LIKE '%[^A-Z0-9_-]%'
            AND VoucherCode COLLATE Latin1_General_100_BIN2
                = UPPER(VoucherCode) COLLATE Latin1_General_100_BIN2
        );

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_Type CHECK (
            VoucherType IN ('PERCENT', 'FIXED')
        );

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

    ALTER TABLE dbo.Vouchers WITH CHECK
        ADD CONSTRAINT CK_Vouchers_FixedMinOrder CHECK (
            VoucherType <> 'FIXED' OR MinOrderValue >= VoucherValue
        );

    ALTER TABLE dbo.Vouchers WITH CHECK CHECK CONSTRAINT ALL;

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
