#!/bin/sh

if ( [ -e dist/WipeFreeSpaceGUI2.jar ] ); then
	java -jar -Duser.language=en -Duser.country=US dist/WipeFreeSpaceGUI2.jar
elif ( [ -e WipeFreeSpaceGUI2.jar ] ); then
	java -jar -Duser.language=en -Duser.country=US WipeFreeSpaceGUI2.jar
fi
