@echo off

pushd "%~dp0"

echo.
echo ###############################################################################
echo ### WASTEDGE API JASPER DESIGNER                                            ###
echo ###############################################################################
echo.

cd wastedge-api-jasper-designer
call mvn clean install
call mvn nbm:cluster
call mvn nbm:run-ide "-Dnetbeans.installation=C:/Program Files (x86)/Jaspersoft/iReport-5.6.0" -Dnetbeans.userdir=C:/Users/Pieter/.ireport/5.6.0
cd ..

popd

pause
