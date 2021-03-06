package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.let.KotlinLet
import org.junit.Test

class LetTests {

    @Test
    fun `let is marginally undetectable`() {
        assertThat(KotlinLet::baseline, probablyDifferentTo(KotlinLet::let))
        assertThat(KotlinLet::baseline, ! probablyFasterThan(KotlinLet::let))
    }
}