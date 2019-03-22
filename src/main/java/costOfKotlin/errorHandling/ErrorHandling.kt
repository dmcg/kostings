package costOfKotlin.errorHandling

import arrow.core.*
import arrow.core.extensions.either.monad.binding
import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.onFailure
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

object ParseFailure

fun result4kParse(s: String): Result<Int, ParseFailure> =
    when (s) {
        "1" -> Success(1)
        "2" -> Success(2)
        else -> Failure(ParseFailure)
    }

fun result4kCombine(s: String, t: String): Result<Int, ParseFailure> {
    val a = result4kParse(s).onFailure { return it }
    val b = result4kParse(t).onFailure { return it }
    return Success(a + b)
}

fun arrowParse(s: String): Either<ParseFailure, Int> =
    when (s) {
        "1" -> Right(1)
        "2" -> Right(2)
        else -> Left(ParseFailure)
    }

fun arrowCombine(s: String, t: String): Either<ParseFailure, Int> =
    binding {
        val (a) = arrowParse(s)
        val (b) = arrowParse(t)
        a + b
    }


fun exceptionsParse(s: String): Int =
    when (s) {
        "1" -> 1
        "2" -> 2
        else -> throw IllegalArgumentException(s)
    }

fun exceptionsCombine(s: String, t: String) =
    exceptionsParse(s) + exceptionsParse(t)

fun nullableParse(s: String): Int? =
    when (s) {
        "1" -> 1
        "2" -> 2
        else -> null
    }

fun nullableCombine(s: String, t: String): Int? =
    nullableParse(s)?.let { parsed_s ->
        nullableParse(t)?.let { parsed_t ->
            return parsed_s + parsed_t
        }
    }


open class ErrorHandling {
    @Benchmark
    fun success_with_nulls(blackhole: Blackhole) {
        blackhole.consume(nullableCombine("1", "2"))
    }
    
    @Benchmark
    fun failure_with_nulls(blackhole: Blackhole) {
        blackhole.consume(nullableCombine("x", "2"))
    }
    
    @Benchmark
    fun success_with_exceptions(blackhole: Blackhole) {
        blackhole.consume(exceptionsCombine("1", "2"))
    }
    
    @Benchmark
    fun failure_with_exceptions(blackhole: Blackhole) {
        blackhole.consume(exceptionsCombine("x", "2"))
    }
    
    @Benchmark
    fun success_with_result4k(blackhole: Blackhole) {
        blackhole.consume(result4kCombine("1", "2"))
    }
    
    @Benchmark
    fun failure_with_result4k(blackhole: Blackhole) {
        blackhole.consume(result4kCombine("x", "2"))
    }
    
    @Benchmark
    fun success_with_arrow(blackhole: Blackhole) {
        blackhole.consume(arrowCombine("1", "2"))
    }
    
    @Benchmark
    fun failure_with_arrow(blackhole: Blackhole) {
        blackhole.consume(arrowCombine("x", "2"))
    }
}
