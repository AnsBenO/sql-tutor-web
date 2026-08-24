package com.ansbeno.db;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

      private final DatabaseConnectionManager databaseConnectionManager;
      private final DatabaseSession databaseSession;

      public List<String> getDatabases() throws SQLException {
            List<String> databases = new ArrayList<>();
            try (Statement statement = this.databaseConnectionManager.getDatabaseConnection().createStatement()) {
                  ResultSet rs = statement.executeQuery("SELECT datname FROM pg_database;");
                  while (rs.next()) {
                        databases.add(rs.getString(1));
                  }
            }
            return databases;
      }

      public List<String> getTables(String database) throws SQLException {
            List<String> tables = new ArrayList<>();
            DatabaseMetaData metaData = this.databaseConnectionManager.getDatabaseConnection().getMetaData();
            String[] types = { "TABLE" };
            ResultSet rs = metaData.getTables(database, null, "%", types);
            while (rs.next()) {
                  tables.add(rs.getString("table_name"));
            }
            this.databaseSession.setDatabaseTables(tables);
            return tables;
      }

      public QueryResult executeQuery(String sql) throws SQLException {
            try (Statement st = this.databaseConnectionManager.getDatabaseConnection().createStatement()) {
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

      public QueryResult getTableSchema(String tableName) throws SQLException {
            // 1. Whitelist validation (already present, which is great!)
            if (!databaseSession.getDatabaseTables().contains(tableName)) {
                  throw new IllegalArgumentException("Table not found: " + tableName);
            }

            // 2. Safely interpolate the validated table name directly into the string
            String sql = "SELECT * FROM " + tableName + " WHERE FALSE";

            try (PreparedStatement st = databaseConnectionManager
                        .getDatabaseConnection()
                        .prepareStatement(sql)) {
                  
                  try (ResultSet rs = st.executeQuery()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        List<String> columnNames = List.of("Column", "Type", "Precision");
                        List<List<String>> rows = new ArrayList<>();

                        for (int i = 1; i <= columnCount; i++) {
                              String columnName = metaData.getColumnName(i);
                              String columnType = metaData.getColumnTypeName(i);
                              int precision = metaData.getPrecision(i);

                              rows.add(List.of(
                                          columnName,
                                          columnType,
                                          String.valueOf(precision)));
                        }
                        return new QueryResult(columnNames, rows);
                  }
            }
      }

      public void connectServer(String username, String password, String server) throws SQLException {
            this.databaseConnectionManager.connectServer(username, password, server);
      }

      public void connectDatabase(String username, String password, String server, String database)
                  throws SQLException {
            this.databaseConnectionManager.connectDatabase(username, password, server, database);
      }

      @Override
      public void closeConnection() throws SQLException {
            this.databaseConnectionManager.closeConnection();
      }

}
