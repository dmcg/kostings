package com.oneeyedmen.kostings

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.math.BigDecimal
import java.nio.charset.Charset

data class Batch(val options: Options, val csvFile: File, val results: List<Result>)

fun readBatch(options: Options, csvFile: File): Batch {
    return csvFile.reader(Charset.defaultCharset()).use { reader ->
        Batch(options, csvFile,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).map { it.toResult() }
        )
    }
}

private fun CSVRecord.toResult() = Result(
    benchmarkName = this["Benchmark"],
    mode = this["Mode"],
    samples = this["Samples"].toInt(),
    score = this["Score"].toBigDecimal()!!,
    error = this["Score Error (99.9%)"].toBigDecimal(),
    units = this["Unit"]

)

private fun String.toBigDecimal() = if (this == "NaN") null else BigDecimal(this)

