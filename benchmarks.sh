#!/usr/bin/env bash

for i in `seq 1 100`;
do
    NAME=run$i
    echo $NAME
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME baselines
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME primitives
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME strings
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME invoking
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME mapping
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME let
    ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME sets
done