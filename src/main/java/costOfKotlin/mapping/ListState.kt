package costOfKotlin.mapping

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class ListState {
    val listOfStrings = List(10000, Int::toString)
}