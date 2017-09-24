package com.oneeyedmen.kostings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.io.File
import java.io.IOException

data class JsonBatch(
    override val batchOptions: BatchOptions,
    override val dataFile: File,
    override val results: List<Result>)
    : Batch {

    override val summaryCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".csv").apply {
            writeCSV(this)
        }
    }

    val samplesCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".samples.csv").apply {
            writeSamplesCSV(this)
        }
    }
}

fun readBatchFromJson(batchOptions: BatchOptions, jsonFile: File): Batch {
    val results = jacksonObjectMapper().readTree(jsonFile)?.asIterable()?.map { it.toResult() } ?: throw IOException("Can't read $jsonFile as JSON")
    return JsonBatch(batchOptions, jsonFile, results)
}

private fun JsonNode.toResult(): Result {
    val allSampleNodes = this["primaryMetric"]["rawData"].asIterable().asIterable().flatten()
    return Result(
        benchmarkName = this["benchmark"].asText(),
        mode = this["mode"].asText(),
        units = this["primaryMetric"]["scoreUnit"].asText(),
        stats = allSampleNodes.collectToStats()
    )
}

private fun Iterable<JsonNode>.collectToStats(): DescriptiveStatistics = fold(DescriptiveStatistics()) { stats, node ->
    stats.addValue(node.doubleValue())
    stats
}
