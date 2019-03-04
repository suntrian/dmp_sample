package com.quantchi.extractor;

import com.quantchi.extractor.datasource.DatasourceResolver;
import com.quantchi.extractor.datasource.DriverType;
import com.quantchi.extractor.entity.*;
import com.quantchi.extractor.extractimpl.AbstractMetaDataExtractor;
import com.quantchi.extractor.extractimpl.AbstractSqlExtractor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCExtractor implements MetaDataExtractor, SqlExtractor {

    @Getter
    private Connection connection;

    @Getter
    private DatabaseMetaData databaseMetaData;

    public JDBCExtractor(String url, String username, String password) throws SQLException, ClassNotFoundException {
        this.connection = DatasourceResolver.getConnection(url, username, password);
    }

    public JDBCExtractor(DriverType driver, String host, Integer port, String database, String username, String password)
            throws SQLException, ClassNotFoundException {
        this.connection = DatasourceResolver.getConnection(driver, host, port, database, username, password);
    }

    /**
     * mysql 只有catalog,没有schema
     * oracle 只有schema 没有catalog
     * sqlserver、postgresql 有 catalog和schema，catalog作为数据库
     *
     * @return {@link RawDatabase}
     * @throws SQLException just throw out
     */
    @Override
    public List<RawDatabase> extractDatabase() throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractDatabase(this.databaseMetaData);
    }

    @Override
    public List<RawTable> extractTable() throws SQLException {
        return extractTable( null, null, null,"TABLE","VIEW");
    }

    @Override
    public List<RawTable> extractTable(@Nullable String catalog,
                                       @Nullable String schemaPattern,
                                       @Nullable String tableNamePattern,
                                       @Nullable String... types) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractTable(this.databaseMetaData, catalog, schemaPattern, tableNamePattern,types);
    }

    public List<RawColumn> extractColumn() throws SQLException {
        return extractColumn(null, null, null, null);
    }

    @Override
    public List<RawColumn> extractColumn(@Nullable String catalog,
                                         @Nullable String schemaPattern,
                                         @Nullable String tableNamePattern,
                                         @Nullable String columnNamePattern) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractColumn(this.databaseMetaData, catalog, schemaPattern, tableNamePattern, columnNamePattern);
    }

    public List<RawPrimaryKey> extractPrimaryKey(String tableName) throws SQLException {
        return extractPrimaryKey(null, null, tableName);
    }

    @Override
    public List<RawPrimaryKey> extractPrimaryKey(@Nullable String catalog,
                                                 @Nullable String schema,
                                                 String tableName) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractPrimaryKey(this.databaseMetaData, catalog, schema, tableName);
    }

    public List<RawIndexInfo> extractIndexInfo(String tableName,
                                               boolean unique,
                                               boolean approximate) throws SQLException {
        return extractIndexInfo(null, null, tableName, unique, approximate);
    }

    @Override
    public List<RawIndexInfo> extractIndexInfo(@Nullable String catalog,
                                               @Nullable String schema,
                                               String tableName,
                                               boolean unique,
                                               boolean approximate) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractIndexInfo(this.databaseMetaData, catalog, schema, tableName, unique, approximate);
    }

    @Override
    public List<RawForeignKeyInfo> extractExportedKey(@Nullable String catalog,
                                                      @Nullable String schema,
                                                      String table) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractExportedKey(this.databaseMetaData, catalog, schema, table);
    }

    @Override
    public List<RawForeignKeyInfo> extractImportedKey(@Nullable String catalog,
                                                      @Nullable String schema,
                                                      String table) throws SQLException {
        if (this.databaseMetaData==null){
            this.databaseMetaData = DatasourceResolver.getDatabaseMetaData(this.connection);
        }
        return AbstractMetaDataExtractor.extractImportedKey(this.databaseMetaData, catalog, schema, table);
    }

    @Override
    public List<Object[]> extractArray(String sql) throws SQLException {
        return AbstractSqlExtractor.extractArray(this.connection, sql);
    }

    @Override
    public List<Object[]> extractArray(String sql, Object... args) throws SQLException {
        return AbstractSqlExtractor.extractArray(this.connection, sql, args);
    }

    @Override
    public List<Map<String, Object>> extractMap(String sql) throws SQLException {
        return AbstractSqlExtractor.extractMap(this.connection, sql);
    }

    @Override
    public List<Map<String, Object>> extractMap(String sql, Object... args) throws SQLException {
        return AbstractSqlExtractor.extractMap(this.connection, sql, args);
    }

    @Override
    protected void finalize() {
        DatasourceResolver.close(this.connection);
    }

}
