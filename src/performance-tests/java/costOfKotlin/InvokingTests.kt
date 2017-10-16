package costOfKotlin

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.invoking.Invoking
import org.junit.Test
import kotlin.reflect.KFunction

class InvokingTests {
    

    @Test
    fun `invoke via reference is slower than plain invocation`() {
        assertThat(Invoking::baseline, probablyFasterThan(Invoking::invoke_via_reference, byAFactorOf = 0.01))
        assertThat(Invoking::baseline, !probablyFasterThan(Invoking::invoke_via_reference, byAFactorOf = 0.02))
    }

    @Test
    fun `lambda and function reference are indistinguishable`() {
        assertThat(Invoking::passed_as_lambda, !probablyDifferentTo(Invoking::passed_as_function_reference))
    }

    @Test
    fun `value of function type is like via reference`() {
        assertThat(Invoking::passed_as_lambda, probablyFasterButNotByMoreThan(Invoking::passed_as_value_of_function_type, aFactorOf = 0.02))
    }


}

fun probablyFasterButNotByMoreThan(other: KFunction<*>, aFactorOf: Double) = probablyFasterThan(other) and !probablyFasterThan(other, byAFactorOf = aFactorOf)
