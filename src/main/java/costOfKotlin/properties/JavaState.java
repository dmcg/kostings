package costOfKotlin.properties;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JavaState {

    public String field = "hello";

    public String getField() {
        return field;
    }

    public String getConstant() {
        return "hello";
    }
}
