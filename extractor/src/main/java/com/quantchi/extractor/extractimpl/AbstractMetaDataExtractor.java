package com.quantchi.extractor.extractimpl;

import com.quantchi.extractor.entity.*;
import com.quantchi.extractor.parser.*;

import org.jetbrains.annotations.Nullable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractMetaDataExtractor {


  public static List<RawDatabase> extractDatabase(DatabaseMetaData metaData) throws SQLException {
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    ResultSet resultSet = resultSetParser.decideDatabase(metaData);
    return resultSetParser.parseToDatabase(resultSet);
  }


  /**
   * TABLE_CAT
   * TABLE_SCHEM
   * TABLE_NAME
   * TABLE_TYPE
   * REMARKS
   * TYPE_CAT
   * TYPE_SCHEM
   * TYPE_NAME
   * SELF_REFERENCING_COL_NAME
   * REF_GENERATION
   *
   * @param metaData
   * @param catalog a catalog name; must match the catalog name as it
   *        is stored in the database; "" retrieves those without a catalog;
   *        <code>null</code> means that the catalog name should not be used to narrow
   *        the search
   * @param schemaPattern a schema name pattern; must match the schema name
   *        as it is stored in the database; "" retrieves those without a schema;
   *        <code>null</code> means that the schema name should not be used to narrow
   *        the search
   * @param tableNamePattern a table name pattern; must match the
   *        table name as it is stored in the database
   * @param types a list of table types, which must be from the list of table types
   *         returned from {@link DatabaseMetaData#getTableTypes},to include; <code>null</code> returns
   * all types
   * @return
   */
  @SuppressWarnings("all")
  public static List<RawTable> extractTable(DatabaseMetaData metaData,
                                            @Nullable String catalog,
                                            @Nullable String schemaPattern,
                                            @Nullable String tableNamePattern,
                                            @Nullable String... types) throws SQLException {
    ResultSet resultSet = metaData.getTables(catalog, schemaPattern, tableNamePattern, types);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToTable(resultSet);
  }

  /**
   *
   * TABLE_CAT
   * TABLE_SCHEM
   * TABLE_NAME
   * COLUMN_NAME
   * DATA_TYPE
   * TYPE_NAME
   * COLUMN_SIZE
   * BUFFER_LENGTH
   * DECIMAL_DIGITS
   * NUM_PREC_RADIX
   * NULLABLE
   * REMARKS
   * COLUMN_DEF
   * SQL_DATA_TYPE
   * SQL_DATETIME_SUB
   * CHAR_OCTET_LENGTH
   * ORDINAL_POSITION
   * IS_NULLABLE
   * SCOPE_CATALOG
   * SCOPE_SCHEMA
   * SCOPE_TABLE
   * SOURCE_DATA_TYPE
   * IS_AUTOINCREMENT
   * IS_GENERATEDCOLUMN
   * @param metaData
   * @param catalog a catalog name; must match the catalog name as it
   *        is stored in the database; "" retrieves those without a catalog;
   *        <code>null</code> means that the catalog name should not be used to narrow
   *        the search
   * @param schemaPattern a schema name pattern; must match the schema name
   *        as it is stored in the database; "" retrieves those without a schema;
   *        <code>null</code> means that the schema name should not be used to narrow
   *        the search
   * @param tableNamePattern a table name pattern; must match the
   *        table name as it is stored in the database
   * @param columnNamePattern a column name pattern; must match the column
   *        name as it is stored in the database
   * @return
   */
  public static List<RawColumn> extractColumn(DatabaseMetaData metaData,
                                              @Nullable String catalog,
                                              @Nullable String schemaPattern,
                                              @Nullable String tableNamePattern,
                                              @Nullable String columnNamePattern) throws SQLException {
    ResultSet resultSet = metaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToColumn(resultSet);
  }

  /**
   *
   * @param catalog a catalog name; must match the catalog name as it
   *        is stored in the database; "" retrieves those without a catalog;
   *        <code>null</code> means that the catalog name should not be used to narrow
   *        the search
   * @param schema a schema name; must match the schema name
   *        as it is stored in the database; "" retrieves those without a schema;
   *        <code>null</code> means that the schema name should not be used to narrow
   *        the search
   * @param tableName a table name; must match the table name as it is stored
   *        in the database
   * @return
   * @throws SQLException
   */
  public static List<RawPrimaryKey> extractPrimaryKey(DatabaseMetaData metaData,
                                                      @Nullable String catalog,
                                                      @Nullable String schema,
                                                      String tableName) throws SQLException {
    ResultSet resultSet = metaData.getPrimaryKeys(catalog, schema, tableName);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToPrimaryKey(resultSet);
  }

  /**
   *
   * @param catalog a catalog name; must match the catalog name as it
   *        is stored in this database; "" retrieves those without a catalog;
   *        <code>null</code> means that the catalog name should not be used to narrow
   *        the search
   * @param schema a schema name; must match the schema name
   *        as it is stored in this database; "" retrieves those without a schema;
   *        <code>null</code> means that the schema name should not be used to narrow
   *        the search
   * @param table a table name; must match the table name as it is stored
   *        in this database
   * @param unique when true, return only indices for unique values;
   *     when false, return indices regardless of whether unique or not
   * @param approximate when true, result is allowed to reflect approximate
   *     or out of data values; when false, results are requested to be
   *     accurate
   * @return
   * @throws SQLException
   */
  public static List<RawIndexInfo> extractIndexInfo(DatabaseMetaData metaData,
                                                    @Nullable String catalog,
                                                    @Nullable String schema,
                                                    String table,
                                                    boolean unique,
                                                    boolean approximate) throws SQLException {
    ResultSet resultSet = metaData.getIndexInfo(catalog, schema, table, unique, approximate);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToIndexInfo(resultSet);
  }

  public static List<RawForeignKeyInfo> extractExportedKey(DatabaseMetaData metaData,
                                                           @Nullable String catalog,
                                                           @Nullable String schema,
                                                           String table) throws SQLException {
    ResultSet resultSet = metaData.getExportedKeys(catalog, schema, table);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToForeignKeyInfo(resultSet);
  }

  public static List<RawForeignKeyInfo> extractImportedKey(DatabaseMetaData metaData,
                                                           @Nullable String catalog,
                                                           @Nullable String schema,
                                                           String table) throws SQLException {
    ResultSet resultSet = metaData.getImportedKeys(catalog, schema, table);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToForeignKeyInfo(resultSet);
  }


  public static List<RawProcedure> extractPro(DatabaseMetaData metaData,
                                        @Nullable String catalog,
                                        @Nullable String schemaPattern,
                                        @Nullable String procedureNamePattern) throws SQLException {
    ResultSet resultSet = metaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
    MetaDataResultSetParser resultSetParser = determineResultSetParser(metaData);
    return resultSetParser.parseToProcedure(resultSet);
  }

  protected static MetaDataResultSetParser determineResultSetParser(DatabaseMetaData metaData) throws SQLException {
    String productName = metaData.getDatabaseProductName();
    if (productName.toLowerCase().contains("hive")){
      return HiveMetaDataResultSetParser.getInstance();
    } else if (productName.toLowerCase().contains("db2")){
      return DB2MetaDataResultParser.getInstance();
    }
    switch (productName){
      case "MySQL":
        return MySqlMetaDataResultSetParser.getInstance();
      case "Oracle":
        return OracleMetaDataResultSetParser.getInstance();
      case "PostgreSQL":
        return PostgresMetaDataResultSetParser.getInstance();
      case "Microsoft SQL Server":
        return SQLServerMetaDataResultSetParser.getInstance();
      case "Impala":
        return HiveMetaDataResultSetParser.getInstance();
      case "DB2/NT64":
      case "z/OS":
      case "SQLDS":
      case "iSeries":
      case "Cloudscape":
      case "Informix":
        return DB2MetaDataResultParser.getInstance();
    }
    return DefaultMetaDataResultSetParser.getInstance();
  }
}
