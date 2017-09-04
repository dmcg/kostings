#!/usr/bin/env bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}

mvn clean package

./benchmark.sh strings
./benchmark.sh primitives
./benchmark.sh let

