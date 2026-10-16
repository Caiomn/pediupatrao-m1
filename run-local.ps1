$ErrorActionPreference = 'Stop'

$jdkPath = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
if (-not (Test-Path (Join-Path $jdkPath 'bin\java.exe'))) {
    throw "JDK 21 nao encontrado em $jdkPath. Ajuste `$jdkPath no arquivo run-local.ps1."
}

$env:JAVA_HOME = $jdkPath
$env:Path = "$jdkPath\bin;$env:Path"
$env:PORT = '3001'

Write-Host 'Iniciando PediuPatrao em http://localhost:3001/login'
Write-Host 'Mantenha este terminal aberto enquanto usa a aplicacao.'
Push-Location $PSScriptRoot
try {
    & .\mvnw.cmd spring-boot:run
    if ($LASTEXITCODE -ne 0) {
        throw "O Spring Boot encerrou com codigo $LASTEXITCODE. Veja as mensagens acima."
    }
}
finally {
    Pop-Location
}
