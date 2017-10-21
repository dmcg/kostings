package costOfKotlin.baselines;

import org.openjdk.jmh.annotations.Benchmark;

public class JavaBaseline {

    @Benchmark
    public EmptyState baseline(EmptyState state) {
        return state;
    }
}
