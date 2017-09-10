#!/usr/bin/env bash

#theory is that maybe there isn't enough warmup so lots of variation
./benchmark.sh -f 1 -wi 10 -i 100 -o run1 baselines
./benchmark.sh -f 1 -wi 10 -i 100 -o run2 baselines
./benchmark.sh -f 1 -wi 10 -i 100 -o run3 baselines
./benchmark.sh -f 1 -wi 10 -i 100 -o run4 baselines
./benchmark.sh -f 1 -wi 10 -i 100 -o run5 baselines

./benchmark.sh -f 1 -wi 100 -i 100 -o run1 baselines
./benchmark.sh -f 1 -wi 100 -i 100 -o run2 baselines
./benchmark.sh -f 1 -wi 100 -i 100 -o run3 baselines
./benchmark.sh -f 1 -wi 100 -i 100 -o run4 baselines
./benchmark.sh -f 1 -wi 100 -i 100 -o run5 baselines

# and lets try defaults

./benchmark.sh -f 10 -wi 20 -i 20 -o run1 baselines
./benchmark.sh -f 10 -wi 20 -i 20 -o run2 baselines
./benchmark.sh -f 10 -wi 20 -i 20 -o run3 baselines
./benchmark.sh -f 10 -wi 20 -i 20 -o run4 baselines
./benchmark.sh -f 10 -wi 20 -i 20 -o run5 baselines

# and then same number of iterations, but 1 fork
./benchmark.sh -f 1 -wi 20 -i 200 -o run1 baselines
./benchmark.sh -f 1 -wi 20 -i 200 -o run2 baselines
./benchmark.sh -f 1 -wi 20 -i 200 -o run3 baselines
./benchmark.sh -f 1 -wi 20 -i 200 -o run4 baselines
./benchmark.sh -f 1 -wi 20 -i 200 -o run5 baselines