#!/usr/bin/env bash
# Run QuizService JMeter plan in non-GUI mode and generate HTML report.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if ! command -v jmeter >/dev/null 2>&1; then
  echo "ERROR: 'jmeter' not found on PATH. Install Apache JMeter 5.x and try again."
  exit 1
fi

BASE_HOST="${BASE_HOST:-localhost}"
BASE_PORT="${BASE_PORT:-8080}"
THREADS="${THREADS:-5}"
RAMP_UP="${RAMP_UP:-5}"
LOOPS="${LOOPS:-10}"

mkdir -p results
rm -rf results/html-report results/results.jtl results/aggregate-results.jtl

echo "Running QuizService JMeter plan against http://${BASE_HOST}:${BASE_PORT}"
echo "Threads=${THREADS} RampUp=${RAMP_UP}s Loops=${LOOPS}"

jmeter -n \
  -t QuizService_QuestionAPI_TestPlan.jmx \
  -l results/results.jtl \
  -e -o results/html-report \
  -JBASE_HOST="${BASE_HOST}" \
  -JBASE_PORT="${BASE_PORT}" \
  -JTHREADS="${THREADS}" \
  -JRAMP_UP="${RAMP_UP}" \
  -JLOOPS="${LOOPS}"

echo
echo "Done."
echo "  JTL:  ${SCRIPT_DIR}/results/results.jtl"
echo "  HTML: ${SCRIPT_DIR}/results/html-report/index.html"
