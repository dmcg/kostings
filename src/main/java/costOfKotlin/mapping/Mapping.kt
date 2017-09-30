package costOfKotlin.mapping

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark

open class Mapping {

    @Benchmark
    fun baseline(listState: ListState) : List<String> {
        val list = listState.arrayListOfStrings
        val result = ArrayList<String>(list.size)
        for (i in 0 until result.size) {
            result.add(list.get(i))
        }
        return result
    }

    @Benchmark
    fun baseline_iterator(listState: ListState) : List<String> {
        val list = listState.arrayListOfStrings
        val result = ArrayList<String>(list.size)
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        return result
    }

    @Benchmark
    fun map_identity(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.map { it }
    }

    @Benchmark
    fun map_identityFunction(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.map(identity())
    }

    @Benchmark
    fun indexedMap(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.indexedMap { it }
    }

    @Benchmark
    fun indexedMap_on_linked_list(listState: ListState) : List<String> {
        // the nodes will not be scattered in memory
        return listState.linkedListOfStrings.indexedMap { it }
    }

    @Benchmark
    fun specialisedMap(listState: ListState) : List<String> {
        return listState.arrayListOfStrings.specialisedMap { it }
    }

    @Benchmark
    fun specialisedMap_on_linked_list(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.specialisedMap { it }
    }

    @Benchmark
    fun map_on_linked_list(listState: ListState) : List<String> {
        return listState.linkedListOfStrings.map{ it }
    }

    @Test
    fun `use of iterator is crippling`() {
        assertThat(this::baseline, probablyFasterThan(this::baseline_iterator, byAFactorOf = 25.0))
        assertThat(this::baseline, ! probablyFasterThan(this::baseline_iterator, byAFactorOf = 30.0))
    }

    @Test
    fun `we could do so much better`() {
        assertThat(this::baseline, ! probablyFasterThan(this::specialisedMap, byAFactorOf = 0.05))
        assertThat(this::map_on_linked_list, ! probablyDifferentTo(this::specialisedMap_on_linked_list))
    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun<T> identity(): (T) -> T = { it }

inline fun <T, R> List<T>.indexedMap(transform: (T) -> R): List<R> {
    val result = ArrayList<R>(this.size)
    for (i in 0 until result.size) {
        result.add(transform(this.get(i)))
    }
    return result
}

inline fun <T, R> List<T>.specialisedMap(transform: (T) -> R): List<R> = when {
    this is RandomAccess -> indexedMap(transform)
    else -> map(transform)
}