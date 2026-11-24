package com.example.sqlassistant;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SqlTools {
    private static final Pattern FORBIDDEN = Pattern.compile(
            "\\b(insert|update|delete|drop|alter|truncate|create|grant|revoke|copy|call|execute|pg_sleep)\\b",
            Pattern.CASE_INSENSITIVE);

    private final JdbcTemplate jdbc;
    private final ThreadLocal<List<String>> executed = ThreadLocal.withInitial(ArrayList::new);

    public SqlTools(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.jdbc.setMaxRows(100);        // row limit
        this.jdbc.setQueryTimeout(5);     // seconds
    }

    @Tool(description = "Returns all tables with their columns and types. Always call this first.")
    public String getSchema() {
        return jdbc.queryForList(
                "SELECT table_name, column_name, data_type FROM information_schema.columns " +
                        "WHERE table_schema = 'public' ORDER BY table_name, ordinal_position").toString();
    }

    @Tool(description = "Runs ONE read-only PostgreSQL SELECT query and returns up to 100 rows.")
    public String runQuery(@ToolParam(description = "A single PostgreSQL SELECT statement") String sql) {
        String cleaned = sql.trim().replaceAll(";+\\s*$", "");
        String lower = cleaned.toLowerCase();
        boolean ok = (lower.startsWith("select") || lower.startsWith("with"))
                && !cleaned.contains(";") && !FORBIDDEN.matcher(cleaned).find();
        if (!ok) {
            return "REJECTED: only a single read-only SELECT statement is allowed.";
        }
        executed.get().add(cleaned);
        try {
            return jdbc.queryForList(cleaned).toString();
        } catch (Exception e) {
            return "SQL ERROR: " + e.getMessage();   // lets the AI fix its own query
        }
    }

    public void reset() { executed.remove(); }

    public List<String> drainExecuted() {
        List<String> copy = new ArrayList<>(executed.get());
        executed.remove();
        return copy;
    }
}
