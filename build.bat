@echo off
setlocal

echo Deleting old plugin...
del /f /q "server\plugins\Identity.jar"

echo Building the plugin...
mvn package -f "pom.xml"