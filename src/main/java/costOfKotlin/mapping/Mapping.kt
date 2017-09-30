package costOfKotlin.mapping

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyDifferentTo
import com.oneeyedmen.kostings.probablyFasterThan
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark

open class Mapping {

    @Benchmark
    fun baseline(listState: ListState) : List<String> {
        val list = listState.listOfStrings
        val result = ArrayList<String>(list.size)
        for (i in 0 until result.size) {
            result.add(list.get(i))
        }
        return result
    }

    @Benchmark
    fun baseline_iterator(listState: ListState) : List<String> {
        val list = listState.listOfStrings
        val result = ArrayList<String>(list.size)
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        return result
    }

    @Benchmark
    fun map_identity(listState: ListState) : List<String> {
        return listState.listOfStrings.map { it }
    }

    @Benchmark
    fun map_identityFunction(listState: ListState) : List<String> {
        return listState.listOfStrings.map(identity())
    }

    @Benchmark
    fun specialised_map_identity(listState: ListState) : List<String> {
        return listState.listOfStrings.listMap { it }
    }

    @Test
    fun `use of iterator is crippling`() {
        assertThat(this::baseline, probablyFasterThan(this::baseline_iterator, byAFactorOf = 25.0))
        assertThat(this::baseline, ! probablyFasterThan(this::baseline_iterator, byAFactorOf = 30.0))
    }

    @Test
    fun `we could do so much better`() {
        assertThat(this::baseline, ! probablyDifferentTo(this::specialised_map_identity))
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun<T> identity(): (T) -> T = { it }

inline fun <T, R> List<T>.listMap(transform: (T) -> R): List<R> {
    val result = ArrayList<R>(this.size)
    for (i in 0 until result.size) {
        result.add(transform(this.get(i)))
    }
    return result
}