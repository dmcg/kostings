package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import costOfKotlin.errorHandling.ErrorHandling
import org.junit.Test

class ErrorHandlingTests {

    @Test
    fun failure() {
        assertThat(ErrorHandling::failure_with_arrow, probablyDifferentTo(ErrorHandling::failure_with_result4k))
    }
    
    @Test
    fun success() {
        assertThat(ErrorHandling::success_with_arrow, probablyDifferentTo(ErrorHandling::success_with_result4k))
    }
}