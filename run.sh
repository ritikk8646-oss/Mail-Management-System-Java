#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/mailapp/*.java
java -cp out mailapp.Main
