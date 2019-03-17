package com.quantchi.extractor.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
 */
@Data
public class RawTable implements Serializable {

  public static final String TYPE_VIEW = "VIEW";
  public static final String TYPE_TABLE = "TABLE";
  public static final String TYPE_SYSTEM = "SYSTEM_TABLE";
  public static final String TYPE_GLOBAL_TEMP = "GLOBAL TEMPORARY";
  public static final String TYPE_LOCAL_TEMP = "LOCAL TEMPORARY";
  public static final String TYPE_ALIAS = "ALIAS";
  public static final String TYPE_SYNONYM = "SYNONYM";

  private String TABLE_CAT;
  private String TABLE_SCHEM;
  private String TABLE_NAME;
  private String TABLE_TYPE;       //"TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM"
  private String REMARKS;
  private String TYPE_CAT;
  private String TYPE_SCHEM;
  private String TYPE_NAME;
  private String SELF_REFERENCING_COL_NAME;
  private String REF_GENERATION;



}
