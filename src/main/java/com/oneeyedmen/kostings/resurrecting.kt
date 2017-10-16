package com.oneeyedmen.kostings

import java.io.File

fun readBatches(dir: File): Sequence<Batch> = dir
    .listFiles { dir, name ->
        dir.resolve(name).isAResultFile()
    }
    .asSequence()
    .mapNotNull { Batch.readFromJson(it) }

fun readResults(dir: File): Sequence<IndividualBenchmarkResult> = readBatches(dir).flatMap { it.results.asSequence() }

fun File.isAResultFile() = name.endsWith(".json") && length() >= 0 // aborted runs leave behind 0 length files
