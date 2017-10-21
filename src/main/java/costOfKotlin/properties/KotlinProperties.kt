package costOfKotlin.properties

import org.openjdk.jmh.annotations.Benchmark

open class KotlinProperties {

    @Benchmark
    fun property_access(state: KotlinState): String {
        return state.withBackingField
    }

    @Benchmark
    fun no_backing_field_property_access(state: KotlinState): String {
        return state.noBackingField
    }

    @Benchmark
    fun method_access(state: KotlinState): String {
        return state.getConstant()
    }
}
