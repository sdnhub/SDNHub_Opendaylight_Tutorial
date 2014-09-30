@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

SET CONTROLLER_RUNBAT=run.internal.bat
SET OF_FILTER=
SET OF13=0
SET BUNDLEFILTER=

:LOOP
IF "%~1" NEQ "" (
    SET CARG=%~1
    IF "!CARG!"=="-of13" (
       SET OF13=1
       SHIFT
       GOTO :LOOP
    )

    SET BUNDLEFILTER= !BUNDLEFILTER! !CARG!
    SHIFT
    GOTO :LOOP 
)

dir /b configuration\initial>initial.txt
dir /b configuration\initial.available>initial.available.txt

REM clean available optional configurations
for /f %%b in (initial.available.txt) do (
      set foob=%%b
            for /f %%a in (initial.txt) do (
                set fooa=%%a
                if !fooa!==!foob! (
                     del configuration\initial\!foob!
                ) 
            )
)

del initial.txt
del initial.available.txt

dir /b configuration\initial>initial.txt
dir /b configuration\initial.available>initial.available.txt

SET fifi="^^(?^!org.opendaylight.(openflowplugin^|openflowjava)).*"
IF "%OF13%" NEQ "0" (

copy configuration\initial.available\42-openflowplugin.xml configuration\initial\
SET fifi="^^(?^!org.opendaylight.controller.(thirdparty.org.openflow^|protocol_plugins.openflow)).*"
)

del initial.txt
del initial.available.txt

%CONTROLLER_RUNBAT% "-Dfelix.fileinstall.filter=!fifi!" %BUNDLEFILTER%
