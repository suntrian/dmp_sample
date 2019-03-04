package com.quantchi.extractor.parser;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

public class PostgresMetaDataResultSetParser extends DefaultMetaDataResultSetParser {

    private PostgresMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (PostgresMetaDataResultSetParser.class){
                if (instance == null){
                    return new PostgresMetaDataResultSetParser();
                }
            }
        }
        return instance;
    }

    @Override
    protected boolean isSysCat(String catName) {
        return catName!=null && catName.toLowerCase().startsWith("pg_");
    }

    @Override
    protected boolean isSysSchema(String schemName) {
        return schemName != null && schemName.toLowerCase().startsWith("pg_");
    }

    @Override
    protected boolean isSysTable(String tabName) {
        return tabName != null && tabName.toLowerCase().startsWith("pg_");
    }

    @Override
    public ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException {
        return metaData.getCatalogs();
    }
}
