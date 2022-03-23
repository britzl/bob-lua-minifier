#!/usr/bin/env bash

JAR=obfuscator.jar

rm -rf build
rm sources.txt
rm ${JAR}

# gathering source files
find . -name "*.java" > sources.txt

# compiling source files
javac -d ./build @sources.txt

# creating jar
jar cvf ${JAR} -C build .
jar -uf ${JAR} minify.lua

