#!/usr/bin/env bash
set -e

WARMUPS=5
ITERATIONS=10

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

mvn clean package
java -jar target/benchmarks.jar -f 1 -wi $WARMUPS -i $ITERATIONS -tu ms  "$@"