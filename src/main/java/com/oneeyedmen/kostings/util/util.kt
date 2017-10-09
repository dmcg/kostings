package com.oneeyedmen.kostings.util


fun <T> Iterable<T>.inBatchesOf(batchSize: Int): List<List<T>> =
    withIndex() // create index value pairs
        .groupBy { it.index / batchSize }   // create grouping index
        .map { it.value.map { it.value } }   // split into different partitions


fun DoubleArray.inBatchesOf(batchSize: Int): List<DoubleArray> =
    asList().inBatchesOf(batchSize).map { it.toDoubleArray() }
