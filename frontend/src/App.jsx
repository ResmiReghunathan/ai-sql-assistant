import { useState } from "react";

export default function App() {
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [sql, setSql] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const examples = [
  "What was the total salary paid in the last 6 months?",
  "Show the GST amount for each month this year",
  "Which month had the highest salary?",
];

  async function ask(q) {
    const text = q ?? question;
    if (!text.trim()) return;
    setLoading(true); setError(""); setAnswer(""); setSql([]);
    try {
      const res = await fetch("http://localhost:8080/api/ask", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ question: text }),
      });
      if (!res.ok) throw new Error("Server error " + res.status);
      const data = await res.json();
      setAnswer(data.answer);
      setSql(data.sql);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ maxWidth: 720, margin: "40px auto", fontFamily: "sans-serif" }}>
      <h1>AI SQL Assistant</h1>
      <input
        style={{ width: "75%", padding: 8 }}
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && ask()}
        placeholder="Ask a question about the shop data..."
      />
      <button onClick={() => ask()} disabled={loading} style={{ padding: 8, marginLeft: 8 }}>
        {loading ? "Thinking..." : "Ask"}
      </button>
      <div style={{ margin: "12px 0" }}>
        {examples.map((e) => (
          <button key={e} onClick={() => { setQuestion(e); ask(e); }} style={{ marginRight: 8 }}>
            {e}
          </button>
        ))}
      </div>
      {error && <p style={{ color: "crimson" }}>{error}</p>}
      {answer && <><h3>Answer</h3><p>{answer}</p></>}
      {sql.length > 0 && (
        <>
          <h3>SQL used</h3>
          {sql.map((s, i) => (
            <pre key={i} style={{ background: "#f4f4f4", padding: 10, overflowX: "auto" }}>{s}</pre>
          ))}
        </>
      )}
    </div>
  );
}