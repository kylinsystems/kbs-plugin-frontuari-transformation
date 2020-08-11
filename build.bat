@echo off

set DEBUG_MODE=

if "%1" == "debug" (
  set DEBUG_MODE=debug
)

cd transformation.targetplatform
call .\plugin-builder.bat %DEBUG_MODE% ..\transformation ..\transformation.test
cd ..
