package costOfKotlin.mapping

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.*

@State(Scope.Thread)
open class ListState {
    val arrayListOfStrings = List(10000, Int::toString)
    val linkedListOfStrings = LinkedList(arrayListOfStrings)
}