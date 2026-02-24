param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$Users = 10,
    [int]$Duration = 30,
    [int]$Ramp = 0,
    [int]$PauseMs = 0,
    [string]$Role = "ATHLETE",
    [switch]$DryRun
)

$scriptPath = Join-Path $PSScriptRoot "load-test.js"

$arguments = @(
    "--base-url", $BaseUrl,
    "--users", $Users,
    "--duration", $Duration,
    "--ramp", $Ramp,
    "--pause-ms", $PauseMs,
    "--role", $Role
)

if ($DryRun) {
    $arguments += "--dry-run"
}

node $scriptPath @arguments

