#!bin/sh
javac -O -sourcepath src -classpath .:bin:lib/myLibrary.jar $1 -d bin
