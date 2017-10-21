package costOfKotlin.properties;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JavaState {

    public String field = "hello";

    public final String getConstant() {
        return "hello";
    }
}
