package com.quantchi.extractor.parser;

import com.quantchi.extractor.entity.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface MetaDataResultSetParser {

    ResultSet decideDatabase(DatabaseMetaData metaData) throws SQLException;

    List<RawDatabase> parseToDatabase(ResultSet resultSet) throws SQLException;

    List<RawTable> parseToTable(ResultSet resultSet) throws SQLException;

    List<RawColumn> parseToColumn(ResultSet resultSet) throws SQLException;

    List<RawPrimaryKey> parseToPrimaryKey(ResultSet resultSet) throws SQLException;

    List<RawIndexInfo> parseToIndexInfo(ResultSet resultSet) throws SQLException;

    List<RawForeignKeyInfo> parseToForeignKeyInfo(ResultSet resultSet) throws SQLException;

    List<RawProcedure> parseToProcedure(ResultSet resultSet) throws SQLException;
}
