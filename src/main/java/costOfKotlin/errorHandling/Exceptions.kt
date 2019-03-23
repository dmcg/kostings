package costOfKotlin.errorHandling

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


class ExampleException(s: String): Exception(s)

private fun parse(s: String): Int =
    when (s) {
        "1" -> 1
        "2" -> 2
        else -> throw ExampleException(s)
    }

private fun combine(s: String, t: String) =
    parse(s) + parse(t)


open class Exceptions {
    @Benchmark
    fun success(blackhole: Blackhole) {
        blackhole.consume(combine("1", "2"))
    }
    
    @Benchmark
    fun failure(blackhole: Blackhole) {
        blackhole.consume(combine("x", "2"))
    }
}
