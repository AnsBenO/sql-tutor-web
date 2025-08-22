package com.ansbeno.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ansbeno.db.DatabaseService;
import com.ansbeno.db.QueryResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@SessionAttributes({ "server", "username", "password" })
public class DatabaseController {

    private final DatabaseService databaseService;

    private Connection dbConnection;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("server", "localhost:5432");
        model.addAttribute("username", "postgres");
        model.addAttribute("password", "123456");
        return "index";
    }

    @PostMapping("/connectServer")
    public String connectServer(@RequestParam String server,
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        try (Connection conn = databaseService.getServerConnection(username, password, server)) {

            List<String> databases = databaseService.getDatabases(conn);
            model.addAttribute("databases", databases);
            model.addAttribute("server", server);
            model.addAttribute("username", username);
            model.addAttribute("password", password);
            model.addAttribute("success", "Connected to server successfully.");
            return "fragments/database-select :: databaseSelect";
        } catch (SQLException e) {
            model.addAttribute("error", "Connection error: " + e.getMessage());
            return "fragments/database-select :: databaseSelect";
        }
    }

    @PostMapping("/connectDatabase")
    public String connectDatabase(@RequestParam String database,
            @ModelAttribute("server") String server,
            @ModelAttribute("username") String username,
            @ModelAttribute("password") String password,
            Model model) {
        try {
            Connection conn = databaseService.getDbConnection(username, password, server, database);
            this.dbConnection = conn; // Store the connection in the session
            List<String> tables = databaseService.getTables(dbConnection, database);
            model.addAttribute("tables", tables);
            model.addAttribute("database", database);
            model.addAttribute("success", "Connected to database successfully.");
            return "fragments/table-list :: tableList";
        } catch (SQLException e) {
            model.addAttribute("error", "Connection error: " + e.getMessage());
            return "fragments/table-list :: tableList";
        }
    }

    @PostMapping("/showTable")
    public String showTable(@RequestParam String tableName,
            Model model) {
        try {
            QueryResult schema = databaseService.getTableSchema(dbConnection, tableName);
            model.addAttribute("result", schema);
            return "fragments/result-table :: resultTable";
        } catch (SQLException e) {
            model.addAttribute("error", "Error fetching table schema: " + e.getMessage());
            return "fragments/result-table :: resultTable";
        }
    }

    @PostMapping("/execute")
    public String execute(@RequestParam String sql,
            Model model) {
        try {
            QueryResult result = databaseService.executeQuery(dbConnection, sql);
            model.addAttribute("result", result);
            return "fragments/result-table :: resultTable";
        } catch (SQLException e) {
            model.addAttribute("error", "SQL execution error: " + e.getMessage());
            return "fragments/result-table :: resultTable";
        }
    }
}
