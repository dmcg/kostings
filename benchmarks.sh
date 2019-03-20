#!/usr/bin/env bash

tests=${@:-
    errorHandling
    mapLike
    baselines
    primitives
    strings
    invoking
    mapping
    let
    properties
}

for i in $(seq 1 100)
do
    NAME=run$i
    echo $NAME

    for test in ${tests}
    do
        echo ./benchmark.sh -f 1 -wi 20 -i 500 -o $NAME $test
    done
done
