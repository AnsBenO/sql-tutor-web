package com.ansbeno.db;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseService {

      public List<String> getDatabases() throws SQLException;

      public List<String> getTables(String database) throws SQLException, IllegalArgumentException;

      public QueryResult executeQuery(String sql) throws SQLException;

      public QueryResult getTableSchema(String tableName) throws SQLException;

      public void connectServer(String username, String password, String server) throws SQLException;

      public void connectDatabase(String username, String password, String server, String database) throws SQLException;

      public void closeConnection() throws SQLException, IllegalArgumentException;
}
