# AI SQL Assistant

Ask business questions in plain English and get answers from a PostgreSQL database.
An LLM agent reads the schema, writes a read-only SQL query, runs it through a safe tool, and explains the result.

screenshot/screenshot 1.png

## Tech stack
Java 21 · Spring Boot · Spring AI (tool calling) · Google Gemini API · PostgreSQL · Docker · React (Vite)

## How it works
React UI → Spring Boot REST API → LLM with tools (`getSchema`, `runQuery`) → PostgreSQL

## Safety layers
- The app connects with a **read-only database user**
- Only a single `SELECT` statement is allowed; other statements are rejected in code
- Row limit (100) and query timeout (5 seconds)
- Prompt rules telling the model never to modify data

## Run it locally
1. Start the database: `docker compose up -d`
2. Get a Gemini API key from aistudio.google.com and set it: `GEMINI_API_KEY=your-key`
3. Run the backend: open `sqlassistant` in IntelliJ and run `SqlassistantApplication`
4. Run the frontend: `cd frontend`, then `npm install` and `npm run dev`
5. Open http://localhost:5173

## Sample questions
- What was the total salary paid in the last 6 months?
- Show the GST amount for each month this year
- Which month had the highest salary?

## Notes
The data is fake sample data. The database passwords in this repo are for local demo use only.
Never put real or sensitive data through a free LLM API key.

## Possible improvements
Login and access control, query logging, charts for results, deployment on AWS.