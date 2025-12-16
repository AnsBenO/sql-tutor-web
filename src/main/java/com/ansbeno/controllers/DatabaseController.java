package com.ansbeno.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.ansbeno.db.DatabaseService;
import com.ansbeno.db.QueryResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@SessionAttributes({ "server", "username", "password" })
public class DatabaseController {

    private final DatabaseService databaseService;

    private static final String RESULTS_TABLE_FRAGMENT = "fragments/result-table :: resultTable";

    private static final String TABLE_LIST_FRAGMENT = "fragments/table-list :: tableList";

    private static final String DATABASE_SELECT_FRAGMENT = "fragments/database-select :: databaseSelect";

    private static final String ERROR_MESSAGE = "error";

    private static final String SUCCESS_MESSAGE = "success";

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
        try {
            // Establish server connection
            databaseService.getServerConnection(username, password, server);
            // Fetch databases
            List<String> databases = databaseService.getDatabases();
            model.addAttribute("databases", databases);
            model.addAttribute("server", server);
            model.addAttribute("username", username);
            model.addAttribute("password", password);
            model.addAttribute(SUCCESS_MESSAGE, "Connected to server successfully.");

        } catch (SQLException e) {
            model.addAttribute(ERROR_MESSAGE, "Connection error: " + e.getMessage());
        }
        return DATABASE_SELECT_FRAGMENT;
    }

    @PostMapping("/connectDatabase")
    public String connectDatabase(@RequestParam String database,
            @ModelAttribute("server") String server,
            @ModelAttribute("username") String username,
            @ModelAttribute("password") String password,
            Model model) {
        try {
            // Establish database connection
            databaseService.getDbConnection(username, password, server, database);
            List<String> tables = databaseService.getTables(database);
            model.addAttribute("tables", tables);
            model.addAttribute("database", database);
            model.addAttribute(SUCCESS_MESSAGE, "Connected to database successfully.");

        } catch (SQLException e) {
            model.addAttribute(ERROR_MESSAGE, "Connection error: " + e.getMessage());
        }
        return TABLE_LIST_FRAGMENT;
    }

    @PostMapping("/showTable")
    public String showTable(@RequestParam String tableName,
            Model model) {
        try {
            QueryResult schema = databaseService.getTableSchema(tableName);
            model.addAttribute("result", schema);
        } catch (SQLException e) {
            model.addAttribute(ERROR_MESSAGE, "Error fetching table schema: " + e.getMessage());
        }
        return RESULTS_TABLE_FRAGMENT;
    }

    @PostMapping("/execute")
    public String execute(@RequestParam String sql,
            Model model) {
        try {
            QueryResult result = databaseService.executeQuery(sql);
            model.addAttribute("result", result);

        } catch (SQLException e) {
            model.addAttribute(ERROR_MESSAGE, "SQL execution error: " + e.getMessage());
        }
        return RESULTS_TABLE_FRAGMENT;
    }

    @PostMapping("/disconnect")
    public String disconnect(SessionStatus status) {
        try {
            databaseService.closeConnection();
            status.setComplete();
            return "redirect:/";
        } catch (SQLException e) {
            return "redirect:/";
        }
    }
}
