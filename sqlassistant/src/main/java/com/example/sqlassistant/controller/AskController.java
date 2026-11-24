package com.example.sqlassistant.controller;

import com.example.sqlassistant.SqlTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AskController {
    record AskRequest(String question) {}
    record AskResponse(String answer, List<String> sql) {}

    private static final String SYSTEM = """
    You are a data analyst for a company database (PostgreSQL).
    Always call getSchema first, then use runQuery with a single SELECT statement.
    Never try to modify data. If runQuery returns an error, fix the query and retry.
    Answer briefly in plain English and mention key numbers.
    If the question is not about the data, say you can only answer questions about the company data.

    Business notes:
    - Table salary has one row per month. salary_month is the first day of the month.
    - salary is the amount for that month. gst_percentage is a percentage (18 means 18%).
    - GST amount = salary * gst_percentage / 100. Total including GST = salary + GST amount.
    - Amounts are in Indian rupees (INR).
    """;

    private final ChatClient chatClient;
    private final SqlTools tools;

    public AskController(ChatClient.Builder builder, SqlTools tools) {
        this.tools = tools;
        this.chatClient = builder.defaultSystem(SYSTEM).build();
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        tools.reset();
        String answer = chatClient.prompt()
                .user(request.question())
                .tools(tools)
                .call()
                .content();
        return new AskResponse(answer, tools.drainExecuted());
    }
}
