[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "=== Running regression tests ==="

mvn clean test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Tests failed"
    exit 1
}

Write-Host "=== Generating coverage report ==="

mvn jacoco:report

Write-Host "=== Opening JaCoCo report ==="

Start-Process "target/site/jacoco/index.html"

Write-Host "✅ Regression test finished successfully"
