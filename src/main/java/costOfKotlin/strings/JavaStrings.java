
package costOfKotlin.strings;

import org.openjdk.jmh.annotations.Benchmark;

@SuppressWarnings("StringBufferReplaceableByString")
public class JavaStrings {

    @Benchmark
    public String concat(StringState state) {
        return state.greeting + " " + state.subject;
    }

    @Benchmark
    public String desugared_concat(StringState state) {
        return new StringBuilder().append(state.greeting).append(" ").append(state.subject).toString();
    }

    @Benchmark
    public String optimized_concat(StringState state) {
        return new StringBuilder(state.greeting).append(" ").append(state.subject).toString();
    }
}