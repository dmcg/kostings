package costOfKotlin.mapLike

import org.openjdk.jmh.annotations.Benchmark
import java.util.*
import kotlin.collections.LinkedHashMap

open class Maps {

    @Benchmark
    fun hashMap(state: ObjectsState): List<String> {
        return HashMap<String, String>(state.objects.size)
            .filledWith(state.objects)
            .everyValueTheHardWay()
    }

    @Benchmark
    fun treeMap(state: ObjectsState): List<String> {
        return LinkedHashMap<String, String>(state.objects.size)
            .filledWith(state.objects)
            .everyValueTheHardWay()
    }
}

private fun <T> MutableMap<T, T>.filledWith(objects: Iterable<T>): Map<T, T> {
    for (element in objects) {
        put(element, element)
    }
    return this
}

private fun Map<String, String>.everyValueTheHardWay(): List<String> = keys.map {
    this[it]!!
}

