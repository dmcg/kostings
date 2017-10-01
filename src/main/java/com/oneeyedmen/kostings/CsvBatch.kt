package com.oneeyedmen.kostings

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File

fun Batch.writeCSV(file: File) {
    file.bufferedWriter(Charsets.UTF_8).use { writer ->
        val printer = CSVPrinter(writer, CSVFormat.EXCEL)
        printer.printRecord("Benchmark", "Mode", "Samples", "Score", "Score Error (99.9%)", "Unit")
        results.forEach {
            printer.printRecord(it.benchmarkName, it.mode, it.samplesCount.toString(), it.score.toString(), it.error.toString(), it.units)
        }
    }
}

fun JsonBatch.writeSamplesCSV(file: File) {
    file.bufferedWriter(Charsets.UTF_8).use { writer ->
        val printer = CSVPrinter(writer, CSVFormat.EXCEL)
        printer.printRecord(*results.map { it.benchmarkName }.toTypedArray())
        (0 until results.first().samplesCount)
            .map { i -> results.map { it.getSample(i) }.toTypedArray() }
            .forEach { printer.printRecord(*it) }
    }
}