package com.ansbeno.db;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record QueryResult(
        List<String> columnNames,
        List<List<String>> rows) {
}