#!/usr/bin/env bash
set -e

FORKS=10
WARMUPS=3
ITERATIONS=20
OUTPUT=target/$1

java -jar target/benchmarks.jar -f $FORKS -wi $WARMUPS -i $ITERATIONS -tu ms -rf CSV -rff ${OUTPUT}.csv "$1"
python plot.py ${OUTPUT}.csv ${OUTPUT}.png
