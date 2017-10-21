package costOfKotlin

import com.natpryce.hamkrest.assertion.assertThat
import com.oneeyedmen.kostings.probablyFasterThan
import costOfKotlin.mapping.KotlinMapping
import costOfKotlin.mapping.spliteratorMap
import org.junit.Test
import kotlin.test.assertEquals

class KotlinMappingTests {

    @Test
    fun `on arrayList map is quite a lot slower than indexed access`() {
        assertThat(KotlinMapping::baseline_indexed_arrayList,
            probablyFasterThan(KotlinMapping::map_arrayList, byMoreThan = 0.3, butNotMoreThan = 0.4))
    }

    @Test
    fun spliteratorMap() {
        val list = listOf("hello", "world")
        assertEquals(list.map { it.length }, list.spliteratorMap { it.length })
    }

}