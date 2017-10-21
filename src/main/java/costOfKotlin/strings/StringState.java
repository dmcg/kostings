package costOfKotlin.strings;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class StringState {
    public String greeting = "hello";
    public String subject = "world";
}
