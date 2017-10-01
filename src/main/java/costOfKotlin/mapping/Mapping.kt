package costOfKotlin.mapping

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import kotlin.reflect.KFunction

open class Mapping {

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
    fun baseline_iterator_arrayList(listState: ListState) : List<String> {
        val list = listState.arrayListOfStrings
        val result = ArrayList<String>(list.size)
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            result.add(iterator.next())
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
    fun specialisedMap_arrayList(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.specialisedMap { it }
    }

    @Benchmark
    fun specialisedMap_linkedList(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.specialisedMap { it }
    }

    @Test
    fun `use of iterator rather than indexing is crippling`() {
        assertThat(this::baseline_indexed_arrayList, probablyFasterByBetween(this::baseline_iterator_arrayList, 25.0, 30.0))
    }

    @Test
    fun `indexedMap is much better for both lists`() {
        assertThat(this::indexedMap_arrayList, probablyFasterByBetween(this::map_arrayList, 30.0, 35.0))

        assertThat(this::indexedMap_linkedList, probablyFasterByBetween(this::map_linkedList, 30.0, 35.0))
            // This may be a bit unfair because of all nodes are created at the same time
    }

    @Test
    fun `specialised map can choose`() {
        assertThat(this::specialisedMap_arrayList, probablyFasterByBetween(this::map_arrayList, 30.0, 35.0))
        assertThat(this::map_linkedList, ! probablyDifferentTo(this::specialisedMap_linkedList))
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
