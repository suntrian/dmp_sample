package com.quantchi.extractor;

import com.quantchi.extractor.JDBCExtractor;
import com.quantchi.extractor.MetaDataExtractor;
import com.quantchi.extractor.datasource.DatasourceResolver;
import com.quantchi.extractor.datasource.DriverType;
import com.quantchi.extractor.entity.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.quantchi.extractor.datasource.DriverType.*;

public class TestExtractMetaData {

  @Test
  public void testJudgeDriver() {
    DriverType driver = DriverType.judgeDriver("jdbc:oracle:thin:@192.168.2.60:1521:oradb");
    assert driver== DriverType.ORACLE;
  }

  @Test
  public void testExtractMysqlMetaData() throws SQLException, ClassNotFoundException {


    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection(DriverType.MYSQL, "192.168.2.52", null, null, "liangzhi","liangzhi123" ));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables("dmp_test", null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns("dmp_test", null, null, null);


    int index = 0, limit = 2;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_CATALOG,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }
    //TABLE_CAT : dmp_test
    //TABLE_SCHEM : null
    //TABLE_NAME : cm_code_system_conf
    //TABLE_TYPE : TABLE
    //REMARKS :
    //TYPE_CAT : null
    //TYPE_SCHEM : null
    //TYPE_NAME : null
    //SELF_REFERENCING_COL_NAME : null
    //REF_GENERATION : null

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append(columns.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN,
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
    //TABLE_CAT : dmp_test
    //TABLE_SCHEM : null
    //TABLE_NAME : cm_code_system_conf
    //COLUMN_NAME : id
    //DATA_TYPE : 4
    //TYPE_NAME : INT
    //COLUMN_SIZE : 10
    //BUFFER_LENGTH : 65535
    //DECIMAL_DIGITS : null
    //NUM_PREC_RADIX : 10
    //NULLABLE : 0
    //REMARKS : 系统自增Id
    //COLUMN_DEF : null
    //SQL_DATA_TYPE : 0
    //SQL_DATETIME_SUB : 0
    //CHAR_OCTET_LENGTH : null
    //ORDINAL_POSITION : 1
    //IS_NULLABLE : NO
    //SCOPE_CATALOG : null
    //SCOPE_SCHEMA : null
    //SCOPE_TABLE : null
    //SOURCE_DATA_TYPE : null
    //IS_AUTOINCREMENT : YES
    //IS_GENERATEDCOLUMN : NO
  }

  @Test
  public void testExtractMysqlMetaData2() throws SQLException, ClassNotFoundException {
    //List<RawColumn> columns1 = new JDBCExtractor(DriverType.MYSQL, "localhost",3306, "ngo", "root","abc123").extractColumn();
    JDBCExtractor extractor = new JDBCExtractor(DriverType.MYSQL, "localhost",3306, null, "root","abc123");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable();

    databases.stream().limit(12).forEach(System.out::println);
    tables.stream().limit(50).forEach(System.out::println);

    List<RawColumn> columns2 = extractor.extractColumn("ngo",null, null, null);
    List<RawPrimaryKey> primaryKeys = columns2.stream().map(RawColumn::getTABLE_NAME).distinct().map((String tableName) -> {
      try {
        return extractor.extractPrimaryKey(tableName);
      } catch (SQLException e) {
        return new ArrayList<RawPrimaryKey>();
      }
    }).reduce((List<RawPrimaryKey> l,List<RawPrimaryKey> m)->{l.addAll(m); return l;}).orElse(Collections.emptyList());

    List<RawIndexInfo> indexInfos = columns2.stream().map(RawColumn::getTABLE_NAME).distinct().map((table)->{
      try {
        return extractor.extractIndexInfo(table, true, false);
      } catch (SQLException e) {
        return new ArrayList<RawIndexInfo>();
      }
    }).reduce((l,m)->{l.addAll(m); return l;}).orElse(Collections.emptyList());

    //Assert.assertTrue(columns1.size()>0);
    Assert.assertTrue(columns2.size()>0);
    //Assert.assertEquals(columns1.size(), columns2.size());
    columns2.stream().limit(80).forEach(System.out::println);
    System.out.println();
    primaryKeys.stream().limit(20).forEach(System.out::println);
    System.out.println();
    indexInfos.stream().limit(20).forEach(System.out::println);

  }

  @Test
  public void testExtractOracleMetaData() throws SQLException, ClassNotFoundException {
    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection("jdbc:oracle:thin:@192.168.2.60:1521:oradb", "ctuser","ctuser"));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns(null, null, null, null);


    int index = 0, limit = 2;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }
    //TABLE_CAT : null
    //TABLE_SCHEM : APEX_030200
    //TABLE_NAME : SYS_IOT_OVER_70794
    //TABLE_TYPE : TABLE
    //REMARKS : null

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append(columns.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE,
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
    //TABLE_CAT : null
    //TABLE_SCHEM : APEX_030200
    //TABLE_NAME : APEX_APPLICATIONS
    //COLUMN_NAME : WORKSPACE
    //DATA_TYPE : 12
    //TYPE_NAME : VARCHAR2
    //COLUMN_SIZE : 255
    //BUFFER_LENGTH : 0
    //DECIMAL_DIGITS : null
    //NUM_PREC_RADIX : 10
    //NULLABLE : 0
    //REMARKS : A work area mapped to one or more database schemas
    //COLUMN_DEF : null
    //SQL_DATA_TYPE : 0
    //SQL_DATETIME_SUB : 0
    //CHAR_OCTET_LENGTH : 255
    //ORDINAL_POSITION : 1
    //IS_NULLABLE : NO
  }

  @Test
  public void testExtractOracleMetaData2() throws SQLException, ClassNotFoundException {
    //jdbc:oracle:thin:@192.168.2.60:1521:oradb
    JDBCExtractor extractor = new JDBCExtractor("jdbc:oracle:thin:@192.168.2.60:1521:oradb","ctuser","ctuser");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable();
    List<RawColumn> columns = extractor.extractColumn();

    databases.stream().limit(20).forEach(System.out::println);
    tables.stream().limit(100).forEach(System.out::println);
    columns.stream().limit(100).forEach(System.out::println);

  }

  @Test
  public void testExtractHiveMetaData() throws SQLException, ClassNotFoundException {
    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection(HIVE, "192.168.2.59",10000,null, "liangzhi","liangzhi123"));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns(null, null, null, null);


    int index = 0, limit = 50;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_CATALOG,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //table_cat, table_schem, table_name, table_type, remarks,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append("(").append(columns.getMetaData().getColumnName(i+1)).append(":").append(columns.getMetaData().getColumnLabel(i+1)).append("), ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATLOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT,
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
    //TABLE_CAT : null
    //TABLE_SCHEM : cashclient
    //TABLE_NAME : cashclient_asset
    //COLUMN_NAME : init_date
    //DATA_TYPE : 4
    //TYPE_NAME : INT
    //COLUMN_SIZE : 10
    //BUFFER_LENGTH : null
    //DECIMAL_DIGITS : 0
    //NUM_PREC_RADIX : 10
    //NULLABLE : 1
    //REMARKS : null
    //COLUMN_DEF : null
    //SQL_DATA_TYPE : null
    //SQL_DATETIME_SUB : null
    //CHAR_OCTET_LENGTH : null
    //ORDINAL_POSITION : 1
    //IS_NULLABLE : YES
    //SCOPE_CATALOG : null
    //SCOPE_SCHEMA : null
    //SCOPE_TABLE : null
    //SOURCE_DATA_TYPE : null
    //IS_AUTO_INCREMENT : NO
  }

  @Test
  public void testExtractHiveMetaData2() throws SQLException, ClassNotFoundException {
    JDBCExtractor extractor = new JDBCExtractor(HIVE,"192.168.2.59",0, null,"liangzhi","liangzhi123");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable();
    List<RawColumn> columns = extractor.extractColumn();

    databases.stream().limit(10).forEach(System.out::println);
    tables.stream().limit(10).forEach(System.out::println);
    columns.stream().limit(100).forEach(System.out::println);
  }

  @Test
  public void testExtractSqlServerMetaData() throws SQLException, ClassNotFoundException {
    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection(SQLSERVER, "192.168.2.60",0,"dmp", "sa","liangzhi123"));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns(null, null, null, null);


    int index = 0, limit = 2;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_CATALOG,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append(columns.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    //TABLE_CAT
    //TABLE_SCHEM
    //TABLE_NAME
    //COLUMN_NAME
    //DATA_TYPE
    //TYPE_NAME
    //COLUMN_SIZE
    //BUFFER_LENGTH
    //DECIMAL_DIGITS
    //NUM_PREC_RADIX
    //NULLABLE
    //REMARKS
    //COLUMN_DEF
    //SQL_DATA_TYPE
    //SQL_DATETIME_SUB
    //CHAR_OCTET_LENGTH
    //ORDINAL_POSITION
    //IS_NULLABLE
    //SS_IS_SPARSE
    //SS_IS_COLUMN_SET
    //IS_GENERATEDCOLUMN
    //IS_AUTOINCREMENT
    //SS_UDT_CATALOG_NAME
    //SS_UDT_SCHEMA_NAME
    //SS_UDT_ASSEMBLY_TYPE_NAME
    //SS_XML_SCHEMACOLLECTION_CATALOG_NAME
    //SS_XML_SCHEMACOLLECTION_SCHEMA_NAME
    //SS_XML_SCHEMACOLLECTION_NAME
    //SS_DATA_TYPE
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
  }


  @Test
  public void testExtractSqlServerMetaData2() throws SQLException, ClassNotFoundException {
    JDBCExtractor extractor = new JDBCExtractor(SQLSERVER,"192.168.2.60",0, "dmp","sa","liangzhi123");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable();
    List<RawColumn> columns = extractor.extractColumn();

    databases.stream().limit(10).forEach(System.out::println);
    tables.stream().limit(10).forEach(System.out::println);
    columns.stream().limit(100).forEach(System.out::println);
  }


  @Test
  public void testExtractPostgreSqlMetaData() throws SQLException, ClassNotFoundException {
    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection(POSTGRESQL, "192.168.2.60",0,"dmp", "postgres","liangzhi123"));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns(null, null, null, null);


    int index = 0, limit = 2;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, table_schem, table_catalog,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append(columns.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
  }

  @Test
  public void testExtractPostgreSqlMetaData2() throws SQLException, ClassNotFoundException {
    JDBCExtractor extractor = new JDBCExtractor(POSTGRESQL, "192.168.2.60",0,"dmp", "postgres", "liangzhi123");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable(null,null,null);
    List<RawColumn> columns = extractor.extractColumn(null,null, null, null);

    databases.stream()/*.limit(10)*/.forEach(System.out::println);
    tables.stream()/*.limit(50)*/.forEach(System.out::println);
    columns.stream()/*.limit(100)*/.forEach(System.out::println);
  }



  @Test
  public void testExtractDb2MetaData() throws SQLException, ClassNotFoundException {
    DatabaseMetaData metaData = DatasourceResolver.getDatabaseMetaData(DatasourceResolver.getConnection(DB2, "192.168.2.60",0,"sample", "db2admin","liangzhi123"));
    ResultSet schemas = metaData.getSchemas();
    ResultSet catalogs = metaData.getCatalogs();
    ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
    ResultSet columns = metaData.getColumns(null, null, null, null);


    int index = 0, limit = 2;
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    for (int i = 0; i < catalogs.getMetaData().getColumnCount();i++){
      builder.append(catalogs.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("catalog:");
    System.out.println(builder.toString());
    //TABLE_CAT,
    System.out.println("catalog data:");
    while (catalogs.next() && index++<limit){
      for (int i = 0; i < catalogs.getMetaData().getColumnCount(); i++) {
        System.out.println(catalogs.getMetaData().getColumnName(i+1) + " : " + catalogs.getObject(i+1));
      }
    }

    for (int i = 0; i < schemas.getMetaData().getColumnCount();i++){
      builder.append(schemas.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("schema:");
    System.out.println(builder.toString());
    //TABLE_CAT, table_schem, table_catalog,
    index = 0;
    System.out.println("schema data:");
    while (schemas.next() && ++index<limit){
      for (int i = 0; i < schemas.getMetaData().getColumnCount(); i++) {
        System.out.println(schemas.getMetaData().getColumnName(i+1) + " : " + schemas.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < tables.getMetaData().getColumnCount();i++){
      builder.append(tables.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("table:");
    System.out.println(builder.toString());
    //TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION,
    index = 0;
    System.out.println("table data:");
    while (tables.next() && ++index<limit){
      for (int i = 0; i < tables.getMetaData().getColumnCount(); i++) {
        System.out.println(tables.getMetaData().getColumnName(i+1) + " : " + tables.getObject(i+1));
      }
    }

    builder.setLength(0);
    for (int i = 0; i < columns.getMetaData().getColumnCount();i++){
      builder.append(columns.getMetaData().getColumnName(i+1)).append(", ");
    }
    System.out.println("column:");
    System.out.println(builder.toString());
    index = 0;
    System.out.println("column data:");
    while (columns.next() && ++index<limit){
      for (int i = 0; i < columns.getMetaData().getColumnCount(); i++) {
        System.out.println(columns.getMetaData().getColumnName(i+1) + " : " + columns.getObject(i+1));
      }
    }
  }

  @Test
  public void testExtractDb2MetaData2() throws SQLException, ClassNotFoundException {
    JDBCExtractor extractor = new JDBCExtractor(DB2, "192.168.2.60",0,"sample", "db2admin", "liangzhi123");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable(null,null,null);
    List<RawColumn> columns = extractor.extractColumn(null,null, null, null);

    databases.stream()/*.limit(10)*/.forEach(System.out::println);
    tables.stream()/*.limit(50)*/.forEach(System.out::println);
    columns.stream()/*.limit(100)*/.forEach(System.out::println);
  }

  @Test
  public void testExtractOracleBySql() throws SQLException, ClassNotFoundException {
    JDBCExtractor extractor = new JDBCExtractor(ORACLE, "192.168.2.60", 0, "oradb","ctuser","ctuser");
    List<Map<String, Object>> list = extractor.extractMap("select table_name as tableName ,'oracleTable' as tableType from all_tables");
    list.forEach(System.out::println);
  }

  @Test
  public void testExtractImpalaBySql() throws SQLException, ClassNotFoundException {
    MetaDataExtractor extractor = new JDBCExtractor(IMPALA, "192.168.2.51", 0, "default", "root","root");
    List<RawDatabase> databases = extractor.extractDatabase();
    List<RawTable> tables = extractor.extractTable(null, null, null);
    Assert.assertTrue(databases.size()>0);
  }
}
