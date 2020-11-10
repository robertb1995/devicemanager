@ECHO OFF
set CLASSPATH=.

cd %CLASSPATH%
call mvn clean install
java -jar %CLASSPATH%/target/devicemanager-0.0.1-SNAPSHOT.jar