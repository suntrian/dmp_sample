package com.quantchi.extractor.entity;

import lombok.Data;

/**
 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
 */
@Data
public class RawTable {

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
