package costOfKotlin.properties

import org.openjdk.jmh.annotations.Benchmark

class KotlinProperties {

    @Benchmark
    fun property_access(state: KotlinState): String {
        return state.witbBackingField
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
