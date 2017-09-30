package costOfKotlin.invoking

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.primitives.IntState
import org.junit.Test
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

    @Test
    fun `pretty much indestinguishable`() {
        assertThat(this::baseline, probablyFasterThan(this::invoke_via_reference))
        assertThat(this::baseline, ! probablyDifferentTo(this::passed_as_lambda))
        assertThat(this::baseline, ! probablyDifferentTo(this::passed_as_function_reference))
        assertThat(this::baseline, probablyFasterButNotByMoreThan(this::passed_as_value_of_function_type, aFactorOf = 0.02))
    }


}

fun probablyFasterButNotByMoreThan(other: KFunction<*>, aFactorOf: Double) = probablyFasterThan(other) and ! probablyFasterThan(other, byAFactorOf = aFactorOf)

fun aFunction(i: Int) = 2 * i

val aBlock: (Int) -> Int = { 2 * it }

inline fun <T> applier(t: T, f: (T) -> T) = f(t)