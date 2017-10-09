
A set of benchmarks designed to show the cost of Kotlin language features.

## Principle Of Operation

Kostings uses the [JMH](http://openjdk.java.net/projects/code-tools/jmh/) microbenchmark framework to measure the throughput of benchmark methods. It then uses JUnit to test assertions about the results.

## To Run Benchmarks

JMH requires a sophisticated Maven build, with code-generation-n-shit. After every change to actual benchmark code or its dependencies, rebuild the benchmarks with

```bash
mvn clean package
```

To run the default set of benchmarks with the default number of runs, read the section on [Running Reliable Benchmarks](#Running-Reliable-Benchmarks) and then run

```bash
./benchmark.sh
```

This will tie your computer up for hours to populate the `results` directory and plot some nice graphs in `results/images`. 

The results directory is then used as a cache - results for a given benchmark configuration will be read from the directory or rebuilt if they are not there.

## To Run Tests

The JUnit tests assume that the benchmarks have already been run - this allows fast iteration on the tests once data has been gathered.

Because benchmarks may be run with different parameters and in different batches, the tests don't load results from the `results` directory. Instead they load from `canonical-results`. For now, just copy the files from `results` to `canonical-results`. (If more than one file has results for a benchmark, the results from all files will be merged). Then you should be able to invoke the `main` in `com/oneeyedmen/kostings/testing.kt` from your IDE, or point at an individual test in your IDE and run it there. 

Tests are able to reference benchmark results with function references, viz

```kotlin
open class KotlinBaseline {

    @Benchmark
    fun baseline(state: StringState, blackhole: Blackhole) {
        blackhole.consume(state)
    }

    @Test
    fun `java is quicker but not by much`() {
        assertThat(JavaBaseline::baseline, probablyFasterThan(this::baseline, byAFactorOf = 0.005))
        assertThat(JavaBaseline::baseline, ! probablyFasterThan(this::baseline, byAFactorOf = 0.01))
    }

}
```

Note that the matchers are becoming statistically sophisticated. probablyFasterThan/probablySlowerThan/probablyDifferentTo all use T-Tests to statistically compare the means between benchmarks. Optional byAFactorOf parameters allow testing of the scale of the difference.

## Aspects of Performance to Investigate

These are the sort of things I'd like to write tests for

* Nullable primitives (boxing)
* Non-nullable references (assertions)
* String interpolation 
* Collections of primitives
* Use of TreeMap TreeSet etc
* Iterables vs Sequences
* Inlining
* Calling Lambda
* Null-coalesing and elvis
* Method references
* Java 6 vs 8
* Top-level functions 
* Companion objects
* Synchronisation (delegates) 

## Running Reliable Benchmarks

To get reliably results, you want your computer to be doing as little else as possible. Close your IDE and all other extraneous apps, and run the benchmark from a vanilla Bash prompt. [More tips](http://oneeyedmen.com/benchmarks.html)