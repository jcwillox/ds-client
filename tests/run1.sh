#!/usr/bin/env bash
shopt -s globstar

# ensure we are in the same directory as the script
DIR=$(dirname "$0")
cd "$DIR" || exit

echo "compiling java files"
javac "$(realpath ../src/main)"/**/*.java -d . -verbose

# prevent test script from killing this process
trap -- '' SIGTERM
trap "rm -rf ./main" SIGINT

echo "running tests"
bash ./tests1.sh main/Main.class

rm -rf ./main
echo "removed generated class files"

echo "difference: '$(cat ./S1testConfigs/stage1.diff)'"