package com.quantchi.extractor.parser;

import com.quantchi.extractor.datasource.DatasourceResolver;
import com.quantchi.extractor.entity.RawColumn;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("Duplicates")
public final class SQLServerMetaDataResultSetParser extends DefaultMetaDataResultSetParser {

    private static final Set<String> SYS_CAT = new HashSet<>(Arrays.asList(
            "master",
            "model",
            "msdb",
            "tempdb"
    ));

    private static final Set<String> SYS_SCHEMA = new HashSet<>(Arrays.asList(
            //"dbo",
            "guest",
            "information_schema",
            "sys",
            "db_owner",
            "db_accessadmin",
            "db_securityadmin",
            "db_ddladmin",
            "db_backupoperator",
            "db_datareader",
            "db_datawriter",
            "db_denydatareader",
            "db_denydatawriter"
    ));

    private static final Set<String> SYS_TABLE = Collections.singleton("sysdiagrams");

    private SQLServerMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (SQLServerMetaDataResultSetParser.class){
                if (instance == null){
                    return new SQLServerMetaDataResultSetParser();
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
        return SYS_SCHEMA.contains(schemName);
    }

    @Override
    protected boolean isSysTable(String tabName) {
        return SYS_TABLE.contains(tabName);
    }

    @Override
    public ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException {
        return metaData.getCatalogs();
    }

    /**
     *  TABLE_CAT
     *  TABLE_SCHEM
     *  TABLE_NAME
     *  COLUMN_NAME
     *  DATA_TYPE
     *  TYPE_NAME
     *  COLUMN_SIZE
     *  BUFFER_LENGTH
     *  DECIMAL_DIGITS
     *  NUM_PREC_RADIX
     *  NULLABLE
     *  REMARKS
     *  COLUMN_DEF
     *  SQL_DATA_TYPE
     *  SQL_DATETIME_SUB
     *  CHAR_OCTET_LENGTH
     *  ORDINAL_POSITION
     *  IS_NULLABLE
     *  SS_IS_SPARSE
     *  SS_IS_COLUMN_SET
     *  IS_GENERATEDCOLUMN
     *  IS_AUTOINCREMENT
     *  SS_UDT_CATALOG_NAME
     *  SS_UDT_SCHEMA_NAME
     *  SS_UDT_ASSEMBLY_TYPE_NAME
     *  SS_XML_SCHEMACOLLECTION_CATALOG_NAME
     *  SS_XML_SCHEMACOLLECTION_SCHEMA_NAME
     *  SS_XML_SCHEMACOLLECTION_NAME
     *  SS_DATA_TYPE
     * @param resultSet
     * @return
     * @throws SQLException
     */
    @Override
    public List<RawColumn> parseToColumn(ResultSet resultSet) throws SQLException {
        List<RawColumn> rawColumns = new LinkedList<>();
        while (resultSet.next()){
            RawColumn column = new RawColumn();
            column.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            if (isSysCat(column.getTABLE_CAT())){
                continue;
            }
            column.setTABLE_SCHEM(resultSet.getString(2 /*"TABLE_SCHEM"*/));
            if (isSysSchema(column.getTABLE_SCHEM())){
                continue;
            }
            column.setTABLE_NAME(resultSet.getString(3 /*"TABLE_NAME"*/));
            if (isSysTable(column.getTABLE_NAME())){
                continue;
            }
            column.setCOLUMN_NAME(resultSet.getString(4 /*"COLUMN_NAME"*/));
            column.setDATA_TYPE(resultSet.getInt(5 /*"DATA_TYPE"*/));
            column.setTYPE_NAME(resultSet.getString(6 /*"TYPE_NAME"*/));
            column.setCOLUMN_SIZE(resultSet.getInt(7 /*"COLUMN_SIZE"*/));
            //column.setBUFFER_LENGTH(resultSet.getInt(8 /*"BUFFER_LENGTH"*/));
            column.setDECIMAL_DIGITS(resultSet.getInt(9 /*"DECIMAL_DIGITS"*/));
            column.setNUM_PREC_RADIX(resultSet.getInt(10 /*"NUM_PREC_RADIX"*/));
            column.setNULLABLE(resultSet.getInt(11 /*"NULLABLE"*/));
            column.setREMARKS(resultSet.getString(12 /*"REMARKS"*/));
            column.setCOLUMN_DEF(resultSet.getString(13 /*"COLUMN_DEF"*/));
            //column.setSQL_DATA_TYPE(resultSet.getInt(14 /*"SQL_DATA_TYPE"*/));
            //column.setSQL_DATETIME_SUB(resultSet.getInt(15 /*"SQL_DATETIME_SUB"*/));
            column.setCHAR_OCTET_LENGTH(resultSet.getInt(16 /*"CHAR_OCTET_LENGTH"*/));
            column.setORDINAL_POSITION(resultSet.getInt(17 /*"ORDINAL_POSITION"*/));
            column.setIS_NULLABLE(resultSet.getString(18 /*"IS_NULLABLE"*/));
            column.setIS_AUTOINCREMENT(resultSet.getString(22 /*IS_AUTO_INCREMENT*/));
            column.setIS_GENERATEDCOLUMN(resultSet.getString(21 /*"IS_GENERATEDCOLUMN"*/));
            rawColumns.add(column);
        }
        DatasourceResolver.close(resultSet);
        return rawColumns;
    }
}
