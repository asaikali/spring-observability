#!/bin/sh
set -x
../mvnw clean -Pnative native:compile
ls -lah target/quotes-jvm