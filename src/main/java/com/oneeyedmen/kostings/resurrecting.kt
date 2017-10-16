package com.oneeyedmen.kostings

import java.io.File
import java.io.FilenameFilter

fun readBatches(dir: File): Sequence<Batch> = dir
    .listFiles(isAResultFile)
    .asSequence()
    .mapNotNull { Batch.readFromJson(it) }

fun readResults(dir: File): Sequence<IndividualBenchmarkResult> = readBatches(dir).flatMap { it.results.asSequence() }

private val isAResultFile: FilenameFilter = FilenameFilter { dir, name ->
    name.endsWith(".json") && dir.resolve(name).length() >= 0 // aborted runs leave behind 0 length files
}
