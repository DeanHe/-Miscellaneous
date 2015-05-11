#!/usr/bin/env bash
#  check input number
if [ "$#" -ne 1 ]; then
  echo "illegal number of arguments"
  exit 1
fi
# create a directory
echo "Path: $1"
mkdir $1

# run java code
javac Generator.java
java Generator "$1"
