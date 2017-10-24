package costOfKotlin

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
open class ObjectsState {

    val objects = List(10000) {
        "hello " + it
    }
}