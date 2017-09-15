package com.oneeyedmen.kostings

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.math.BigDecimal
import java.nio.charset.Charset

data class CsvBatch(
    override val batchOptions: BatchOptions,
    override val dataFile: File,
    override val results: List<Result>)
: Batch {

    override val summaryCsvFile = dataFile
}

fun readBatchFromCsv(batchOptions: BatchOptions, csvFile: File): Batch =
    csvFile.reader(Charset.defaultCharset()).use { reader ->
        CsvBatch(batchOptions, csvFile,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).map { it.toResult() }
        )
    }

private fun CSVRecord.toResult() = Result(
    benchmarkName = this["Benchmark"],
    mode = this["Mode"],
    samplesCount = this["Samples"].toInt(),
    score = this["Score"].toBigDecimal()!!,
    error = this["Score Error (99.9%)"].toBigDecimal(),
    units = this["Unit"]
)

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
        printer.printRecord(*results.map(Result::benchmarkName).toTypedArray())
        for (i in results.first().samples!!.indices) {
            printer.printRecord(*results.map { it.samples!![i] }.toTypedArray())
        }
    }
}

private fun String.toBigDecimal() = if (this == "NaN") null else BigDecimal(this)