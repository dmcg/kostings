package costOfKotlin.mapping

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import java.util.function.Consumer
import kotlin.reflect.KFunction
import kotlin.test.assertEquals

open class KotlinMapping {

    @Benchmark
    fun baseline_indexed_arrayList(listState: ListState) : List<String> {
        val list = listState.arrayListOfStrings
        val result = ArrayList<String>(list.size)
        for (i in 0 until list.size) {
            result.add(list[i])
        }
        return result
    }

    @Benchmark
    fun map_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.map { it }
    }

    @Benchmark
    fun map_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.map { it }
    }

    @Benchmark
    fun indexedMap_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.indexedMap { it }
    }

    @Benchmark
    fun indexedMap_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.indexedMap { it }
    }

    @Benchmark
    fun specialised_map_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.specialisedMap { it }
    }

    @Benchmark
    fun specialised_map_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.specialisedMap { it }
    }

    @Benchmark
    fun spliterator_map_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.spliteratorMap { it }
    }

    @Benchmark
    fun spliterator_map_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.spliteratorMap { it }
    }

    @Benchmark
    fun foreach_map_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.forEachMap { it }
    }

    @Benchmark
    fun foreach_map_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.forEachMap { it }
    }

    @Test
    fun `on arrayList map is quite a lot slower than indexed access`() {
        assertThat(this::baseline_indexed_arrayList, probablyFasterByBetween(this::map_arrayList, 0.2, 0.3))
    }

    @Test
    fun spliteratorMap() {
        val list = listOf("hello", "world")
        assertEquals(list.map { it.length }, list.spliteratorMap { it.length })
    }

}

fun probablyFasterByBetween(reference: KFunction<*>, minFactor: Double, maxFactor: Double) =
    probablyFasterThan(reference, byAFactorOf = minFactor) and ! probablyFasterThan(reference, byAFactorOf = maxFactor)

inline fun <T, R> List<T>.indexedMap(transform: (T) -> R): List<R> {
    val result = ArrayList<R>(this.size)
    for (i in 0 until size) {
        result.add(transform(this.get(i)))
    }
    return result
}

inline fun <T, R> List<T>.specialisedMap(transform: (T) -> R): List<R> = when {
    this is RandomAccess -> indexedMap(transform)
    else -> map(transform)
}

inline fun <T, R> List<T>.spliteratorMap(crossinline transform: (T) -> R) : List<R>{
    val result = ArrayList<R>(this.size)
    spliterator().forEachRemaining() { result.add(transform(it)) }
    return result
}

inline fun <T, R> List<T>.forEachMap(crossinline transform: (T) -> R) : List<R>{
    val result = ArrayList<R>(this.size)
    this.forEach(Consumer { t -> result.add(transform(t)) })
    return result
}