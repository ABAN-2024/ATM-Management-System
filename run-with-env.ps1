# Usage: copy .env.example -> .env, edit values, then run this script.
# This loads key=value pairs from .env into process env vars and runs Maven.
$envFile = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path $envFile)) {
    Write-Error ".env file not found in $PSScriptRoot. Copy .env.example to .env and edit it first."
    exit 1
}
Get-Content $envFile | ForEach-Object {
    if ($_ -match '^\s*#') { return }
    if ($_ -match '^\s*$') { return }
    $parts = $_ -split '=', 2
    if ($parts.Length -ne 2) { return }
    $name = $parts[0].Trim()
    $value = $parts[1].Trim()
    $env:$name = $value
    Write-Host "Set" $name
}
Write-Host "Starting Spring Boot with process-scoped env vars..."
mvn spring-boot:run
