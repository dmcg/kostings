package costOfKotlin.strings

import org.openjdk.jmh.annotations.Benchmark

@Suppress("RemoveSingleExpressionStringTemplate")
open class KotlinStrings {

    @Benchmark
    fun concat(state: StringState): String {
        /* There is an extra append("") compared to the Java
           but Kotlin uses char 32 in between rather than " "
         */
        return "${state.greeting} ${state.subject}"
    }

    @Benchmark
    fun desugared_concat(state: StringState): String? {
        return StringBuilder().append(state.greeting).append(' ').append(state.subject).toString()
    }

    @Benchmark
    fun optimized_concat(state: StringState): String? {
        return StringBuilder(state.greeting).append(' ').append(state.subject).toString()
    }

    fun `the compiler optimizes this to a constant`() = "${"hello"} ${"world"}"

    fun `and even this`() = "${"${"hello" + " " + "world"}"}"

    fun `but not this`() = "$hello $world"
}

private val hello = "hello"
private val world = "world"
