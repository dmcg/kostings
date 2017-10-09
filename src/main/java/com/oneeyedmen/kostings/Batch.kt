package com.oneeyedmen.kostings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.io.File
import java.io.IOException

/**
 * Represents the results of a single invocation of JMH, which might run multiple benchmarks (as governed by the pattern
 * in batchOptions), but collects all the results into a single JSON datafile.
 */
data class Batch(
    val batchOptions: BatchOptions,
    val dataFile: File,
    override val results: List<IndividualBenchmarkResult>
) : ResultSet
{
    companion object {
        private val objectMapper = jacksonObjectMapper()

        fun readFromJson(jsonFile: File): Batch? {
            val batchOptions = BatchOptions.fromFilename(jsonFile.name) ?: return null
            val results = objectMapper.readTree(jsonFile)?.asIterable()?.map { it.toResult() } ?:
                throw IOException("Can't read $jsonFile as JSON")
            return Batch(batchOptions, jsonFile, results)
        }
    }

    override val description get() = batchOptions.outputFilename

    fun writeSamplesCSV(file: File) {
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
