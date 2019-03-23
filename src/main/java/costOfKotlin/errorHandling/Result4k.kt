package costOfKotlin.errorHandling

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.onFailure
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

private fun parse(s: String): Result<Int, ParseFailure> =
    when (s) {
        "1" -> Success(1)
        "2" -> Success(2)
        else -> Failure(ParseFailure)
    }

private fun combine(s: String, t: String): Result<Int, ParseFailure> {
    val a = parse(s).onFailure { return it }
    val b = parse(t).onFailure { return it }
    return Success(a + b)
}

open class Result4k {
    @Benchmark
    fun success(blackhole: Blackhole) {
        blackhole.consume(combine("1", "2"))
    }
    
    @Benchmark
    fun failure(blackhole: Blackhole) {
        blackhole.consume(combine("x", "2"))
    }
}
