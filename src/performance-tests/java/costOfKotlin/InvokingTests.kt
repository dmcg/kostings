package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.invoking.Invoking
import org.junit.Test

class InvokingTests {
    

    @Test
    fun `invoke via reference is slower than plain invocation`() {
        assertThat(Invoking::baseline, probablyFasterThan(Invoking::invoke_via_reference, byMoreThan = 0.20, butNotMoreThan = 0.25))
    }

    @Test
    fun `inlined invocation is marginally slower`() {
        assertThat(Invoking::baseline, probablyFasterThan(Invoking::passed_as_lambda, byMoreThan = 0.00, butNotMoreThan = 0.01))
    }

    @Test
    fun `lambda and function reference are indistinguishable`() {
        assertThat(Invoking::passed_as_lambda, !probablyDifferentTo(Invoking::passed_as_function_reference))
    }

    @Test
    fun `value of function type is like via reference`() {
        assertThat(Invoking::passed_as_lambda, probablyFasterThan(Invoking::passed_as_value_of_function_type, byMoreThan = 0.20, butNotMoreThan = 0.25))
    }

}

