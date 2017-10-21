package costOfKotlin.properties

import org.openjdk.jmh.annotations.Benchmark

class KotlinProperties {

    @Benchmark
    fun field_access(state: KotlinState): String {
        return state.field
    }

    @Benchmark
    fun method_access(state: KotlinState): String {
        return state.getConstant()
    }
}
