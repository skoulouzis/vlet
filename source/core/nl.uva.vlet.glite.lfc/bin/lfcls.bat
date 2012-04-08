@echo off
rem #
rem # info   : lfcfs example
rem # author : Piter T. de Boer 
rem # MS DOs adaptation: F. Michel
rem #

setlocal ENABLEDELAYEDEXPANSION
set JAVA="%JAVA_HOME%\bin\java"

rem -- Collect command line arguments
set ARGS=
:loop
    if [%1] == [] goto end
    set ARGS=%ARGS% %1
    shift
    goto loop
:end

rem -- Build classpath from jars in the lib folder
set CLASSPATH=.
FOR /R .\lib %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G
echo 

echo CLASSPATH = %CLASSPATH%
%JAVA% -cp %CLASSPATH% nl.uva.vlet.glite.lfc.main.LfcLs %ARGS%

