package com.oneeyedmen.kostings

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterDataTests {

    @Test
    fun `does not fail consistent data` () {
        val data = (0..200).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})
        assertFalse { hasAnomalousData(data) }
    }

    @Test
    fun `should fail with step in middle of data` () {
        val data = (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})+
                (0..5).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(4.0,5.0,4.0)})+
                (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})
        assertTrue { hasAnomalousData(data) }
    }

    @Test
    fun `should not fail after rejecting anomalous data` () {
        val data = (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})+
                (0..5).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(4.0,5.0,4.0)})+
                (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})
        val filteredData = rejectAnomalousData(data)
        assertFalse { hasAnomalousData(filteredData) }
    }

    @Test(expected = RejectingTooManyValues::class)
    fun `should fail if rejecting too many values` () {
        val data = (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})+
                (0..5).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(4.0,5.0,4.0)})+
                (0..100).fold( doubleArrayOf(1.0,2.0,3.0), {a,b -> a + doubleArrayOf(1.0,2.0,3.0)})
        val filteredData = rejectAnomalousData(data, maximumRejectionFactor = 0.01)
        assertFalse { hasAnomalousData(filteredData) }
    }

}