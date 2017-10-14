
package costOfKotlin.strings;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class JavaStrings {

    @Benchmark
    public void baseline(StringState state, Blackhole blackhole) {
        blackhole.consume(state.getHello());
        blackhole.consume(state.getWorld());
    }

    @Benchmark
    public void concat(StringState state, Blackhole blackhole) {
        blackhole.consume(state.getHello() + " " + state.getWorld());
    }

    @Benchmark
    public void hand_rolled_concat(StringState state, Blackhole blackhole) {
        blackhole.consume(new StringBuilder().append(state.getHello()).append(" ").append(state.getWorld()).toString());
    }

    @Benchmark
    public void hand_rolled_concat_2(StringState state, Blackhole blackhole) {
        blackhole.consume(new StringBuilder(state.getHello()).append(" ").append(state.getWorld()).toString());
    }

    @Test
    public void dummy() {}

}