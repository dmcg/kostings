package costOfKotlin

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.mapping.KotlinMapping
import costOfKotlin.mapping.spliteratorMap
import org.junit.Test
import kotlin.reflect.KFunction
import kotlin.test.assertEquals

class KotlinMappingTests {

    @Test
    fun `on arrayList map is quite a lot slower than indexed access`() {
        assertThat(KotlinMapping::baseline_indexed_arrayList, probablyFasterByBetween(KotlinMapping::map_arrayList, 0.2, 0.3))
    }

    @Test
    fun spliteratorMap() {
        val list = listOf("hello", "world")
        assertEquals(list.map { it.length }, list.spliteratorMap { it.length })
    }

}

fun probablyFasterByBetween(reference: KFunction<*>, minFactor: Double, maxFactor: Double) =
    probablyFasterThan(reference, byAFactorOf = minFactor) and !probablyFasterThan(reference, byAFactorOf = maxFactor)

