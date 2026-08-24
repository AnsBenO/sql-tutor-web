package com.ansbeno.db;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Getter;
import lombok.Setter;

@Component
@SessionScope
@Getter
@Setter
public class DatabaseSession {
      private Connection connection;      
      private List<String> databaseTables;
}
