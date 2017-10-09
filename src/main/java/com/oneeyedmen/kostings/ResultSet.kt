package com.oneeyedmen.kostings

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File


interface ResultSet {
    val description: String
    val results: List<Result>

    fun writeStatsCSV(file: File) {
        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            val printer = CSVPrinter(writer, CSVFormat.EXCEL)
            printer.printRecord("Benchmark", "Mode", "Samples", "Score", "Score Error (99.9%)", "Unit")
            results.forEach {
                printer.printRecord(it.benchmarkName, it.mode, it.samplesCount.toString(), it.score.toString(), it.error_999.toString(), it.units)
            }
        }
    }
}