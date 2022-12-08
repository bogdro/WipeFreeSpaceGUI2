@echo off
if exist dist\WipeFreeSpaceGUI2.jar java -jar -Duser.language=en -Duser.country=US dist\WipeFreeSpaceGUI2.jar
if exist WipeFreeSpaceGUI2.jar  java -jar -Duser.language=en -Duser.country=US WipeFreeSpaceGUI2.jar
