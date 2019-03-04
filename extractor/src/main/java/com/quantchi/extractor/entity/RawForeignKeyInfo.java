package com.quantchi.extractor.entity;

import lombok.Data;

/**
 * @see java.sql.DatabaseMetaData#getExportedKeys(String, String, String)
 * @see java.sql.DatabaseMetaData#getImportedKeys(String, String, String)
 */
@Data
public class RawForeignKeyInfo {

    private String PKTABLE_CAT;
    private String PKTABLE_SCHEM;
    private String PKTABLE_NAME;
    private String PKCOLUMN_NAME;
    private String FKTABLE_CAT;
    private String FKTABLE_SCHEM;
    private String FKTABLE_NAME;
    private String FKCOLUMN_NAME;
    private Short  KEY_SEQ;
    private Short  UPDATE_RULE;
    private Short  DELETE_RULE;
    private String FK_NAME;
    private String PK_NAME;

}
