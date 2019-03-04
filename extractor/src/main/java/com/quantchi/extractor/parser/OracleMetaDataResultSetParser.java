package com.quantchi.extractor.parser;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleMetaDataResultSetParser extends DefaultMetaDataResultSetParser {

    private OracleMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (OracleMetaDataResultSetParser.class){
                if (instance == null){
                    return new OracleMetaDataResultSetParser();
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
        return metaData.getSchemas();
    }
}
