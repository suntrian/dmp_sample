package com.quantchi.extractor.entity;

import lombok.Data;

/**
 * @see java.sql.DatabaseMetaData#getPrimaryKeys(String, String, String)
 */
@Data
public class RawPrimaryKey {

  private String TABLE_CAT;
  private String TABLE_SCHEM;
  private String TABLE_NAME;
  private String COLUMN_NAME;
  private Short KEY_SEQ;
  private String PK_NAME;

}
