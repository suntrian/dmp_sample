package com.quantchi.extractor.extractimpl;

import com.quantchi.extractor.datasource.DatasourceResolver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractSqlExtractor {

    public static List<Object[]> extractArray(Connection connection, String sql) throws SQLException {
        ResultSet resultSet = DatasourceResolver.query(connection, sql);
        return extractResultSetArray(resultSet);
    }

    public static List<Object[]> extractArray(Connection connection, String sql, Object... args) throws SQLException {
        ResultSet resultSet = DatasourceResolver.query(connection, sql, args);
        return extractResultSetArray(resultSet);
    }

    public static List<Map<String, Object>> extractMap(Connection connection, String sql) throws SQLException {
        ResultSet resultSet = DatasourceResolver.query(connection, sql);
        return extractResultSetMap(resultSet);
    }

    public static List<Map<String, Object>> extractMap(Connection connection, String sql, Object... args) throws SQLException {
        ResultSet resultSet = DatasourceResolver.query(connection, sql, args);
        return extractResultSetMap(resultSet);
    }

    private static List<Object[]> extractResultSetArray(ResultSet resultSet) throws SQLException {
        final int columnSize = resultSet.getMetaData().getColumnCount();
        List<Object[]> objects = new LinkedList<>();
        while (resultSet.next()){
            Object[] obj = new Object[columnSize];
            for (int i = 0; i < columnSize; i++){
                obj[i] = resultSet.getObject(i+1);
            }
            objects.add(obj);
        }
        DatasourceResolver.close(resultSet);
        return objects;
    }

    private static List<Map<String, Object>> extractResultSetMap(ResultSet resultSet) throws SQLException {
        final int columnSize = resultSet.getMetaData().getColumnCount();
        List<String> columnNames = new ArrayList<>(columnSize);
        for (int i = 0; i < columnSize; i++){
            columnNames.add(resultSet.getMetaData().getColumnLabel(i+1));
        }
        List<Map<String, Object>> maps = new LinkedList<>();
        while (resultSet.next()){
            Map<String, Object> map = new HashMap<>(columnSize);
            for (int i = 0; i < columnSize; i++){
                map.put(columnNames.get(i), resultSet.getObject(i+1));
            }
            maps.add(map);
        }
        DatasourceResolver.close(resultSet);
        return maps;
    }
}
