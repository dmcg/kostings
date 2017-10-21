package costOfKotlin.baselines

import org.openjdk.jmh.annotations.Benchmark

open class KotlinBaseline {

    @Benchmark
    fun baseline(state: EmptyState): EmptyState{
        return state
    }
}
