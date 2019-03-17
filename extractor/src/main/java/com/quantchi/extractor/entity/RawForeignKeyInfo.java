package com.quantchi.extractor.entity;

import lombok.Data;

/**
 * @see java.sql.DatabaseMetaData#getExportedKeys(String, String, String)
 * @see java.sql.DatabaseMetaData#getImportedKeys(String, String, String)
 */
@Data
public class RawForeignKeyInfo {

    public enum Rule{
        UNKNOWN ("UNKNOWN", 99),
        CASCADE("CASCADE", 0),
        RESTRICT("RESTRICT", 1),
        SET_NULL("SET_NULL", 2),
        NO_ACTION( "NO_ACTION", 3),
        SET_DEFAULT("SET_DEFAULT", 4);
        private String name;
        private Integer code;

        Rule(String name, Integer code) {
            this.name = name;
            this.code = code;
        }

        public static Rule of(int rule){
            for (Rule r: Rule.values()){
                if (r.code == rule){
                    return r;
                }
            }
            return UNKNOWN;
        }
    }

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
