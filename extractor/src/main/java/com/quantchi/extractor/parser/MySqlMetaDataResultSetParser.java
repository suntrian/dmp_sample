package com.quantchi.extractor.parser;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class MySqlMetaDataResultSetParser extends DefaultMetaDataResultSetParser {

    private final static Set<String> SYS_CAT = new HashSet<>(Arrays.asList(
            "information_schema",
            "mysql",
            "performance_schema",
            "test",
            "sys"
    ));

    private MySqlMetaDataResultSetParser(){}

    public static MetaDataResultSetParser getInstance(){
        if (instance == null){
            synchronized (MySqlMetaDataResultSetParser.class){
                if (instance == null){
                    return new MySqlMetaDataResultSetParser();
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
