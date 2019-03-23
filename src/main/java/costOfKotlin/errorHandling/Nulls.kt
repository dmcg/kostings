package costOfKotlin.errorHandling

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


private fun parse(s: String): Int? =
    when (s) {
        "1" -> 1
        "2" -> 2
        else -> null
    }

private fun combine(s: String, t: String): Int? =
    parse(s)?.let { parsed_s ->
        parse(t)?.let { parsed_t ->
            return parsed_s + parsed_t
        }
    }


open class Nulls {
    @Benchmark
    fun success(blackhole: Blackhole) {
        blackhole.consume(combine("1", "2"))
    }
    
    @Benchmark
    fun failure(blackhole: Blackhole) {
        try {
            blackhole.consume(combine("x", "2"))
        }
        catch (e: ExampleException) {
        }
    }
}
