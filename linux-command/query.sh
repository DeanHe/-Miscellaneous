#!/usr/bin/env bash
#  check input number
if [ "$#" -ne 1 ]; then
  echo "illegal number of arguments"
  exit 1
fi

# check if directory exists
if ! [ -e "$1" ]; then
  echo "$1 not found" >&2
  exit 1
fi

if ! [ -d "$1" ]; then
  echo "$1 not a directory" >&2
  exit 1
fi

# run java code 
javac QueryHandler.java 
java QueryHandler "$1" 

