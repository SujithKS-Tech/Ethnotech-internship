$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$javaExe = Get-Command java -ErrorAction SilentlyContinue
if (-not $env:JAVA_HOME -and $javaExe) {
    $env:JAVA_HOME = Split-Path (Split-Path $javaExe.Source -Parent) -Parent
}

$maven = $null
if (Test-Path ".\mvnw.cmd") {
    $maven = ".\mvnw.cmd"
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $maven = "mvn"
}

if (-not $maven) {
    throw "Maven was not found. Install Maven or add Maven Wrapper (mvnw.cmd) in this folder."
}

Write-Host "Starting Expense Tracker at http://localhost:8080"
& $maven spring-boot:run
