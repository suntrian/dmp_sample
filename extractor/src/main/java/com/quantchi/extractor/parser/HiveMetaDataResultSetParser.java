package com.quantchi.extractor.parser;

import com.quantchi.extractor.datasource.DatasourceResolver;
import com.quantchi.extractor.entity.RawColumn;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("all")
public class HiveMetaDataResultSetParser extends DefaultMetaDataResultSetParser {

    private HiveMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (HiveMetaDataResultSetParser.class){
                if (instance == null){
                    return new HiveMetaDataResultSetParser();
                }
            }
        }
        return instance;
    }

    @Override
    protected boolean isSysCat(String catName) {
        return false;
    }

    @Override
    protected boolean isSysSchema(String schemName) {
        return false;
    }

    @Override
    protected boolean isSysTable(String tabName) {
        return false;
    }

    @Override
    public ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException {
        return metaData.getCatalogs();
    }

    @Override
    public List<RawColumn> parseToColumn(ResultSet resultSet) throws SQLException {
        List<RawColumn> rawColumns = new LinkedList<>();
        while (resultSet.next()){
            RawColumn column = new RawColumn();
            rawColumns.add(column);
            column.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            column.setTABLE_SCHEM(resultSet.getString(2 /*"TABLE_SCHEM"*/));
            column.setTABLE_NAME(resultSet.getString(3 /*"TABLE_NAME"*/));
            column.setCOLUMN_NAME(resultSet.getString(4 /*"COLUMN_NAME"*/));
            column.setDATA_TYPE(resultSet.getInt(5 /*"DATA_TYPE"*/));
            column.setTYPE_NAME(resultSet.getString(6 /*"TYPE_NAME"*/));
            column.setCOLUMN_SIZE(resultSet.getInt(7 /*"COLUMN_SIZE"*/));
            column.setBUFFER_LENGTH(resultSet.getInt(8 /*"BUFFER_LENGTH"*/));
            column.setDECIMAL_DIGITS(resultSet.getInt(9 /*"DECIMAL_DIGITS"*/));
            column.setNUM_PREC_RADIX(resultSet.getInt(10 /*"NUM_PREC_RADIX"*/));
            column.setNULLABLE(resultSet.getInt(11 /*"NULLABLE"*/));
            column.setREMARKS(resultSet.getString(12 /*"REMARKS"*/));
            column.setCOLUMN_DEF(resultSet.getString(13 /*"COLUMN_DEF"*/));
            column.setSQL_DATA_TYPE(resultSet.getInt(14 /*"SQL_DATA_TYPE"*/));
            column.setSQL_DATETIME_SUB(resultSet.getInt(15 /*"SQL_DATETIME_SUB"*/));
            column.setCHAR_OCTET_LENGTH(resultSet.getInt(16 /*"CHAR_OCTET_LENGTH"*/));
            column.setORDINAL_POSITION(resultSet.getInt(17 /*"ORDINAL_POSITION"*/));
            column.setIS_NULLABLE(resultSet.getString(18 /*"IS_NULLABLE"*/));
            column.setSCOPE_CATALOG(resultSet.getString(19 /*"SCOPE_CATALOG"*/));
            column.setSCOPE_SCHEMA(resultSet.getString(20 /*"SCOPE_SCHEMA"*/));
            column.setSCOPE_TABLE(resultSet.getString(21 /*"SCOPE_TABLE"*/));
            column.setSOURCE_DATA_TYPE(resultSet.getShort(22 /*"SOURCE_DATA_TYPE"*/));
            column.setIS_AUTOINCREMENT(resultSet.getString(23 /*"IS_AUTO_INCREMENT"*/));
        }
        DatasourceResolver.close(resultSet);
        return rawColumns;
    }
}
