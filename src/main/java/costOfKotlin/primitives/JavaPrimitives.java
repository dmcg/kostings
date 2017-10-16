
package costOfKotlin.primitives;

import org.openjdk.jmh.annotations.Benchmark;

public class JavaPrimitives {

    @Benchmark
    public int _1_baseline(IntState state) {
        return state.get_41();
    }

    @Benchmark
    public int _2_sum(IntState state) {
        return state.get_41() + 1;
    }
}
