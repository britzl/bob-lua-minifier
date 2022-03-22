#!/usr/bin/env bash

rm -rf build
rm sources.txt
rm obfuscator.jar

find . -name "*.java" > sources.txt
javac -d ./build @sources.txt
jar cvf obfuscator.jar -C build .
