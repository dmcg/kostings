package costOfKotlin.sets

import costOfKotlin.ObjectsState
import org.openjdk.jmh.annotations.Benchmark


open class Sets {

    @Benchmark fun baseline(state: ObjectsState): List<String> {
        return HashSet(state.objects).map {
            it + " "
        }
    }

    @Benchmark fun hashSet(state: ObjectsState): List<String> {
        return state.objects.toSet().map {
            it + " "
        }
    }
}