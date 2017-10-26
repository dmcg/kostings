package costOfKotlin.properties

import org.openjdk.jmh.annotations.Benchmark

open class KotlinProperties {

    @Benchmark
    fun field_property(state: KotlinState): String {
        return state.fieldProperty
    }

    @Benchmark
    fun method_property(state: KotlinState): String {
        return state.methodProperty
    }

    @Benchmark
    fun constant_method(state: KotlinState): String {
        return state.getConstant()
    }
}
