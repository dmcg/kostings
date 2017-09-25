package costOfKotlin.strings

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

open class KotlinStrings {

    @Benchmark
    fun baseline(state: StringState, blackhole: Blackhole) {
        blackhole.consume(state.hello)
        blackhole.consume(state.world)
    }

    @Benchmark
    fun concat(state: StringState, blackhole: Blackhole) {
        /* There is an extra append("") compared to the Java
         */
        blackhole.consume("${state.hello} ${state.world}")
    }

    fun `the compiler optimizes this to a constant`() = "${"hello"} ${"world"}"

    fun `and even this`() = "${"${"hello" + " " + "world"}"}"

    fun `but not this`() = "$hello $world"


    @Test
    fun `cannot detect the difference between Java and Kotlin`() {
        assertThat(JavaStrings::concat, ! probablyDifferentTo(this::concat))
    }


}

private val hello = "hello"
private val world = "world"
