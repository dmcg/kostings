package com.oneeyedmen.kostings

import java.io.File

class Results(dir: File) {

    private val resultsByName: Map<String, CompositeResult> = readResults(dir)
        .groupBy { it.benchmarkName }
        .mapValues { entry -> CompositeResult(entry.value) }

    fun resultNamed(benchmarkName: String): CompositeResult? = resultsByName[benchmarkName]

    val allResults: Collection<CompositeResult> get() = resultsByName.values
}