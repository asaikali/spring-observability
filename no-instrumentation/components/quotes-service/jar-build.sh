#!/bin/sh
set -x
../../mvnw clean package
ls -lah target/*.jar
