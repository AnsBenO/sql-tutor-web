package com.ansbeno.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatabaseService {

      public Connection getServerConnection(String username, String password, String server) throws SQLException {
            String url = "jdbc:postgresql://" + server + "/";
            return DriverManager.getConnection(url, username, password);
      }

      public Connection getDbConnection(String username, String password, String server, String database)
                  throws SQLException {
            String url = "jdbc:postgresql://" + server + "/" + database;
            log.info("Connecting to database: {}", url);
            return DriverManager.getConnection(url, username, password);
      }

      public List<String> getDatabases(Connection connection) throws SQLException {
            List<String> databases = new ArrayList<>();
            try (Statement statement = connection.createStatement()) {
                  ResultSet rs = statement.executeQuery("SELECT datname FROM pg_database;");
                  while (rs.next()) {
                        databases.add(rs.getString(1));
                  }
            }
            return databases;
      }

      public List<String> getTables(Connection connection, String database) throws SQLException {
            List<String> tables = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            String[] types = { "TABLE" };
            ResultSet rs = metaData.getTables(database, null, "%", types);
            while (rs.next()) {
                  tables.add(rs.getString("table_name"));
            }
            return tables;
      }

      public QueryResult executeQuery(Connection connection, String sql) throws SQLException {
            try (Statement st = connection.createStatement()) {
                  ResultSet rs = st.executeQuery(sql);
                  ResultSetMetaData metaData = rs.getMetaData();
                  int columnCount = metaData.getColumnCount();

                  List<String> columnNames = new ArrayList<>();
                  for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                  }

                  List<List<String>> rows = new ArrayList<>();
                  while (rs.next()) {
                        List<String> row = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                              row.add(rs.getString(i));
                        }
                        rows.add(row);
                  }

                  return new QueryResult(columnNames, rows);

            }
      }

      public QueryResult getTableSchema(Connection connection, String tableName) throws SQLException {
            String sql = "SELECT * FROM " + tableName + " LIMIT 1";
            try (Statement st = connection.createStatement()) {
                  ResultSet rs = st.executeQuery(sql);
                  ResultSetMetaData metaData = rs.getMetaData();
                  int columnCount = metaData.getColumnCount();

                  List<String> columnNames = new ArrayList<>();
                  List<List<String>> rows = new ArrayList<>();

                  // Add column headers
                  columnNames.add("Column");
                  columnNames.add("Type");
                  columnNames.add("Precision");

                  // Add the column details
                  for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnType = metaData.getColumnTypeName(i);
                        int precision = metaData.getPrecision(i);
                        rows.add(List.of(columnName, columnType, String.valueOf(precision)));
                  }

                  return new QueryResult(columnNames, rows);
            }
      }
}
