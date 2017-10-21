package costOfKotlin.properties

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KotlinState {

    val withBackingField = "hello"

    val noBackingField get() = "hello"

    fun getConstant(): String {
        return "hello"
    }
}
