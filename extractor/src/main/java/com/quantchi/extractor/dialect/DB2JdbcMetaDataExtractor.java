package com.quantchi.extractor.dialect;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DB2JdbcMetaDataExtractor extends DefaultJdbcMetaDataExtractor {

  private final static Set<String> SYS_CAT = new HashSet<>(Arrays.asList(
          "administrator",
          "nullid",
          "sqlj",
          "syscat",
          "sysfun",
          "sysibm",
          "sysibmadm",
          "sysibminternal",
          "sysibmts",
          "sysproc",
          "syspublic",
          "sysstat",
          "systools"));

  private DB2JdbcMetaDataExtractor(){}

  public static JdbcMetaDataExtractor newInstance(){
    return new DB2JdbcMetaDataExtractor();
  }

  @Override
  protected boolean isSysCatalog(String catalogName) {
    return catalogName!=null && SYS_CAT.contains(catalogName.toLowerCase());
  }

  @Override
  protected boolean isSysSchema(String schemaName) {
    return schemaName!=null && SYS_CAT.contains(schemaName.toLowerCase());
  }

  @Override
  protected boolean isSysTable(String tableName) {
    return false;
  }

  @Override
  protected ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException {
    return metaData.getSchemas();
  }

}
