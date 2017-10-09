package com.oneeyedmen.kostings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.io.File
import java.io.IOException

data class Batch(
    val batchOptions: BatchOptions,
    val dataFile: File,
    val results: List<IndividualBenchmarkResult>)
{
    companion object {
        private val objectMapper = jacksonObjectMapper()

        fun readFromJson(batchOptions: BatchOptions, jsonFile: File): Batch {
            val results = objectMapper.readTree(jsonFile)?.asIterable()?.map { it.toResult() } ?: throw IOException("Can't read $jsonFile as JSON")
            return Batch(batchOptions, jsonFile, results)
        }
    }

    val summaryCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".csv").apply {
            writeCSV(this)
        }
    }

    val samplesCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".samples.csv").apply {
            writeSamplesCSV(this)
        }
    }

    private fun writeCSV(file: File) {
        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            val printer = CSVPrinter(writer, CSVFormat.EXCEL)
            printer.printRecord("Benchmark", "Mode", "Samples", "Score", "Score Error (99.9%)", "Unit")
            results.forEach {
                printer.printRecord(it.benchmarkName, it.mode, it.samplesCount.toString(), it.score.toString(), it.error_999.toString(), it.units)
            }
        }
    }

    private fun writeSamplesCSV(file: File) {
        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            val printer = CSVPrinter(writer, CSVFormat.EXCEL)
            printer.printRecord(*results.map { it.benchmarkName }.toTypedArray())
            (0 until results.first().samplesCount)
                .map { i -> results.map { it.getSample(i) }.toTypedArray() }
                .forEach { printer.printRecord(*it) }
        }
    }
}

private fun JsonNode.toResult(): IndividualBenchmarkResult {
    val allSampleNodes = this["primaryMetric"]["rawData"].asIterable().asIterable().flatten()
    return IndividualBenchmarkResult(
        benchmarkName = this["benchmark"].asText(),
        mode = this["mode"].asText(),
        units = this["primaryMetric"]["scoreUnit"].asText(),
        data = allSampleNodes.collectToStats()
    )
}

private fun Iterable<JsonNode>.collectToStats(): DescriptiveStatistics = fold(DescriptiveStatistics()) { stats, node ->
    stats.addValue(node.doubleValue())
    stats
}
