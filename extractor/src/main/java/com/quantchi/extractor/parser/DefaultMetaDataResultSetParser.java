package com.quantchi.extractor.parser;

import com.quantchi.extractor.datasource.DatasourceResolver;
import com.quantchi.extractor.entity.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("Duplicates")
public abstract class DefaultMetaDataResultSetParser implements MetaDataResultSetParser {

    public static class DefaultMetaDataResultSetParserImpl extends DefaultMetaDataResultSetParser{
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
    }

    protected static MetaDataResultSetParser instance;

    protected DefaultMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (DefaultMetaDataResultSetParser.class){
                if (instance == null){
                    return new DefaultMetaDataResultSetParserImpl();
                }
            }
        }
        return instance;
    }

    protected abstract boolean isSysCat(String catName);

    protected abstract boolean isSysSchema(String schemaName);

    protected abstract boolean isSysTable(String tabName);

    @Override
    public abstract ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException ;

    @Override
    public List<RawDatabase> parseToDatabase(ResultSet resultSet) throws SQLException {
        List<RawDatabase> rawDatabases = new LinkedList<>();
        while (resultSet.next()){
            RawDatabase database = new RawDatabase();
            database.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            if(database.getTABLE_CAT()!=null && isSysCat(database.getTABLE_CAT())){
                continue;
            }
            rawDatabases.add(database);
        }
        DatasourceResolver.close(resultSet);
        return rawDatabases;
    }

    @Override
    public List<RawTable> parseToTable(ResultSet resultSet) throws SQLException {
        final int columnSize = resultSet.getMetaData().getColumnCount();
        List<RawTable> rawTables = new LinkedList<>();
        while (resultSet.next()){
            RawTable table = new RawTable();
            table.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            if (table.getTABLE_CAT()!=null && isSysCat(table.getTABLE_CAT())){
                continue;
            }
            table.setTABLE_SCHEM(resultSet.getString(2 /*"TABLE_SCHEM"*/));
            if (table.getTABLE_SCHEM()!=null && isSysSchema(table.getTABLE_SCHEM())){
                continue;
            }
            table.setTABLE_NAME(resultSet.getString(3 /*"TABLE_NAME"*/));
            if (isSysTable(table.getTABLE_NAME())){
                continue;
            }
            rawTables.add(table);
            table.setTABLE_TYPE(resultSet.getString(4 /*"TABLE_TYPE"*/));
            table.setREMARKS(resultSet.getString(5 /*"REMARK"*/));
            if (columnSize <6 ) { /*for sqlserver and oracle and postgresql */ continue; }
            table.setTYPE_CAT(resultSet.getString(6 /*"TYPE_CAT"*/));
            table.setTYPE_SCHEM(resultSet.getString(7 /*"TYPE_SCHEM"*/));
            table.setTYPE_NAME(resultSet.getString(8 /*"TYPE_NAME"*/));
            table.setSELF_REFERENCING_COL_NAME(resultSet.getString(9 /*"SELF_REFERENCING_COL_NAME"*/));
            table.setREF_GENERATION(resultSet.getString(10 /*"REF_GENERATION"*/));
        }
        DatasourceResolver.close(resultSet);
        return rawTables;
    }

    @Override
    public List<RawColumn> parseToColumn(ResultSet resultSet) throws SQLException {
        final int columnSize = resultSet.getMetaData().getColumnCount();
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
            rawColumns.add(column);
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
            if (columnSize < 19) {/* for oracle */ continue;}
            // sql server has different realization of fellow
            column.setSCOPE_CATALOG(resultSet.getString(19 /*"SCOPE_CATALOG"*/));
            column.setSCOPE_SCHEMA(resultSet.getString(20 /*"SCOPE_SCHEMA"*/));
            column.setSCOPE_TABLE(resultSet.getString(21 /*"SCOPE_TABLE"*/));
            column.setSOURCE_DATA_TYPE(resultSet.getShort(22 /*"SOURCE_DATA_TYPE"*/));
            column.setIS_AUTOINCREMENT(resultSet.getString(23 /*"IS_AUTOINCREMENT"*/));
            if (columnSize < 24) { /* for hive and postgresql */ continue;}
            column.setIS_GENERATEDCOLUMN(resultSet.getString(24 /*"IS_GENERATEDCOLUMN"*/));
        }
        DatasourceResolver.close(resultSet);
        return rawColumns;
    }

    @Override
    public List<RawPrimaryKey> parseToPrimaryKey(ResultSet resultSet) throws SQLException {
        List<RawPrimaryKey> rawPrimaryKeys = new LinkedList<>();
        while (resultSet.next()){
            RawPrimaryKey primaryKey = new RawPrimaryKey();
            rawPrimaryKeys.add(primaryKey);
            primaryKey.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            primaryKey.setTABLE_SCHEM(resultSet.getString(2 /*"TABLE_SCHEM"*/));
            primaryKey.setTABLE_NAME(resultSet.getString(3 /*"TABLE_NAME"*/));
            primaryKey.setCOLUMN_NAME(resultSet.getString(4 /*"COLUMN_NAME"*/));
            primaryKey.setKEY_SEQ(resultSet.getShort(5 /*"KEY_SEQ"*/));
            primaryKey.setPK_NAME(resultSet.getString(6 /*"PK_NAME"*/));
        }
        DatasourceResolver.close(resultSet);
        return rawPrimaryKeys;
    }

    @Override
    public List<RawIndexInfo> parseToIndexInfo(ResultSet resultSet) throws SQLException {
        List<RawIndexInfo> rawIndexInfos = new LinkedList<>();
        while (resultSet.next()){
            RawIndexInfo indexInfo = new RawIndexInfo();
            rawIndexInfos.add(indexInfo);
            indexInfo.setTABLE_CAT(resultSet.getString(1 /*"TABLE_CAT"*/));
            indexInfo.setTABLE_SCHEM(resultSet.getString(2 /*"TABLE_SCHEM"*/));
            indexInfo.setTABLE_NAME(resultSet.getString(3 /*"TABLE_NAME"*/));
            indexInfo.setNON_UNIQUE(resultSet.getBoolean(4 /*"NON_UNIQUE"*/));
            indexInfo.setINDEX_QUALIFIER(resultSet.getString(5 /*"INDEX_QUALIFIER"*/));
            indexInfo.setINDEX_NAME(resultSet.getString(6 /*"INDEX_NAME"*/));
            indexInfo.setTYPE(resultSet.getShort(7 /*"TYPE"*/));
            indexInfo.setORDINAL_POSITION(resultSet.getShort(8 /*"ORDINAL_POSITION"*/));
            indexInfo.setCOLUMN_NAME(resultSet.getString(9 /*"COLUMN_NAME"*/));
            indexInfo.setASC_OR_DESC(resultSet.getString(10 /*"ASC_OR_DESC"*/));
            indexInfo.setCARDINALITY(resultSet.getLong(11 /*"CARDINALITY"*/));
            indexInfo.setPAGES(resultSet.getLong(12 /*"PAGES"*/));
            indexInfo.setFILTER_CONDITION(resultSet.getString(13 /*"FILTER_CONDITION"*/));
        }
        DatasourceResolver.close(resultSet);
        return rawIndexInfos;
    }

    @Override
    public List<RawForeignKeyInfo> parseToForeignKeyInfo(ResultSet resultSet) throws SQLException {
        List<RawForeignKeyInfo> rawForeignKeyInfos = new LinkedList<>();
        while (resultSet.next()){
            RawForeignKeyInfo foreignKeyInfo = new RawForeignKeyInfo();
            rawForeignKeyInfos.add(foreignKeyInfo);
            foreignKeyInfo.setPKTABLE_CAT(resultSet.getString(1 /*"PKTABLE_CAT"*/));
            foreignKeyInfo.setPKTABLE_SCHEM(resultSet.getString(2 /*"PKTABLE_SCHEM"*/));
            foreignKeyInfo.setPKTABLE_NAME(resultSet.getString(3 /*"PKTABLE_NAME"*/));
            foreignKeyInfo.setPKCOLUMN_NAME(resultSet.getString(4 /*"PKCOLUMN_NAME"*/));
            foreignKeyInfo.setFKTABLE_CAT(resultSet.getString(5 /*"FKTABLE_CAT"*/));
            foreignKeyInfo.setFKTABLE_SCHEM(resultSet.getString(6 /*"FKTABLE_SCHEM"*/));
            foreignKeyInfo.setFKTABLE_NAME(resultSet.getString(7 /*"FKTABLE_NAME"*/));
            foreignKeyInfo.setFKCOLUMN_NAME(resultSet.getString(8 /*"FKCOLUMN_NAME"*/));
            foreignKeyInfo.setKEY_SEQ(resultSet.getShort(9 /*"KEY_SEQ"*/));
            foreignKeyInfo.setUPDATE_RULE(resultSet.getShort(10 /*"UPDATE_RULE"*/));
            foreignKeyInfo.setDELETE_RULE(resultSet.getShort(11 /*"DELETE_RULE"*/));
            foreignKeyInfo.setFK_NAME(resultSet.getString(12 /*"FK_NAME"*/));
            foreignKeyInfo.setPK_NAME(resultSet.getString(13 /*"PK_NAME"*/));
        }
        return rawForeignKeyInfos;
    }

    @Override
    public List<RawProcedure> parseToProcedure(ResultSet resultSet) throws SQLException {
        List<RawProcedure> rawProcedures = new LinkedList<>();
        while (resultSet.next()){
            RawProcedure procedure = new RawProcedure();
            rawProcedures.add(procedure);
            procedure.setPROCEDURE_CAT(resultSet.getString(1 /*PROCEDURE_CAT*/));
            procedure.setPROCEDURE_SCHEM(resultSet.getString(2 /*PROCEDURE_SCHEM*/));
            procedure.setPROCEDURE_NAME(resultSet.getString(3 /*PROCEDURE_NAME*/));
            procedure.setREMARK(resultSet.getString(7 /*REMARKS*/));
            procedure.setPROCEDURE_TYPE(resultSet.getShort(8 /*PROCEDURE_TYPE*/));
            procedure.setSPECIFIC_NAME(resultSet.getString(9 /*SPECIFIC_NAME*/));
        }
        return rawProcedures;
    }
}
