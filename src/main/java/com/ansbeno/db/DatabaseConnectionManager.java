package com.ansbeno.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionManager {

      private final DatabaseSession databaseSession;
      
      public Connection getDatabaseConnection() {
            return this.databaseSession.getConnection();
      }
      

      public void connectServer(String username, String password, String server) throws SQLException {
            String url = "jdbc:postgresql://" + server + "/postgres";
            log.info("Connecting to server: {}", url);
            // close previous connection if exists
            closeConnection();
            Connection conn = DriverManager.getConnection(url, username, password);
            this.databaseSession.setConnection(conn);
      }

      public void connectDatabase(String username, String password, String server, String database)
                  throws SQLException {

            String url = "jdbc:postgresql://" + server + "/" + database;
            log.info("Connecting to database: {}", url);
            // close previous connection if exists
            closeConnection();
            Connection conn = DriverManager.getConnection(url, username, password);
            this.databaseSession.setConnection(conn);
      }

      public void closeConnection() throws SQLException {
            Connection databaseConnection = databaseSession.getConnection();
            if (databaseConnection != null && !databaseConnection.isClosed()) {
                  databaseConnection.close();
                  log.info("Database connection closed.");
            }

      }
}
