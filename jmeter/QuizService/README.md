# QuizService — JMeter Test Plan

Load and functional tests for the **QuizService** Spring Boot microservice (`QuestionController`).

## APIs under test

| ID | Method | Endpoint | Expected |
|----|--------|----------|----------|
| TC-01 | `GET` | `/question/allQuestions` | HTTP 200, JSON array |
| TC-02 | `POST` | `/question/add` | HTTP 200, body `Question added` |
| TC-03 | `GET` | `/question/allQuestions` | HTTP 200, newly added `questionTitle` present |
| TC-04 | `POST` | `/question/add` (empty `{}`) | No unhandled 5xx |
| TC-05 | `GET` | `/question/invalidPath` | HTTP 404 |

## Request body (POST `/question/add`)

Matches the `Question` entity fields:

```json
{
  "questionTitle": "...",
  "option1": "...",
  "option2": "...",
  "option3": "...",
  "option4": "...",
  "rightAnswer": "...",
  "category": "..."
}
```

Sample rows live in `questions.csv` and are injected by the CSV Data Set Config.

## Files

| File | Purpose |
|------|---------|
| `QuizService_QuestionAPI_TestPlan.jmx` | JMeter test plan |
| `questions.csv` | Parameterized question data |
| `run-jmeter.sh` | CLI runner (non-GUI) |

## Prerequisites

1. Start **QuizService** locally (default `http://localhost:8080`).
2. Install [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi) 5.x and ensure `jmeter` is on your `PATH`.

## Run in GUI

1. Open Apache JMeter.
2. **File → Open** → `QuizService_QuestionAPI_TestPlan.jmx`.
3. Confirm **User Defined Variables**: `BASE_HOST`, `BASE_PORT` (default `localhost:8080`).
4. Click the green **Start** button.
5. Inspect **View Results Tree** and **Summary Report**.

## Run from CLI (recommended for load)

From this directory:

```bash
chmod +x run-jmeter.sh
./run-jmeter.sh
```

Or manually:

```bash
mkdir -p results
jmeter -n \
  -t QuizService_QuestionAPI_TestPlan.jmx \
  -l results/results.jtl \
  -e -o results/html-report \
  -JBASE_HOST=localhost \
  -JBASE_PORT=8080 \
  -JTHREADS=5 \
  -JRAMP_UP=5 \
  -JLOOPS=10
```

Open `results/html-report/index.html` for the HTML report.

## Tunable variables

| Variable | Default | Meaning |
|----------|---------|---------|
| `BASE_PROTOCOL` | `http` | Protocol |
| `BASE_HOST` | `localhost` | Host |
| `BASE_PORT` | `8080` | Port |
| `THREADS` | `5` | Concurrent users |
| `RAMP_UP` | `5` | Seconds to start all users |
| `LOOPS` | `10` | Iterations per user |

Override from CLI with `-JNAME=value` (example above).

## Notes

- Keep `questions.csv` in the **same folder** as the `.jmx` when running (relative path).
- No auth is configured; add a Header Manager entry if you later protect the API.
- Response-time assertions fail samples slower than **2 seconds**.
