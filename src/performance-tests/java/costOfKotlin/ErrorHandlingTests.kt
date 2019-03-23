package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.errorHandling.ArrowBinding
import costOfKotlin.errorHandling.Exceptions
import costOfKotlin.errorHandling.Nulls
import costOfKotlin.errorHandling.Result4k
import org.junit.Test

class ErrorHandlingTests {
    
    @Test
    fun success() {
        assertThat(Exceptions::success, probablyFasterThan(Nulls::success))
        assertThat(Nulls::success, probablyFasterThan(Result4k::success))
        assertThat(Result4k::success, probablyFasterThan(ArrowBinding::success))
    }
    
    @Test
    fun failure() {
        assertThat(Result4k::failure, probablyFasterThan(ArrowBinding::failure))
    }
}