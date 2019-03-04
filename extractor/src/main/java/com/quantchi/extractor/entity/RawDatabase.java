package com.quantchi.extractor.entity;

import lombok.Data;

import java.sql.DatabaseMetaData;

/**
 * @see DatabaseMetaData#getCatalogs()
 * @see DatabaseMetaData#getSchemas()
 */
@Data
public class RawDatabase {
  private String TABLE_CAT;
  private String TABLE_SCHEM;
}
