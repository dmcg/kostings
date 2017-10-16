package costOfKotlin.baselines

import costOfKotlin.strings.StringState
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

open class KotlinBaseline {

    @Benchmark
    fun baseline(state: StringState, blackhole: Blackhole) {
        blackhole.consume(state)
    }
}
