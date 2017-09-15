#!/usr/bin/env bash

# lets try defaults

./benchmark.sh -f 10 -wi 20 -i 20 -o run1
./benchmark.sh -f 10 -wi 20 -i 20 -o run2
./benchmark.sh -f 10 -wi 20 -i 20 -o run3
./benchmark.sh -f 10 -wi 20 -i 20 -o run4
./benchmark.sh -f 10 -wi 20 -i 20 -o run5

# and then same number of iterations, but 1 fork
#./benchmark.sh -f 1 -wi 20 -i 200 -o run1
#./benchmark.sh -f 1 -wi 20 -i 200 -o run2
#./benchmark.sh -f 1 -wi 20 -i 200 -o run3
#./benchmark.sh -f 1 -wi 20 -i 200 -o run4
#./benchmark.sh -f 1 -wi 20 -i 200 -o run5