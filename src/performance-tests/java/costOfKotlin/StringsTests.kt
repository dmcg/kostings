package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import costOfKotlin.strings.JavaStrings
import costOfKotlin.strings.KotlinStrings
import org.junit.Test

class StringsTests {

    @Test
    fun `cannot detect the difference between Java and Kotlin`() {
        assertThat(JavaStrings::concat, !probablyDifferentTo(KotlinStrings::concat))
    }


}
