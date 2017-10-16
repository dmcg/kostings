package costOfKotlin.baselines;

import costOfKotlin.strings.StringState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaBaseline {

    @Benchmark
    public void baseline(StringState state, Blackhole blackhole) {
        blackhole.consume(state);
    }
}
