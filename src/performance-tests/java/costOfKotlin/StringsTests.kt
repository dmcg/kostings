package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.strings.JavaStrings
import costOfKotlin.strings.KotlinStrings
import org.junit.Test

class StringsTests {

    @Test
    fun `Java is quicker but not by much`() {
        assertThat(JavaStrings::concat, probablyFasterThan(KotlinStrings::concat, byMoreThan = 0.05, butNotMoreThan = 0.10))
    }


}
