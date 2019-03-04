package com.quantchi.extractor.entity;

import lombok.Data;

/**
 * @see java.sql.DatabaseMetaData#getIndexInfo(String, String, String, boolean, boolean)
 */
@Data
public class RawIndexInfo {

  private String TABLE_CAT;
  private String TABLE_SCHEM;
  private String TABLE_NAME;
  private Boolean NON_UNIQUE;
  private String INDEX_QUALIFIER;
  private String INDEX_NAME;
  // tableIndexStatistic - this identifies table statistics that are
  // returned in conjuction with a table's index descriptions
  // tableIndexClustered - this is a clustered index
  // tableIndexHashed - this is a hashed index
  // tableIndexOther - this is some other style of index
  private Short TYPE;
  private Short ORDINAL_POSITION;
  private String COLUMN_NAME;
  private String ASC_OR_DESC;
  private Long CARDINALITY;
  private Long PAGES;
  private String FILTER_CONDITION;

}
