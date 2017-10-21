package costOfKotlin.properties;

import org.openjdk.jmh.annotations.Benchmark;

public class JavaProperties {

    @Benchmark
    public String field_access(JavaState state) {
        return state.field;
    }

    @Benchmark
    public String method_access(JavaState state) {
        return state.getConstant();
    }
}
