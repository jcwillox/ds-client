#!/usr/bin/env bash
shopt -s globstar

# ensure we are in the same directory as the script
DIR=$(dirname "$0")
cd "$DIR" || exit

echo "INFO: compiling java files"
javac "$(realpath ../src/main)"/**/*.java -d .

export NO_LOGGING=true
echo "INFO: running tests"

./server.py --client "java main.Main" --config S1testConfigs/* -q "$@"

rm -rf ./main && echo "INFO: removed generated class files"
