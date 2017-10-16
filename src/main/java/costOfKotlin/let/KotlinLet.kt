package costOfKotlin.let

import costOfKotlin.primitives.IntState
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


open class KotlinLet {

    @Benchmark
    fun baseline(state: IntState, blackhole: Blackhole) {
        val v = state.randomInt + 1
        blackhole.consume(v)
    }

    @Benchmark
    fun let(state: IntState, blackhole: Blackhole) = state.randomInt.plus(1).let {
        blackhole.consume(it)
    }
}