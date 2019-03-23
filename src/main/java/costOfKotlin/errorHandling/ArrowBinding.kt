package costOfKotlin.errorHandling

import arrow.core.*
import arrow.core.extensions.either.monad.binding
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


private fun parse(s: String): Either<ParseFailure, Int> =
    when (s) {
        "1" -> Right(1)
        "2" -> Right(2)
        else -> Left(ParseFailure)
    }

private fun combine(s: String, t: String): Either<ParseFailure, Int> =
    binding {
        val (a) = parse(s)
        val (b) = parse(t)
        a + b
    }

open class ArrowBinding {
    @Benchmark
    fun success(blackhole: Blackhole) {
        blackhole.consume(combine("1", "2"))
    }
    
    @Benchmark
    fun failure(blackhole: Blackhole) {
        blackhole.consume(combine("x", "2"))
    }
}
