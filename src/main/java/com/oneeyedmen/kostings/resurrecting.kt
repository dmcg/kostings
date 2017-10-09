package com.oneeyedmen.kostings

import java.io.File

fun readBatches(dir: File): Sequence<Batch> = dir
    .listFiles { _, name -> name.endsWith(".json") }
    .asSequence()
    .mapNotNull { Batch.readFromJson(it) }

fun readResults(dir: File): Sequence<IndividualBenchmarkResult> = readBatches(dir).flatMap { it.results.asSequence() }

