package costOfKotlin.properties

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
open class KotlinState {

    val fieldProperty = "hello"

    val methodProperty get() = "hello"

    fun getConstant() = "hello"
}
