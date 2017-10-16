package costOfKotlin.invoking

import com.natpryce.hamkrest.and
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.primitives.IntState
import org.openjdk.jmh.annotations.Benchmark
import kotlin.reflect.KFunction

open class Invoking {

    @Benchmark
    fun baseline(intState: IntState) : Int {
        return aFunction(intState.randomInt)
    }

    @Benchmark
    fun invoke_via_reference(intState: IntState) : Int {
        // not so smart here, compiler
        return (::aFunction)(intState.randomInt)
    }

    @Benchmark
    fun passed_as_lambda(intState: IntState) : Int {
        return applier(intState.randomInt) { aFunction(it ) }
    }

    @Benchmark
    fun passed_as_function_reference(intState: IntState) : Int {
        return applier(intState.randomInt, ::aFunction)
    }

    @Benchmark
    fun passed_as_value_of_function_type(intState: IntState) : Int {
        return applier(intState.randomInt, aBlock)
    }
}

fun probablyFasterButNotByMoreThan(other: KFunction<*>, aFactorOf: Double) = probablyFasterThan(other) and ! probablyFasterThan(other, byAFactorOf = aFactorOf)

fun aFunction(i: Int) = 2 * i

val aBlock: (Int) -> Int = { 2 * it }

inline fun <T> applier(t: T, f: (T) -> T) = f(t)