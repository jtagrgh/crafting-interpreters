#!/bin/bash

if [ -d jtagrgh/lox/ ]
then
    pushd jtagrgh/lox/ 
    javac *.java
    popd
fi
