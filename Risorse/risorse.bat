@echo off
setlocal

set "FILE_URL=https://github.com/Vsb23/Risorse-da-scaricare/raw/refs/heads/main/risorse.zip?download="
set "ZIP_FILE=.\risorse.zip"
set "EXTRACT_PATH=.\risorse\"
set "7Z_PATH=C:\Program Files\7-Zip\7z.exe"

REM Download con verifica
powershell -Command "Invoke-WebRequest -Uri '%FILE_URL%' -OutFile '%ZIP_FILE%' -DisableKeepAlive -UseBasicParsing"
if errorlevel 1 goto error

REM Controllo esistenza file
if not exist "%ZIP_FILE%" (
    echo File non scaricato!
    goto error
)

REM Decompressione con 7-Zip (modifica il percorso se necessario)
if exist "%7Z_PATH%" (
    "%7Z_PATH%" x "%ZIP_FILE%" -o"%EXTRACT_PATH%" -y
) else (
    powershell -Command "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%EXTRACT_PATH%' -Force"
)
if errorlevel 1 goto error

echo Operazione completata con successo!
del "%ZIP_FILE%"
pause
exit /b 0

:error
echo Errore durante l'operazione. Verifica:
echo - URL del file
echo - Connessione internet
echo - Integrità dell'archivio ZIP
pause
exit /b 1