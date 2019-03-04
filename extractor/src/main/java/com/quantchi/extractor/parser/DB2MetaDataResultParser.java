package com.quantchi.extractor.parser;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DB2MetaDataResultParser extends DefaultMetaDataResultSetParser {

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

  private DB2MetaDataResultParser(){}

  public static MetaDataResultSetParser getInstance(){
    if (instance == null){
      synchronized (DB2MetaDataResultParser.class){
        if (instance == null){
          return new DB2MetaDataResultParser();
        }
      }
    }
    return instance;
  }

  @Override
  protected boolean isSysCat(String catName) {
    return SYS_CAT.contains(catName);
  }

  @Override
  protected boolean isSysSchema(String schemName) {
    return SYS_CAT.contains(schemName);
  }

  @Override
  protected boolean isSysTable(String tabName) {
    return false;
  }

  @Override
  public ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException {
    return metaData.getSchemas();
  }

}
