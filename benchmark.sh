#!/usr/bin/env bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

mvn clean install
java -jar target/benchmarks.jar -f 1 -wi 5 -i 5