#!/bin/sh

if ( [ -e dist/WipeFreeSpaceGUI2.jar ] ); then
	java -jar dist/WipeFreeSpaceGUI2.jar
elif ( [ -e WipeFreeSpaceGUI2.jar ] ); then
	java -jar WipeFreeSpaceGUI2.jar
fi
